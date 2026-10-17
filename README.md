# Playwright + Java + TestNG: API & UI E2E Framework

## Prerequisites
Java 17+, Maven 3.8+.

## Setup (one time)
```bash
mvn compile exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps chromium"
```

## Run
| Command | What it runs |
|---|---|
| `mvn test` | Whole suite (API, UI, E2E; 3 classes in parallel) |
| `mvn test -DsuiteXmlFile=src/test/resources/testng-bdd.xml` | BDD scenarios only, through the Cucumber TestNG runner |
| `mvn test -Dtest=PostsApiTest` | One class |
| `mvn test -Dgroups=smoke` | Smoke-tagged tests only |
| `mvn test -Dheadless=false -Dbrowser=firefox` | Headed Firefox |
| `mvn test -Dui.base.url=https://staging.example.com` | Override any config key |

Reports: `target/surefire-reports/`. On a UI failure you also get `artifacts/<Class>_<method>.png` and a `-trace.zip`:
```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="show-trace artifacts/<name>-trace.zip"
```

## Structure
```
src/main/java/com/framework/
  config/Config.java            -D flag > env var > config.properties
  core/PlaywrightFactory.java   ThreadLocal Playwright/Browser/Context/Page (parallel-safe)
  api/ApiClient.java            Playwright APIRequestContext + Jackson
  api/models, api/services      POJOs and one service class per resource
  pages/                        Page Objects (fluent, return the next page)
src/test/java/com/framework/
  base/BaseUiTest, BaseApiTest  lifecycle, failure screenshot + trace
  bdd/                          Cucumber step definitions and TestNG runner
  api/ ui/ e2e/                 test classes
src/test/resources/
  config.properties, testng.xml, testng-bdd.xml, features/*.feature
```

BDD scenarios are written in Gherkin under `src/test/resources/features/`. The `CucumberTestRunner` uses Cucumber's TestNG adapter; UI scenarios tagged `@ui` receive a fresh Playwright session and failure artifacts. API scenarios reuse `ApiClient` and `PostsService`.

## Conventions
- No raw selectors or URLs in tests: put them in Page Objects / services.
- Use `assertThat(locator)...` (Playwright assertions) for UI; they auto-wait.
- Tests must not share state; each UI test gets its own browser context.

## Adapting to your app
1. Edit `config.properties` (URLs, credentials).
2. Replace `PostsService`/models with your endpoints; pass a token via `new ApiClient(token)`.
3. Replace the pages and `loginAsStandardUser()` with your flows.
