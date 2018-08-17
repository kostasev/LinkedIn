package com.linkedin.controller;


import javax.imageio.ImageIO;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@Path("profile")
public class ProfileController {

    @Path("getskills")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getSkills( String json ){
        JsonObject tokenJson = Json.createObjectBuilder()
                .add("name","sniper")
                .add("date_start","2000-01-01")
                .add("date_end","2010-01-01")
                .add("type",0)
                .add("description","High accurate sniper with more than 1000 confirmed kills")
                .add("visible", "public")
                .build();
        JsonObject tokenJson2 = Json.createObjectBuilder()
                .add("name","sniper")
                .add("date_start","2000-01-01")
                .add("date_end","2010-01-01")
                .add("type",1)
                .add("description","High accurate sniper with more than 1000 confirmed kills")
                .add("visible", "public")
                .build();
        JsonArray jarray = Json.createArrayBuilder()
                .add(tokenJson)
                .add(tokenJson2)
                .build();
        return Response.ok(jarray).build();
    }

    @Path("picture")
    @GET
    @Produces("image/png")
    public Response getFullImage (  ) throws IOException {
        System.out.println("I got a requeeest_______");

        String filePath = "C:/41.png";
        File file = new File(filePath);

        Response.ResponseBuilder response = Response.ok((Object) file);

        response.header("Content-Disposition",
                "attachment; filename=41.png");

        return response.build();
        //return Response.ok().build();

        // uncomment line below to send streamed
        // return Response.ok(new ByteArrayInputStream(imageData)).build();
    }

}
