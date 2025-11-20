package com.library.library.Utils;

public class HtmlUtils {

    public static String sanitize(String input) {
        return org.springframework.web.util.HtmlUtils.htmlEscape(input == null ? "" : input);
    }
}
