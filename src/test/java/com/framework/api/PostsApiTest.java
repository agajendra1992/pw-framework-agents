package com.framework.api;

import com.framework.api.models.Post;
import com.framework.api.services.PostsService;
import com.framework.base.BaseApiTest;
import com.microsoft.playwright.APIResponse;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class PostsApiTest extends BaseApiTest {
    private PostsService posts;

    @BeforeClass(dependsOnMethods = "setUpApi", alwaysRun = true)
    public void initService() { posts = new PostsService(apiClient); }

    @Test(groups = "smoke", description = "GET /posts/1 returns a valid post")
    public void getPostById() {
        APIResponse res = posts.get(1);
        assertEquals(res.status(), 200);
        Post post = ApiClient.parse(res, Post.class);
        assertEquals(post.id, Integer.valueOf(1));
        assertNotNull(post.title);
    }

    @Test(description = "GET /posts?userId=1 filters by user")
    public void listPostsByUser() {
        APIResponse res = posts.listByUser(1);
        assertEquals(res.status(), 200);
        Post[] list = ApiClient.parse(res, Post[].class);
        assertTrue(list.length > 0);
        for (Post p : list) assertEquals(p.userId, Integer.valueOf(1));
    }

    @Test(description = "POST /posts creates a post")
    public void createPost() {
        Post payload = new Post(1, "e2e title", "e2e body");
        APIResponse res = posts.create(payload);
        assertEquals(res.status(), 201);
        Post created = ApiClient.parse(res, Post.class);
        assertNotNull(created.id);
        assertEquals(created.title, payload.title);
        assertEquals(created.body, payload.body);
    }

    @Test(description = "PUT /posts/1 updates a post")
    public void updatePost() {
        Post payload = new Post(1, "updated", "updated body");
        APIResponse res = posts.update(1, payload);
        assertEquals(res.status(), 200);
        assertEquals(ApiClient.parse(res, Post.class).title, "updated");
    }

    @Test(description = "DELETE /posts/1 succeeds")
    public void deletePost() {
        assertEquals(posts.delete(1).status(), 200);
    }

    @Test(description = "Unknown post returns 404")
    public void unknownPostReturns404() {
        assertEquals(posts.get(999_999).status(), 404);
    }
}
