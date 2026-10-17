package com.framework.pages;

import com.microsoft.playwright.Page;

public abstract class BasePage {
    protected final Page page;

    protected BasePage(Page page) { this.page = page; }

    public void navigate(String path) { page.navigate(path); }
    public String currentUrl()        { return page.url(); }
}
