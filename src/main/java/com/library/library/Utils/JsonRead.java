package com.library.library.Utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@SuppressWarnings(value = "all")
public class JsonRead {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, String> getJsonBody(HttpServletRequest request) {
        try {
            Object jsonAttr = request.getAttribute("JSON_BODY");
            if (jsonAttr != null) {
                return (Map<String, String>) jsonAttr;
            }

            Map<String, String> json = objectMapper.readValue(
                    request.getInputStream(),
                    new TypeReference<Map<String, String>>() {}
            );

            request.setAttribute("JSON_BODY", json);
            return json;

        } catch (Exception e) {
            return Map.of();
        }
    }
}
