package com.framework.bdd;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = "com.framework.bdd",
        plugin = {"pretty", "html:target/cucumber-report.html", "json:target/cucumber-report.json"}
)
public class CucumberTestRunner extends AbstractTestNGCucumberTests {
}