package com.framework.base;

import com.framework.api.ApiClient;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public abstract class BaseApiTest {
    protected ApiClient apiClient;

    @BeforeClass(alwaysRun = true)
    public void setUpApi() { apiClient = new ApiClient(); }

    @AfterClass(alwaysRun = true)
    public void tearDownApi() { if (apiClient != null) apiClient.close(); }
}
