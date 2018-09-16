package com.linkedin.controller;


import com.google.gson.Gson;
import com.linkedin.db.DBConnector;
import com.linkedin.pojos.MyToken;
import com.linkedin.pojos.Post;
import com.linkedin.security.Authenticator;
import com.linkedin.utilities.ResultSetToJsonMapper;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONArray;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
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
            System.out.println("EIMAI STA COMMENTS________ " + auth.getType() + "id post " + token.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        PreparedStatement pSt = null;
        Connection con = DBConnector.getInstance().getConnection();
        ResultSet rs = null;
        String allComms = "SELECT name,surname,author, idpost , text, datetime, seen " +
                "FROM comments join user WHERE idpost = ? and user.iduser = comments.author" +
                " order by datetime DESC";
        try {
            pSt = con.prepareStatement(allComms);
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


    @Path("newcomment")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createComm (String json) throws IOException, SQLException {
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
            String addPost = "INSERT INTO comments " +
                    "(idpost,author,text,datetime,seen) " +
                    "values " +
                    "(?,?,?,?,0)";
            pSt = con.prepareStatement(addPost);
            pSt.setInt(1, newPost.getIduser());
            pSt.setInt(2, auth.getUserid());
            pSt.setString(3, newPost.getPost());
            pSt.setString(4, timeStamp);
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



    @Path("getposts")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getPosts(String json) throws IOException, SQLException {
        Gson gson = new Gson();
        MyToken token = gson.fromJson(json, MyToken.class);
        Authenticator auth = new Authenticator(token.getToken());
        try {
            auth.authenticate(auth.getToken());
            System.out.println("EIMAI STA Posts_______ " + auth.getType() + "id post " + token.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        PreparedStatement pSt = null;
        Connection con = DBConnector.getInstance().getConnection();
        ResultSet rs = null;
        String allPosts = "(select user.name , user.surname, post.idpost , user.iduser , post.datetime ,post.post " +
                "from " +
                "user, " +
                "post, " +
                "(select iduser as id , name , surname , email " +
                "from " +
                "(select * from connection " +
                "where iduser1 = ? " +
                "union " +
                "select b.iduser2 as iduser1 , b.iduser1 as iduser2, status " +
                "from connection b " +
                "where iduser2 = ?) as a " +
                "inner join " +
                "        user " +
                "where status=1 and iduser=a.iduser2 ) as usr, " +
                "(select idpost,iduser " +
                "from likes ) as lk " +
                "where usr.id=lk.iduser and post.author=user.iduser and post.idpost=lk.idpost and user.iduser <> ?) " +
                "union distinct" +
                "(select user.name , user.surname, post.idpost , user.iduser , post.datetime ,post.post " +
                "from " +
                "user, " +
                "post, " +
                "(select iduser as id , name , surname , email " +
                "from" +
                "(select * from connection " +
                "where iduser1 = ? " +
                "union " +
                "select b.iduser2 as iduser1 , b.iduser1 as iduser2, status " +
                "from connection b " +
                "where iduser2 = ?) as a " +
                "inner join " +
                "        user " +
                "where status=1 and iduser=a.iduser2 or iduser=?) as usr " +
                "where post.author=usr.id and user.iduser=post.author) order by datetime desc ";
        try {
            pSt = con.prepareStatement(allPosts);
            pSt.setInt(1, auth.getUserid());
            pSt.setInt(2, auth.getUserid());
            pSt.setInt(3, auth.getUserid());
            pSt.setInt(4, auth.getUserid());
            pSt.setInt(5, auth.getUserid());
            pSt.setInt(6, auth.getUserid());
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


    @Path("getlikes")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getLikes(String json) throws IOException, SQLException {
        Gson gson = new Gson();
        MyToken token = gson.fromJson(json, MyToken.class);
        Authenticator auth = new Authenticator(token.getToken());
        try {
            auth.authenticate(auth.getToken());
            System.out.println("EIMAI STA Posts_______ " + auth.getType() + "id post " + token.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        PreparedStatement pSt = null;
        Connection con = DBConnector.getInstance().getConnection();
        ResultSet rs = null;
        String allPosts =
                "select count(iduser) as likes, idpost " +
                "from likes where idpost=? group by idpost;";
        try {
            pSt = con.prepareStatement(allPosts);
            pSt.setInt(1, token.getId());
            rs = pSt.executeQuery();
            if (!rs.next()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                JsonObject tokenJson = Json.createObjectBuilder()
                        .add("likes",rs.getInt(1))
                        .add("idpost",rs.getInt(2))
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

    @Path("picture/{id}")
    @GET
    public Response getFullImage(@PathParam("id") String id) throws IOException {


        String filePath = "C:/Users/Public/PostPictures/" + id + ".jpg";

        System.out.println("I got a requeeest_______" + filePath);

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("I got a  NOT FOUND requeeest_______" + filePath);
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Response.ResponseBuilder response = Response.ok((Object) file).header("content-type", "image/jpg");

        return response.build();
    }

    @Path("upload")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@FormDataParam("file") InputStream fileInputStream,
                               @FormDataParam("file") FormDataContentDisposition contentDispositionHeader,
                               @FormDataParam("token") String token) {
        Authenticator auth = new Authenticator(token);
        try {
            auth.authenticate(auth.getToken());
            System.out.println("Authenticated Token with user type " + auth.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        String filePath = "C:/Users/Public/PostPictures/" + auth.getUserid() + ".jpg";

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
            OutputStream outpuStream = new FileOutputStream(new File(serverLocation), false);
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
}
