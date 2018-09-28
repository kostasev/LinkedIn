package com.linkedin.controller;

import com.google.gson.Gson;
import com.linkedin.db.DBConnector;
import com.linkedin.pojos.MyToken;
import com.linkedin.security.Authenticator;
import com.linkedin.utilities.ResultSetToJsonMapper;
import org.json.JSONArray;

import javax.json.Json;
import javax.json.JsonObject;
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

@Path("notification")
public class NotifyController {

    @Path("getall")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getRequests(String json) throws IOException, SQLException {
        Gson gson = new Gson();
        MyToken token = gson.fromJson(json, MyToken.class);
        Authenticator auth = new Authenticator(token.getToken());
        try {
            auth.authenticate(auth.getToken());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        PreparedStatement pSt = null;
        Connection con = DBConnector.getInstance().getConnection();
        ResultSet rs = null;
        String myPosts = null;
        myPosts = "select iduser1 , name , surname from " +
                "connection ,user " +
                "where iduser2 = ? and status = 0 and iduser1=user.iduser; ";
        try {
            pSt = con.prepareStatement(myPosts);
            pSt.setInt(1, auth.getUserid());
            rs = pSt.executeQuery();
            if (!rs.next()) {
                return Response.ok("empty skills").build();
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

    @Path("accept")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response acceptConn(String json) throws IOException, SQLException {
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
        try {
            String addPost = "UPDATE connection SET " +
                    "status = 1 " +
                    "WHERE iduser1 = ? and iduser2 = ?  ";
            pSt = con.prepareStatement(addPost);
            pSt.setInt(1, token.getId());
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


    @Path("delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response applyJob(String json) throws IOException, SQLException {
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
        try {
            String addPost = "DELETE  FROM connection " +
                    "WHERE iduser1 = ? and iduser2 = ? ";
            pSt = con.prepareStatement(addPost);
            pSt.setInt(1, token.getId());
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


    @Path("getnotifs")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getNotifs(String json) throws IOException, SQLException {
        Gson gson = new Gson();
        MyToken token = gson.fromJson(json, MyToken.class);
        Authenticator auth = new Authenticator(token.getToken());
        try {
            auth.authenticate(auth.getToken());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        PreparedStatement pSt = null;
        Connection con = DBConnector.getInstance().getConnection();
        ResultSet rs = null;
        String myPosts = null;
        myPosts = "select name , surname , user.iduser , post.idpost, seen, type  from user, post, " +
                "(select idpost ,iduser,seen,ifnull(null,\"like\") as type  from likes " +
                "union( " +
                "select idpost ,author as iduser ,seen,ifnull(null,\"comment\") as type from comments ) ) as ev " +
                "where user.iduser=ev.iduser and ev.idpost=post.idpost and post.author=? and ev.iduser<>?";
        try {
            pSt = con.prepareStatement(myPosts);
            pSt.setInt(1, auth.getUserid());
            pSt.setInt(2, auth.getUserid());
            rs = pSt.executeQuery();
            if (!rs.next()) {
                return Response.ok("empty skills").build();
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



    @Path("seen")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setSeen(String json) throws IOException, SQLException {
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
        try {
            String addPost = "UPDATE comments cm , post ps " +
                    "SET cm.seen = 1 " +
                    "WHERE cm.idpost=ps.idpost and ps.author = ? ";
            pSt = con.prepareStatement(addPost);
            pSt.setInt(1, auth.getUserid());
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
        setLikesSeen(auth.getUserid());
        return Response.ok().build();
    }

    private void setLikesSeen(int userid) throws IOException, SQLException {
        PreparedStatement pSt = null;
        Connection con = DBConnector.getInstance().getConnection();
        try {
            String addPost = "UPDATE likes lk , post ps " +
                    "SET lk.seen = 1 " +
                    "WHERE lk.idpost=ps.idpost and ps.author = ? ";
            pSt = con.prepareStatement(addPost);
            pSt.setInt(1, userid);
            pSt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    @Path("notif")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotif(String json) throws IOException, SQLException {
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
        try {
            String addPost = "select cm.seen from user as us, post as ps , likes as lk, comments as cm " +
                    "where ( us.iduser = ps.author and us.iduser= ? and ps.idpost = cm.idpost and cm.seen=0) " +
                    "union " +
                    "select lk.seen from user as us, post as ps , likes as lk, comments as cm " +
                    "where ( us.iduser = ps.author and us.iduser= ? and ps.idpost = lk.idpost and lk.seen=0 ) ";
            pSt = con.prepareStatement(addPost);
            pSt.setInt(1, auth.getUserid());
            pSt.setInt(2, auth.getUserid());
            rs = pSt.executeQuery();
            if (!rs.next()) {
                JsonObject tokenJson = Json.createObjectBuilder()
                        .add("answer", "no")
                        .build();
                return Response.ok(tokenJson).build();
            } else {
                JsonObject tokenJson = Json.createObjectBuilder()
                        .add("answer", "yes")
                        .build();
                return Response.ok(tokenJson).build();
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
