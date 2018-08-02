package com.linkedin.controller;


import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;


@Path("hello")
public class HelloWorld {

    @GET
    public JsonObject getMsg(){
        // Create a dog instance

        return Json.createObjectBuilder().add("firstname","kostas").build();
    }
}
