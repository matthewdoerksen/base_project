package com.doerksen.base_project.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/word-count")
public interface WordCountResource {
    @GET
    @Path("/{url}")
    Response getWordCount(@PathParam("url") String url);

    @POST
    @Path("/{url}")
    Response insertWordCount(@PathParam("url") String url);

    @PUT
    @Path("/{url}")
    Response updateWordCount(@PathParam("url") String url);
}
