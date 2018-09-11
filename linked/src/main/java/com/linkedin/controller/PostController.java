package com.linkedin.controller;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.linkedin.db.DBConnector;
import com.linkedin.pojos.MyToken;
import com.linkedin.pojos.Post;
import com.linkedin.security.Authenticator;
import com.linkedin.utilities.ResultSetToJsonMapper;
import org.json.JSONArray;

import javax.json.Json;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Path("post")
public class PostController {
    @Path("create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPost (String json) throws IOException, SQLException {
        Gson gson = new Gson();
        Post newPost = gson.fromJson(json, Post.class);
        Authenticator auth = new Authenticator(newPost.getToken());
        try {
            auth.authenticate(auth.getToken());
            System.out.println("Authenticated Token with user type " + auth.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        PreparedStatement pSt = null;
        Connection con = DBConnector.getInstance().getConnection();
        ResultSet rs = null;
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        try {
            String addPost = "INSERT INTO post " +
                    "(author,datetime,visible,post) " +
                    "values " +
                    "(?,?,?,?)";
            pSt = con.prepareStatement(addPost);
            pSt.setInt(1, newPost.getIduser());
            pSt.setString(2, timeStamp);
            pSt.setString(3, newPost.getVisible());
            pSt.setString(4, newPost.getPost());
            pSt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        javax.json.JsonObject res = getPostId(newPost);
        return Response.ok(res).build();
    }

    private javax.json.JsonObject getPostId(Post newPost) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String myPost = "SELECT * " +
                "FROM post " +
                "WHERE author = ? AND " +
                "post = ? LIMIT 1";
        try {
            con = DBConnector.getInstance().getConnection();
            ps = con.prepareStatement(myPost);
            ps.setInt(1, newPost.getIduser());
            ps.setString(2, newPost.getPost());
            rs = ps.executeQuery();
            if (!rs.next()) {
                System.out.println("ResultSet is empty!");
                return null;
            } else {
                javax.json.JsonObject ans = Json.createObjectBuilder()
                        .add("idpost",rs.getInt("idpost" ))
                        .add("datetime",rs.getString("datetime"))
                        .build();
                return ans;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                con.close();
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    @Path("like")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response likePost (String json) throws IOException, SQLException {
        Gson gson = new Gson();
        MyToken newPost = gson.fromJson(json, MyToken.class);
        Authenticator auth = new Authenticator(newPost.getToken());
        try {
            auth.authenticate(auth.getToken());
            System.out.println("Authenticated Token with user type " + auth.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        PreparedStatement pSt = null;
        Connection con = DBConnector.getInstance().getConnection();
        ResultSet rs = null;
        try {
            String addLike = "INSERT INTO likes " +
                    "(idpost,iduser,seen)" +
                    "values " +
                    "(?,?,0)";
            pSt = con.prepareStatement(addLike);
            pSt.setInt(1, newPost.getId());
            pSt.setInt(2, auth.getUserid());
            pSt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return Response.ok().build();
    }


    @Path("comments")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getComments(String json) throws IOException, SQLException {
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
        PreparedStatement pSt = null;
        Connection con = DBConnector.getInstance().getConnection();
        ResultSet rs = null;
        String allComms = "select iduser as id, iduser , name ,surname , email  from " +
                "(select * from connection where iduser1 = ? " +
                " union select b.iduser2 as iduser1 , b.iduser1 as iduser2, status " +
                " from connection b where iduser2 = ? ) as a " +
                "inner join user where status=1 and iduser=a.iduser2 ";
        try {
            pSt = con.prepareStatement(allComms);
            pSt.setInt(1, auth.getUserid());
            pSt.setInt(2, auth.getUserid());
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
}
