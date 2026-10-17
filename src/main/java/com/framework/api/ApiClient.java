package com.framework.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.framework.config.Config;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;

import java.util.HashMap;
import java.util.Map;

/** Thin wrapper over Playwright's APIRequestContext: JSON handling + optional bearer token. */
public class ApiClient implements AutoCloseable {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Playwright playwright;
    private final APIRequestContext request;

    public ApiClient() { this(null); }

    public ApiClient(String bearerToken) { this(bearerToken, Config.apiBaseUrl()); }

    public ApiClient(String bearerToken, String baseUrl) {
        this.playwright = Playwright.create();
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        if (bearerToken != null) headers.put("Authorization", "Bearer " + bearerToken);
        this.request = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(baseUrl)
                .setExtraHTTPHeaders(headers));
    }

    public APIResponse get(String path) { return request.get(path); }

    public APIResponse get(String path, Map<String, Object> queryParams) {
        RequestOptions options = RequestOptions.create();
        queryParams.forEach((k, v) -> options.setQueryParam(k, String.valueOf(v)));
        return request.get(path, options);
    }

    public APIResponse post(String path, Object body)  { return post(path, body, "application/json"); }
    public APIResponse post(String path, Object body, String contentType) {
        return request.post(path, jsonBody(body, contentType));
    }
    public APIResponse put(String path, Object body)   { return request.put(path, jsonBody(body)); }
    public APIResponse patch(String path, Object body) { return request.patch(path, jsonBody(body)); }
    public APIResponse delete(String path)             { return request.delete(path); }

    public static <T> T parse(APIResponse response, Class<T> type) {
        try {
            return MAPPER.readValue(response.text(), type);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot parse response from " + response.url()
                    + " -> " + response.text(), e);
        }
    }

    private static RequestOptions jsonBody(Object body) {
        return jsonBody(body, "application/json");
    }

    private static RequestOptions jsonBody(Object body, String contentType) {
        try {
            return RequestOptions.create()
                    .setHeader("Content-Type", contentType)
                    .setData(MAPPER.writeValueAsString(body));
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot serialise request body", e);
        }
    }

    @Override
    public void close() {
        request.dispose();
        playwright.close();
    }
}
