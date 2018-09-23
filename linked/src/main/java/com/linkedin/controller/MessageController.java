package com.linkedin.controller;


import com.google.gson.Gson;
import com.linkedin.db.DBConnector;
import com.linkedin.pojos.Message;
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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@Path("messages")
public class MessageController {

    @Path("getchats")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getMyChats(String json) throws IOException, SQLException {
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
        myPosts = "select name, surname, user.iduser , us.idchat , max(datetime) as datetime from user_has_chat as us, user, " +
                "(select idchat from user_has_chat where iduser = ? group by idchat) as ch " +
                "where us.iduser<>? and us.idchat=ch.idchat and user.iduser=us.iduser group by iduser order by datetime desc;";
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

    @Path("getchatid")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getChatId(String json) throws IOException, SQLException {
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
        myPosts = "select name, surname, user.iduser , us.idchat , max(datetime) as datetime from user_has_chat as us, user, " +
                "(select idchat from user_has_chat where iduser = ? group by idchat) as ch " +
                "where us.iduser<>? and us.idchat=ch.idchat and user.iduser=us.iduser and us.iduser= ? group by iduser order by datetime desc;";
        try {
            pSt = con.prepareStatement(myPosts);
            pSt.setInt(1, auth.getUserid());
            pSt.setInt(2, auth.getUserid());
            pSt.setInt(3, token.getId());
            rs = pSt.executeQuery();
            if (!rs.next()) {
                return Response.ok("empty skills").build();
            } else {
                JsonObject tokenJson = Json.createObjectBuilder()
                        .add("id",rs.getInt("idchat"))
                        .add("name",rs.getString("name"))
                        .add("surname",rs.getString("surname"))
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


    @Path("getchat")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getChat(String json) throws IOException, SQLException {
        Gson gson = new Gson();
        MyToken token = gson.fromJson(json, MyToken.class);
        Authenticator auth = new Authenticator(token.getToken());
        int chatid;
        try {
            auth.authenticate(auth.getToken());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        if ((chatid = chatExists(token.getId(),auth.getUserid()) ) > 0){

        } else {
            chatid = createChat();
            postMessage(chatid,token.getId());
            postMessage(chatid,auth.getUserid());
        }
        PreparedStatement pSt = null;
        Connection con = DBConnector.getInstance().getConnection();
        ResultSet rs = null;
        String myPosts = null;
        myPosts = "select * from user_has_chat where idchat = ? order by datetime ";
        try {
            pSt = con.prepareStatement(myPosts);
            pSt.setInt(1, chatid);
            rs = pSt.executeQuery();
            if (!rs.next()) {
                return Response.ok("empty message").build();
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

    @Path("postmessage")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postText(String json ) throws IOException, SQLException {
        Gson gson = new Gson();
        Message newMsg = gson.fromJson(json, Message.class);
        Authenticator auth = new Authenticator(newMsg.getToken());
        try {
            auth.authenticate(auth.getToken());
            System.out.println("Authenticated Token with user type " + auth.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        PreparedStatement pSt = null;
        Connection con = DBConnector.getInstance().getConnection();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        try {
            String addPost = "INSERT INTO user_has_chat " +
                    "(iduser,idchat,text,datetime,seen) " +
                    "values " +
                    "(?,?,?,?,0)";
            pSt = con.prepareStatement(addPost);
            pSt.setInt(1, auth.getUserid());
            pSt.setInt(2, newMsg.getIduser());
            pSt.setString(3, newMsg.getText());
            pSt.setString(4,timeStamp);
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

    private int createChat() throws IOException, SQLException {
        PreparedStatement pSt = null;
        Connection con = DBConnector.getInstance().getConnection();
        ResultSet rs = null;
        try {
            String addPost = "INSERT INTO chat " +
                    "value " +
                    "()";
            pSt = con.prepareStatement(addPost);
            pSt.executeUpdate();
            String getchatid = "select max(idchat) as idchat from chat ";
            pSt = con.prepareStatement(getchatid);
            rs = pSt.executeQuery();
            if (!rs.next()) {
                return -1;
            } else {
                return rs.getInt("idchat");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    private void postMessage( int chatid ,int userid ) throws IOException, SQLException {
        PreparedStatement pSt = null;
        Connection con = DBConnector.getInstance().getConnection();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        try {
            String addPost = "INSERT INTO user_has_chat " +
                    "(iduser,idchat,text,datetime,seen) " +
                    "values " +
                    "(?,?,\"\",?,0)";
            pSt = con.prepareStatement(addPost);
            pSt.setInt(1, userid);
            pSt.setInt(2, chatid);
            pSt.setString(3,timeStamp);
            pSt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ;
    }


    public int chatExists(int iduser1 ,int iduser2) throws IOException, SQLException {
        PreparedStatement pSt = null;
        Connection con = DBConnector.getInstance().getConnection();
        ResultSet rs = null;
        String myPosts = "select name, surname, user.iduser , us.idchat , max(datetime) as datetime from user_has_chat as us, user, " +
                "(select idchat from user_has_chat where iduser = ? group by idchat) as ch " +
                "where us.iduser<>? and us.idchat=ch.idchat and user.iduser=us.iduser and us.iduser= ? group by iduser order by datetime desc; ";
        try {
            pSt = con.prepareStatement(myPosts);
            pSt.setInt(1, iduser1);
            pSt.setInt(2, iduser1);
            pSt.setInt(3, iduser2);
            rs = pSt.executeQuery();
            if (!rs.next()) {
                return -1;
            } else {
                int chatid = rs.getInt("idchat");
                return chatid;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
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
