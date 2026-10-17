package com.framework.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.framework.api.models.Post;
import com.framework.api.services.PostsService;
import com.framework.base.BaseApiTest;
import com.microsoft.playwright.APIResponse;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    @Test(description = "POST /posts returns a simulated response and sends the expected Content-Type")
    public void createPost() {
        Post payload = new Post(1, "foo", "bar");
        APIResponse res = posts.create(payload);
        assertEquals(res.status(), 201);
        Post created = ApiClient.parse(res, Post.class);
        assertEquals(created.title, "foo");
        assertEquals(created.body, "bar");
        assertEquals(created.userId, Integer.valueOf(1));
        assertEquals(created.id, Integer.valueOf(101));

        // JSONPlaceholder's POST response covers its simulated response contract. A separate,
        // local capture endpoint observes the actual outbound header without attributing a
        // fabricated response to JSONPlaceholder or creating another behavior test.
        try (OutboundRequestCaptureServer capture = new OutboundRequestCaptureServer();
             ApiClient captureClient = new ApiClient(null, capture.baseUrl())) {
            APIResponse captureResponse = new PostsService(captureClient).create(payload);
            assertEquals(captureResponse.status(), 204);
            assertEquals(capture.contentType(), "application/json; charset=UTF-8");
        }
    }

    @Test(description = "PUT /posts/1 returns a simulated response and sends the expected Content-Type")
    public void updatePost() {
        Post payload = new Post(1, "foo", "bar");
        payload.id = 1;
        APIResponse res = posts.update(1, payload);
        assertEquals(res.status(), 200);
        Post updated = ApiClient.parse(res, Post.class);
        assertEquals(updated.id, Integer.valueOf(1));
        assertEquals(updated.title, "foo");
        assertEquals(updated.body, "bar");
        assertEquals(updated.userId, Integer.valueOf(1));

        // JSONPlaceholder simulates PUT responses and does not persist writes. A local capture
        // endpoint independently observes the outbound header without changing remote data.
        try (OutboundRequestCaptureServer capture = new OutboundRequestCaptureServer();
             ApiClient captureClient = new ApiClient(null, capture.baseUrl())) {
            APIResponse captureResponse = new PostsService(captureClient).update(1, payload);
            assertEquals(captureResponse.status(), 204);
            assertEquals(capture.contentType(), "application/json; charset=UTF-8");
        }
    }

    @Test(description = "PATCH /posts/1 returns the exact simulated response and sends the expected Content-Type")
    public void patchPostTitle() {
        Map<String, String> payload = Map.of("title", "foo");
        APIResponse response = posts.patch(1, payload);
        JsonNode post = ApiClient.parse(response, JsonNode.class);

        assertTrue(post.isObject(), "PATCH response should be a JSON object");
        Set<String> fields = new HashSet<>();
        post.fieldNames().forEachRemaining(fields::add);
        assertEquals(fields, Set.of("userId", "id", "title", "body"));
        assertEquals(post.get("userId").intValue(), 1);
        assertEquals(post.get("id").intValue(), 1);
        assertEquals(post.get("title").textValue(), "foo");
        assertEquals(post.get("body").textValue(),
                "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\n"
                        + "reprehenderit molestiae ut ut quas totam\n"
                        + "nostrum rerum est autem sunt rem eveniet architecto");

        // JSONPlaceholder accepts simulated writes but does not persist them. Observe the
        // outbound header independently against a local capture endpoint.
        try (OutboundRequestCaptureServer capture = new OutboundRequestCaptureServer();
             ApiClient captureClient = new ApiClient(null, capture.baseUrl())) {
            new PostsService(captureClient).patch(1, payload);
            assertEquals(capture.contentType(), "application/json; charset=UTF-8");
        }
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
