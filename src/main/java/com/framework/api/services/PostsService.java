package com.framework.api.services;

import com.framework.api.ApiClient;
import com.framework.api.models.Post;
import com.microsoft.playwright.APIResponse;

import java.util.Map;

/** One service class per API resource keeps tests free of URLs and plumbing. */
public class PostsService {
    private final ApiClient client;

    public PostsService(ApiClient client) { this.client = client; }

    public APIResponse list()                { return client.get("/posts"); }
    public APIResponse listByUser(int userId){ return client.get("/posts", Map.of("userId", userId)); }
    public APIResponse get(int id)           { return client.get("/posts/" + id); }
    public APIResponse create(Post post)     {
        return client.post("/posts", post, "application/json; charset=UTF-8");
    }
    public APIResponse update(int id, Post p){ return client.put("/posts/" + id, p); }
    public APIResponse delete(int id)        { return client.delete("/posts/" + id); }
}
