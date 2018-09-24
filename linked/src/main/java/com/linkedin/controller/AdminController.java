package com.linkedin.controller;

import com.google.gson.Gson;
import com.linkedin.db.DBConnector;
import com.linkedin.pojos.MyToken;
import com.linkedin.security.Authenticator;
import com.linkedin.utilities.ResultSetToJsonMapper;
import com.linkedin.utilities.XmlBuilder;
import org.json.JSONArray;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;

import com.jamesmurty.utils.XMLBuilder;

import java.sql.*;
import java.util.Properties;

@Path("admin")
public class AdminController {

    @Path("users")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUsers(String json) throws IOException, SQLException {
        Gson gson = new Gson();
        MyToken token = gson.fromJson(json, MyToken.class);
        Authenticator auth = new Authenticator(token.getToken());
        try {
            auth.authenticate(auth.getToken());
            System.out.println("Authenticated Token with user type " + auth.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        if ( auth.getUserid()!= 1 ) return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        PreparedStatement pSt = null;
        Connection con = DBConnector.getInstance().getConnection();
        ResultSet rs = null;
        String allConns = "select iduser,name,surname,email,birthday,phone from user";
        try {
            pSt = con.prepareStatement(allConns);
            rs = pSt.executeQuery();
            if (!rs.next()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                JSONArray jarray = ResultSetToJsonMapper.mapResultSet(rs);
                System.out.println(jarray.get(0));
                return Response.ok(jarray.toString()).build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } finally {
            try {
                con.close();
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Path("getxml")
    @POST
    public Response getXmlFile(String json) throws IOException, SQLException {
        Gson gson = new Gson();
        MyToken token = gson.fromJson(json, MyToken.class);
        Authenticator auth = new Authenticator(token.getToken());
        try {
            auth.authenticate(auth.getToken());
            System.out.println("Authenticated Token with user type " + auth.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        if ( auth.getUserid()!= 1 ) return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();


        Connection con      = null;

        ResultSet user     = null,
                post       = null,
                comments   = null,
                likes      = null;
        try {
            con = DBConnector.getInstance().getConnection();
            String query = "SELECT * FROM user";
            Statement userSt = con.createStatement();
            user = userSt.executeQuery(query);


            query = "SELECT * FROM post";
            Statement houseSt = con.createStatement();
            post = houseSt.executeQuery(query);


            query = "SELECT * FROM comments";
            Statement commentSt = con.createStatement();
            comments = commentSt.executeQuery(query);


            query = "SELECT * FROM likes";
            Statement mesSt = con.createStatement();
            likes = mesSt.executeQuery(query);


            XMLBuilder xmlBuilder = XmlBuilder.getXml(user, post, comments, likes);
            if (xmlBuilder == null) {
                throw new NullPointerException("XMLBuilder is null");
            }

            Properties outputProperties = new Properties();
            outputProperties.put(javax.xml.transform.OutputKeys.METHOD, "xml");
            PrintWriter writer = new PrintWriter(new FileOutputStream("C:/Users/Public/ProfPictures/" + "/exp.xml"));
            xmlBuilder.toWriter(writer, outputProperties);


            File file = new File("C:/Users/Public/ProfPictures/" + "/exp.xml");
            Response.ResponseBuilder response = Response.ok((Object) file).header("content-type", "text/xml");
            return response.build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } finally {
            con.close();

        }
    }

}
