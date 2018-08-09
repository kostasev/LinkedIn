package com.linkedin.controller;

import com.linkedin.db.DBConnector;
import com.linkedin.pojos.SignUser;
import com.linkedin.pojos.UserCredentials;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.*;

import java.io.IOException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;

import java.text.ParseException;

import java.util.Calendar;
import java.util.Date;

import java.security.Key;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;


@Path("user")
public class UserController {

    public static final Key key = MacProvider.generateKey();

    private class UserInfo {
        int id;
        String accountType;
    }

    @Path("login")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response logUser(UserCredentials user) {
        try {
            System.out.println("Got mail " + user.getEmail() + " and Pass " + user.getPass());
            UserInfo authUser = authenticateUser(user.getEmail(), user.getPass());
            if (authUser == null) {
                System.out.println("There is not a user with given credentials");
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            String token = createToken(authUser);
            System.out.println("User Logged in succesfully\n");
            return Response.ok(token).build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String createToken(UserInfo user) {
        long expMillis = System.currentTimeMillis() + 3600000;
        JwtBuilder builder = Jwts.builder()
                .claim("id", String.valueOf(user.id))
                .claim("role", user.accountType)
                .setExpiration(new Date(expMillis))
                .signWith(SignatureAlgorithm.HS512, key);

        return builder.compact();
    }


    public UserInfo authenticateUser(String email, String pass) throws SQLException {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        UserInfo user = new UserInfo();
        String findUserSql = "SELECT iduser, pass, role " +
                "FROM user " +
                "WHERE email = ? LIMIT 1";
        try{
            con =  DBConnector.getInstance().getConnection();
            ps  = con.prepareStatement(findUserSql);
            ps.setString(1,email);
            rs  = ps.executeQuery();
            if(!rs.next()){
                System.out.println("ResultSet is empty!");
                return null ;
            }
            else {
                if (encoder.matches(pass,rs.getString("pass"))){
                    user.id = Integer.parseInt(rs.getString("iduser"));
                    user.accountType = rs.getString("role");
                    return user;
                }
                else return null ;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally{
            try {
                con.close();
                rs.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    @Path("signup")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response signUser(SignUser user ) throws SQLException, IOException, ParseException {
        System.out.println(user.getEmail()+
                user.getName()+
                user.getSurname()+
                user.getPass()+
                user.getRepass()+
                user.getEmail()+
                user.getBirthday());

        if (user.getPass().equals(user.getRepass())){
            System.out.println("Password ok");
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            user.setPass(encoder.encode(user.getPass()));
        }
        else{
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        try {
            System.out.println("Email ok");
            InternetAddress emailAddr = new InternetAddress(user.getEmail());
            emailAddr.validate();
        } catch (AddressException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        //LocalDate date = LocalDate.parse(user.getBirthday());
        String date = user.getBirthday().substring(0,4);
        int year = Integer.parseInt(date);
        if( (Calendar.getInstance().get(Calendar.YEAR)-year)<18){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        System.out.println("Bday ok"+(Calendar.getInstance().get(Calendar.YEAR)-year)+"\n"+
                Calendar.getInstance().get(Calendar.YEAR)+"\n"+
                year);
        Connection con = null;
        String insertUser = "INSERT INTO user "+
                "(name,surname,email,pass,birthday,role) "+
                "values "+
                "(?,?,?,?,?,\"user\")";
        try{
            con = DBConnector.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(insertUser);
            ps.setString(1,user.getName());
            ps.setString(2,user.getSurname());
            ps.setString(3,user.getEmail());
            ps.setString(4,user.getPass());
            ps.setString(5,user.getBirthday());
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }finally{
            try {
                con.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        return Response.ok().build();
    }
}



