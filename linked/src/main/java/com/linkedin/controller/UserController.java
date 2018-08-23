package com.linkedin.controller;

import com.google.gson.Gson;
import com.linkedin.db.DBConnector;
import com.linkedin.pojos.*;

import com.linkedin.security.Authenticator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.json.Json;
import javax.json.JsonObject;
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
        String email;
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
            JsonObject tokenJson = Json.createObjectBuilder()
                    .add("token",token)
                    .add("id",authUser.id)
                    .build();
            return Response.ok(tokenJson).build();
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
                .claim("email",user.email)
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
                    user.email = email;
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
        String pass = null;
        if (user.getPass().equals(user.getRepass())){
            pass = user.getPass();
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
        System.out.println("credentials "+ user.getEmail() + " " + user.getPass());
        UserInfo authUser = authenticateUser(user.getEmail(), pass);
        if (authUser == null) {
            System.out.println("There is not a user with given credentials");
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        String token = createToken(authUser);
        JsonObject tokenJson = Json.createObjectBuilder()
                .add("token",token)
                .add("id",authUser.id)
                .build();
        return Response.ok(tokenJson).build();
    }


    @Path("changepass")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response resetPass(String json) throws SQLException {
        Integer id=0;
        Gson gson = new Gson();
        NewPass newpass = gson.fromJson(json, NewPass.class);
        Authenticator auth = new Authenticator(newpass.getToken());
        System.out.println("auth token " + auth.getToken());
        try {
            auth.authenticate(auth.getToken());
            System.out.println("Authenticated Token with user type " + auth.getType());
            id = auth.getUserid();
        }catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        System.out.println("auth getmail "+ auth.getEmail() );
        System.out.println("newpass oldpass "+ newpass.getOldpass() );
        UserInfo authUser = authenticateUser(auth.getEmail(), newpass.getOldpass());
        if (authUser == null) {
            System.out.println("There is not a user with given credentials");
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        newpass.setPass(encoder.encode(newpass.getPass()));

        Connection con = null;
        PreparedStatement pSt = null;
        try {
            con = DBConnector.getInstance().getConnection();
            String update = "UPDATE user SET pass = ? WHERE iduser = ?";
            pSt = con.prepareStatement(update);
            pSt.setString(1, newpass.getPass());
            pSt.setInt(2, id );
            pSt.execute();
            return Response.ok().build();
        } catch ( SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } finally {
            try {
                con.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    @Path("info")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response myInfo( String jsonToken ){
        Gson gson = new Gson();
        MyToken token = gson.fromJson(jsonToken, MyToken.class);
        System.out.println("My token" + token.getToken());
        Authenticator auth = new Authenticator(token.getToken());
        try {
            auth.authenticate(auth.getToken());
            System.out.println("Authenticated Token with user type " + auth.getType());
        }catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        User user = null ;
        if (token.getId()!=0) {
            user = getUserByID(token.getId());
        }else{
            user = getUserByID(auth.getUserid());
            }
        if (user==null){
            System.out.println("uyser is null");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        System.out.println("name" + user.getName() + "\n" +
                "surname" + user.getSurname() + "\n" +
                "email" + user.getEmail() + "\n" +
                "phone" + user.getPhone() + "\n" +
                "birhtday" + user.getBirthday() + "\n");
        if (user.getPhone()==null)
            user.setPhone("0");
        JsonObject tokenJson = Json.createObjectBuilder()
                .add("name",user.getName())
                .add("surname",user.getSurname())
                .add("email",user.getEmail())
                .add("phone",user.getPhone())
                .add("birthday",user.getBirthday())
                .add("id",auth.getUserid())
                .build();
        return Response.ok(tokenJson).build();
    }

    @Path("update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateInfo( String jsonToken ){
        Gson gson = new Gson();
        User user = gson.fromJson(jsonToken, User.class);
        System.out.println("My token" + user.getToken());
        Authenticator auth = new Authenticator(user.getToken());
        try {
            auth.authenticate(auth.getToken());
            System.out.println("Authenticated Token with user type " + auth.getType());
        }catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        Connection con = null;
        PreparedStatement pSt = null;
        try {
            con = DBConnector.getInstance().getConnection();
            String update = "UPDATE user SET name = ?, surname = ? " +
                    ", email = ? , phone = ? , birthday = ? WHERE iduser = ?";
            pSt = con.prepareStatement(update);
            pSt.setString(1, user.getName());
            pSt.setString(2, user.getSurname());
            pSt.setString(3, user.getEmail());
            pSt.setString(4, user.getPhone());
            pSt.setString(5, user.getBirthday());
            pSt.setInt(6, auth.getUserid() );
            pSt.execute();
            return Response.ok().build();
        } catch ( SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("name" + user.getName() + "\n" +
                    "surname" + user.getSurname() + "\n" +
                    "email" + user.getEmail() + "\n" +
                    "phone" + user.getPhone() + "\n" +
                    "birhtday" + user.getBirthday() + "\n");

            return Response.ok().build();
        }
    }

    private User getUserByID(int userid) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        User user = new User();
        String getUserSql = "SELECT name, surname, email , birthday , phone  " +
                "FROM user " +
                "WHERE iduser = ? LIMIT 1";
        try{
            con =  DBConnector.getInstance().getConnection();
            ps  = con.prepareStatement(getUserSql);
            ps.setInt(1,userid);
            rs  = ps.executeQuery();
            if(!rs.next()){
                System.out.println("ResultSet is empty!");
                return null ;
            }
            else {
                user.setName(rs.getString("name"));
                user.setSurname(rs.getString("surname"));
                user.setEmail(rs.getString("email"));
                user.setBirthday(rs.getString("birthday"));
                user.setPhone(rs.getString("phone"));
                return user;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally{
            try {
                con.close();
                rs.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
}





