package com.framework.base;

import com.framework.core.PlaywrightFactory;
import com.microsoft.playwright.Page;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Fresh browser/context/page per test. On failure: screenshot + Playwright trace in artifacts/. */
public abstract class BaseUiTest {
    protected Page page;

    @BeforeMethod(alwaysRun = true)
    public void setUpBrowser() {
        page = PlaywrightFactory.startBrowserSession();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownBrowser(ITestResult result) throws Exception {
        Path trace = null;
        try {
            if (result.getStatus() == ITestResult.FAILURE) {
                Path dir = Paths.get("artifacts");
                Files.createDirectories(dir);
                String name = result.getTestClass().getRealClass().getSimpleName() + "_" + result.getMethod().getMethodName();
                page.screenshot(new Page.ScreenshotOptions().setPath(dir.resolve(name + ".png")).setFullPage(true));
                trace = dir.resolve(name + "-trace.zip");
                System.out.println("Failure artifacts: " + dir.toAbsolutePath() + "/" + name + "*  (view trace: mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args=\"show-trace " + trace + "\")");
            }
        } finally {
            // Always release the browser, even if the failure screenshot itself threw.
            PlaywrightFactory.stopBrowserSession(trace);
        }
    }
}
