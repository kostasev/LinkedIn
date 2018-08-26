package com.linkedin.controller;

import com.google.gson.Gson;
import com.linkedin.db.DBConnector;
import com.linkedin.pojos.MyToken;
import com.linkedin.pojos.Search;
import com.linkedin.security.Authenticator;
import com.linkedin.utilities.ResultSetToJsonMapper;
import org.json.JSONArray;

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


@Path("network")
public class NetworkController {

    @Path("connect")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response connect (String json) throws IOException, SQLException {
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
        if (isARequest(auth.getUserid(),token.getId())==-1){
            System.out.println("this is a new request");
            return addRequest(auth.getUserid(),token.getId());
        } else if (isARequest(auth.getUserid(),token.getId())==0){
            System.out.println("this is an old request");
            if (gotRequest(token.getId(),auth.getUserid())){
                System.out.println("this is received req");
               return addConnection(token.getId(),auth.getUserid());
            }
        }
        return Response.ok().build();
    }

    private Response addConnection(int id, int userid) throws IOException, SQLException {
        PreparedStatement pSt = null;
        Connection con = DBConnector.getInstance().getConnection();
        ResultSet rs = null;
        try {
            String addConnection = "UPDATE connection  SET " +
                    "status = 1 " +
                    "WHERE iduser1 = ? AND iduser2 = ? ";
            pSt = con.prepareStatement(addConnection);
            pSt.setInt(1, id);
            pSt.setInt(2, userid);
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

    private boolean gotRequest(int id, int userid)  throws IOException, SQLException {
        PreparedStatement pSt = null;
        Connection con = null;
        ResultSet rs = null;
        try {
            con = DBConnector.getInstance().getConnection();
            rs = null;
            String getConnectionStatus = "select * " +
                    "from  connection " +
                    "where iduser1 = ?  and iduser2 = ?" ;
            pSt = con.prepareStatement(getConnectionStatus);
            pSt.setInt(1, id);
            pSt.setInt(2, userid);
            rs = pSt.executeQuery();
            if (!rs.next()) {
                System.out.println("ResultSet is empty!");
                return false;
            } else {
                System.out.println(rs.getBoolean("status"));
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        } finally {
            try {
                con.close();
                rs.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    private Response addRequest(int userid, int id) throws IOException, SQLException {
        PreparedStatement pSt = null;
        Connection con = DBConnector.getInstance().getConnection();
        ResultSet rs = null;
        try {
            String addRequest = "INSERT INTO connection " +
                    "(iduser1,iduser2,status) " +
                    "values " +
                    "(?,?,0)";
            pSt = con.prepareStatement(addRequest);
            pSt.setInt(1, userid);
            pSt.setInt(2, id);
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

    private int isARequest(int userid, int id) throws IOException {
        PreparedStatement pSt = null;
        Connection con = null;
        ResultSet rs = null;
        try {
            con = DBConnector.getInstance().getConnection();
            rs = null;
            String getConnectionStatus = "select * " +
                    "from (select * from connection " +
                    "where iduser1 = ? " +
                    "union " +
                    "select a.iduser2 as iduser1 , a.iduser1 as iduser2, status " +
                    "from connection a " +
                    "where iduser2 = ? ) as tb " +
                    "where tb.iduser2 = ? ";
            pSt = con.prepareStatement(getConnectionStatus);
            pSt.setInt(1, userid);
            pSt.setInt(2, userid);
            pSt.setInt(3, id);
            rs = pSt.executeQuery();
            if (!rs.next()) {
                System.out.println("ResultSet is empty!");
                return -1;
            } else {
                System.out.println(rs.getBoolean("status"));
                if (rs.getBoolean("status") == true) return 1;
                else return 0 ;
            }
        }catch (SQLException e){
            e.printStackTrace();
            return -2;
        } finally {
            try {
                con.close();
                rs.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    @Path("delconnect")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delConnect (String json) throws IOException, SQLException {
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
        if ( deleteConnection(auth.getUserid(),token.getId())) {
            System.out.println("AAAA" + auth.getUserid() + token.getId());
        }
        else {
            deleteConnection(token.getId(), auth.getUserid());
            System.out.println("BBBB " + token.getId() + auth.getUserid());
        }
        return Response.ok().build();
    }

    private boolean deleteConnection(int id, int userid) throws IOException, SQLException {
        PreparedStatement pSt = null;
        Connection con = DBConnector.getInstance().getConnection();
        ResultSet rs = null;
        try {
            String delSkill = "DELETE  FROM connection " +
                    "WHERE iduser1 = ? AND iduser2 = ?";
            pSt = con.prepareStatement(delSkill);
            pSt.setInt(1, id);
            pSt.setInt(2, userid);
            int a = pSt.executeUpdate();
            System.out.println("return value:" + a);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (isARequest(id,userid)==-1)
            return true;
        else
            return false;
    }

    @Path("connections")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getSkills(String json) throws IOException, SQLException {
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
        String allConns = "select iduser as id, iduser , name ,surname , email  from " +
                        "(select * from connection where iduser1 = ? " +
                        " union select b.iduser2 as iduser1 , b.iduser1 as iduser2, status " +
                        " from connection b where iduser2 = ? ) as a " +
                        "inner join user where status=1 and iduser=a.iduser2 ";
        try {
            pSt = con.prepareStatement(allConns);
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

    @Path("search")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getSearch(String json) throws IOException, SQLException {
        Gson gson = new Gson();
        Search user = gson.fromJson(json, Search.class);
        Authenticator auth = new Authenticator(user.getToken());
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
        String srcConns = "select iduser , name , surname , email " +
                "from user where name = ? or surname = ? " +
                "and name=? or surname = ? ";
        try {
            pSt = con.prepareStatement(srcConns);
            pSt.setString(1, user.getName());
            pSt.setString(2, user.getSurname());
            pSt.setString(3, user.getSurname());
            pSt.setString(4, user.getName());
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


