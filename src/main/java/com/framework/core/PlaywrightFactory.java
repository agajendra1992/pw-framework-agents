package com.framework.core;

import com.framework.config.Config;
import com.microsoft.playwright.*;

import java.nio.file.Path;
import java.util.Set;

/**
 * Playwright objects are NOT thread-safe, so each test thread gets its own set.
 * This makes TestNG parallel execution safe.
 */
public final class PlaywrightFactory {
    private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<>();
    private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();

    private PlaywrightFactory() {}

    private static final Set<String> SUPPORTED_BROWSERS = Set.of("chromium", "firefox", "webkit");

    public static Page startBrowserSession() {
        // Validate before creating anything, so a typo fails loudly instead of running Chromium.
        String browserName = Config.browser();
        if (!SUPPORTED_BROWSERS.contains(browserName)) {
            throw new IllegalArgumentException("Unsupported browser '" + browserName
                    + "'. Use one of: " + SUPPORTED_BROWSERS);
        }

        Playwright playwright = Playwright.create();
        playwright.selectors().setTestIdAttribute("data-test"); // saucedemo uses data-test
        PLAYWRIGHT.set(playwright);

        BrowserType type = switch (browserName) {
            case "firefox" -> playwright.firefox();
            case "webkit" -> playwright.webkit();
            default -> playwright.chromium();
        };
        Browser browser = type.launch(new BrowserType.LaunchOptions()
                .setHeadless(Config.headless())
                .setSlowMo(Config.slowMo()));
        BROWSER.set(browser);

        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setBaseURL(Config.uiBaseUrl())
                .setViewportSize(1366, 768));
        context.setDefaultTimeout(Config.timeoutMs());
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true).setSnapshots(true).setSources(true));
        CONTEXT.set(context);

        Page page = context.newPage();
        PAGE.set(page);
        return page;
    }

    public static Page page() { return PAGE.get(); }

    /** Stops tracing (saving a zip only when a path is given) and releases everything. */
    public static void stopBrowserSession(Path traceOnFailure) {
        try {
            BrowserContext context = CONTEXT.get();
            if (context != null) {
                if (traceOnFailure != null) {
                    context.tracing().stop(new Tracing.StopOptions().setPath(traceOnFailure));
                } else {
                    context.tracing().stop();
                }
                context.close();
            }
            if (BROWSER.get() != null) BROWSER.get().close();
            if (PLAYWRIGHT.get() != null) PLAYWRIGHT.get().close();
        } finally {
            PAGE.remove();
            CONTEXT.remove();
            BROWSER.remove();
            PLAYWRIGHT.remove();
        }
    }
}
