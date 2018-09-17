package com.linkedin.controller;

import com.google.gson.Gson;
import com.linkedin.db.DBConnector;
import com.linkedin.pojos.Job;
import com.linkedin.pojos.MyToken;
import com.linkedin.pojos.Post;
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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@Path("jobs")
public class JobController {

    @Path("new")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createJob(String json) throws IOException, SQLException {
        Gson gson = new Gson();
        Job newJob = gson.fromJson(json, Job.class);
        Authenticator auth = new Authenticator(newJob.getToken());
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
            String addPost = "INSERT INTO job " +
                    "(idauthor,title,visible,state,descr) " +
                    "values " +
                    "(?,?,?,1,?)";
            pSt = con.prepareStatement(addPost);
            pSt.setInt(1, auth.getUserid());
            pSt.setString(2, newJob.getTitle());
            pSt.setString(3, newJob.getVisible());
            pSt.setString(4, newJob.getDescr());
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
        int idjob = getJobId(newJob,auth.getUserid());
        if(idjob==0){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        if(addSkills(newJob.getSkills(),idjob)==false){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.ok().build();
    }

    private boolean addSkills(String skills,int idjob) throws IOException, SQLException {
        List<String> skillList = Arrays.asList(skills.split(","));
        for (int i=0;i<skillList.size();i++){
            PreparedStatement pSt = null;
            Connection con = DBConnector.getInstance().getConnection();
            try {
                String addSkill = "INSERT INTO desired_skill " +
                        "(name,job_idjobs) " +
                        "values " +
                        "(?,?)";
                pSt = con.prepareStatement(addSkill);
                pSt.setString(1, skillList.get(i));
                pSt.setInt(2, idjob);
                pSt.executeUpdate();
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
        }
        return true;
    }

    private int getJobId(Job newJob, int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String myPost = "SELECT idjobs " +
                "FROM job " +
                "WHERE idauthor = ? AND " +
                "title = ? LIMIT 1";
        try {
            con = DBConnector.getInstance().getConnection();
            ps = con.prepareStatement(myPost);
            ps.setInt(1, id);
            ps.setString(2, newJob.getTitle());
            rs = ps.executeQuery();
            if (!rs.next()) {
                System.out.println("ResultSet is empty!");
                return 0;
            } else {
                return rs.getInt("idjobs" );
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            try {
                con.close();
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Path("myjobs")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getMyJobs(String json) throws IOException, SQLException {
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
        if (token.getId()==4) {
            myPosts = "select * from job where idauthor = ? limit 4 ";
        } else {
            myPosts = "select  * from job where idauthor = ? ";
        }
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


    @Path("skills")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getSkills(String json) throws IOException, SQLException {
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
        String myPosts = "select  * from desired_skill where job_idjobs=?";
        try {
            pSt = con.prepareStatement(myPosts);
            pSt.setInt(1, token.getId());
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


    @Path("applicants")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAplls(String json) throws IOException, SQLException {
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
        String myPosts = "select  idjob,user.iduser,name,surname " +
                "from candidate, user " +
                "where idjob=? and user.iduser=candidate.iduser;";
        try {
            pSt = con.prepareStatement(myPosts);
            pSt.setInt(1, token.getId());
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

    @Path("delete")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response delJob(String json) throws IOException, SQLException {
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
            String delSkill = "DELETE  FROM job " +
                    "WHERE idjobs = ? AND idauthor = ?";
            pSt = con.prepareStatement(delSkill);
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
}
