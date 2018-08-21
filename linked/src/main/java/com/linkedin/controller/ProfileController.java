package com.linkedin.controller;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;


@Path("profile")
public class ProfileController {

    @Path("getskills")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getSkills(String json) {
        JsonObject tokenJson = Json.createObjectBuilder()
                .add("name", "sniper")
                .add("date_start", "2000-01-01")
                .add("date_end", "2010-01-01")
                .add("type", 0)
                .add("description", "High accurate sniper with more than 1000 confirmed kills")
                .add("visible", "public")
                .build();
        JsonObject tokenJson2 = Json.createObjectBuilder()
                .add("name", "sniper")
                .add("date_start", "2000-01-01")
                .add("date_end", "2010-01-01")
                .add("type", 1)
                .add("description", "High accurate sniper with more than 1000 confirmed kills")
                .add("visible", "public")
                .build();
        JsonArray jarray = Json.createArrayBuilder()
                .add(tokenJson)
                .add(tokenJson2)
                .build();
        return Response.ok(jarray).build();
    }

    @Path("picture/{id}")
    @GET
    public Response getFullImage(@PathParam("id") String id) throws IOException {


        String filePath = "C:/" + id + ".jpg";

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
    public Response uploadFile( @FormDataParam("file") InputStream uploadedInputStream,
                                @FormDataParam("file") FormDataContentDisposition fileDetail) {
        return Response.ok().build();
    }

}