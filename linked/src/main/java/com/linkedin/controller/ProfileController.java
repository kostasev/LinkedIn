package com.linkedin.controller;

import com.google.gson.Gson;
import com.linkedin.db.DBConnector;
import com.linkedin.pojos.MyToken;
import com.linkedin.pojos.NewPass;
import com.linkedin.security.Authenticator;
import com.linkedin.utilities.ResultSetToJsonMapper;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONArray;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;


@Path("profile")
public class ProfileController {

    @Path("getskills")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getSkills(String json) throws IOException, SQLException  {
        Gson gson = new Gson();
        MyToken token = gson.fromJson(json, MyToken.class);
        Authenticator auth = new Authenticator(token.getToken());
        System.out.println("auth token " + auth.getToken());
        try {
            auth.authenticate(auth.getToken());
            System.out.println("Authenticated Token with user type " + auth.getType());
        }catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        PreparedStatement pSt = null;
        Connection con = DBConnector.getInstance().getConnection();
        ResultSet rs = null;
        if(token.getId()==0) token.setId(auth.getUserid());
        if ((auth.getUserid()==token.getId())||(isConnected(auth.getUserid(), token.getId()))){
            String allSkills = "SELECT idskill, skill, datetime_start, datetime_end, type, description FROM skills " +
                    "WHERE iduser = ?";
            try {
                pSt = con.prepareStatement(allSkills);
                pSt.setInt(1, token.getId());
                rs = pSt.executeQuery();
                System.out.println("AFTO TO KATI POU THELW "+ token.getId());
                if(!rs.next()){
                    return Response.ok("empty skills").build();
                }
                else {
                    JSONArray jarray = ResultSetToJsonMapper.mapResultSet(rs);
                    System.out.println(jarray.toString());
                    return Response.ok(jarray.toString()).build();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
            finally {
                try {
                    con.close();
                    rs.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }

        }
        else {
            //return only public skills
        }
        return Response.ok("empty skills").build();
    }

    private boolean isConnected(int userid, int id) {
        return true;
    }

    @Path("picture/{id}")
    @GET
    public Response getFullImage(@PathParam("id") String id) throws IOException {


        String filePath = "C:/Users/Public/ProfPictures/" + id + ".jpg";

        System.out.println("I got a requeeest_______" + filePath);

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("I got a  NOT FOUND requeeest_______" + filePath);
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Response.ResponseBuilder response = Response.ok((Object) file).header("content-type", "image/jpg");

        return response.build();
        //return Response.ok().build();

        // uncomment line below to send streamed
        // return Response.ok(new ByteArrayInputStream(imageData)).build();
    }

    @Path("upload")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile( @FormDataParam("file") InputStream fileInputStream,
                                @FormDataParam("file") FormDataContentDisposition contentDispositionHeader,
                                @FormDataParam("token") String token) {
        Authenticator auth = new Authenticator(token);
        try {
            auth.authenticate(auth.getToken());
            System.out.println("Authenticated Token with user type " + auth.getType());
        }catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        String filePath = "C:/Users/Public/ProfPictures/"+ auth.getUserid() + ".jpg";

        // save the file to the server
        saveFile(fileInputStream, filePath);

        String output = "File saved to server location : " + filePath;

        return Response.status(200).build();

    }

    // save uploaded file to a defined location on the server
    private void saveFile(InputStream uploadedInputStream,
                          String serverLocation) {

        try {
            File file = new File(serverLocation);
            file.delete();
            OutputStream outpuStream = new FileOutputStream( new File(serverLocation),false);
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = uploadedInputStream.read(bytes)) != -1) {
                outpuStream.write(bytes, 0, read);
            }
            outpuStream.flush();
            outpuStream.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }


    @Path("deleteskill")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response delSkill(String json) throws IOException, SQLException {
        Gson gson = new Gson();
        MyToken token = gson.fromJson(json, MyToken.class);
        Authenticator auth = new Authenticator(token.getToken());
        try {
            auth.authenticate(auth.getToken());
            System.out.println("Authenticated Token with user type " + auth.getType());
        }catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        PreparedStatement pSt = null;
        Connection con = DBConnector.getInstance().getConnection();
        ResultSet rs = null;
        if ( mySkill(auth.getUserid(),token.getId())){
            try {
                String delSkill = "DELETE  FROM skills " +
                        "WHERE idskill = ?";
                pSt = con.prepareStatement(delSkill);
                pSt.setInt(1, token.getId());
                pSt.executeUpdate();
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
        }
        return Response.ok().build();
    }

    private boolean mySkill(int userid, int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String myPost = "SELECT iduser " +
                "FROM skills " +
                "WHERE idskill = ? LIMIT 1";
        try{
            con =  DBConnector.getInstance().getConnection();
            ps  = con.prepareStatement(myPost);
            ps.setInt(1,id);
            rs  = ps.executeQuery();
            if(!rs.next()){
                System.out.println("ResultSet is empty!");
                return false ;
            }
            else {
                if( Integer.parseInt(rs.getString("iduser")) == userid )
                    return true;
                else return false ;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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