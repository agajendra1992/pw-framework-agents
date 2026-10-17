package com.framework.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Resolution order: -Dkey=value  >  ENV_VAR (KEY_NAME)  >  config.properties
 * e.g. mvn test -Dbrowser=firefox -Dheadless=false
 */
public final class Config {
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) PROPS.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load config.properties", e);
        }
    }

    private Config() {}

    public static String get(String key) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank() && !sys.startsWith("${")) return sys;
        String env = System.getenv(key.toUpperCase().replace('.', '_'));
        if (env != null && !env.isBlank()) return env;
        String value = PROPS.getProperty(key);
        if (value == null) throw new IllegalStateException("Missing config key: " + key);
        return value;
    }

    public static boolean getBoolean(String key) { return Boolean.parseBoolean(get(key)); }
    public static int getInt(String key) { return Integer.parseInt(get(key)); }

    public static String uiBaseUrl()  { return get("ui.base.url"); }
    public static String apiBaseUrl() { return get("api.base.url"); }
    public static String browser()    { return get("browser").toLowerCase(); }
    public static boolean headless()  { return getBoolean("headless"); }
    public static int slowMo()        { return getInt("slow.mo"); }
    public static int timeoutMs()     { return getInt("default.timeout.ms"); }
}
