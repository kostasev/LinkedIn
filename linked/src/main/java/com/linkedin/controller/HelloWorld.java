package com.linkedin.controller;


import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.linkedin.security.Authenticator;
import com.linkedin.db.DBConnector;

import java.sql.Statement;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


@Path("hello")
public class HelloWorld {

    @Path("hello/{token}")
    @GET
    public Response getMsg(@PathParam(value = "token") String token){
        // Create a dog instance

        //DBConnector mydb = new DBConnector();
        System.out.println("Check Location of Print "+ token);

        Authenticator auth = new Authenticator(token);

        try {
            auth.authenticate(token);
            System.out.println("O ROLOS EINAI " + auth.getType());
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }catch (Exception e) {
            e.printStackTrace();
        }

        /*try{
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("MPIKA_______");
            Connection mydb = DriverManager.getConnection(host,uName,uPass);
            Statement myStatement = mydb.createStatement();
            ResultSet myRes = myStatement.executeQuery("select * from user");
            System.out.println("VGIKA_______");
            while(myRes.next()){
                System.out.println("AAAAA");
                System.out.println(myRes.getString(1));
                //return Json.createObjectBuilder().add("firstname","aaaasss").build();
            }
        } catch (SQLException e) {
            System.out.println("CATCHED_____________");
            e.printStackTrace();
        }*/

        //return Json.createObjectBuilder().add("firstname","nikos1").build();
        return Response.ok("AAAAAEEk").build();

    }
}
