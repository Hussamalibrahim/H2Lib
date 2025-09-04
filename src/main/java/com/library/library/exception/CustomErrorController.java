package com.library.library.exception;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exceptionObj = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object pathObj = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        int statusCode = statusObj != null ? Integer.parseInt(statusObj.toString()) : 500;
        String message = "An unexpected error occurred";
        String title = "Error";
        String icon = "⚠️";
        Integer countdown = null;
        String retryLink = null;
        String loginLink = null;

        switch (statusCode) {
            case 400 -> { title = "Bad Request"; icon = "✋"; }
            case 401 -> { title = "Unauthorized"; icon = "🔒"; loginLink = "/login"; }
            case 403 -> { title = "Forbidden"; icon = "🚫"; }
            case 404 -> { title = "Not Found"; icon = "❓"; }
            case 405 -> { title = "Method Not Allowed"; icon = "🔄"; }
            case 413 -> { title = "Payload Too Large"; icon = "📏"; }
            case 415 -> { title = "Unsupported Media Type"; icon = "📁"; }
            case 423 -> {
                title = "Account Locked"; icon = "🔒";
                countdown = 30; // example countdown seconds
                retryLink = "#";
            }
            case 429 -> {
                title = "Too Many Requests"; icon = "⏱️";
                countdown = 30; retryLink = "#";
            }
            case 500 -> { title = "Server Error"; icon = "⚠️"; }
        }

        model.addAttribute("statusCode", statusCode);
        model.addAttribute("title", title);
        model.addAttribute("icon", icon);
        model.addAttribute("message", message);
        model.addAttribute("path", pathObj != null ? pathObj : "Unknown path");
        model.addAttribute("countdownSeconds", countdown);
        model.addAttribute("retryLink", retryLink);
        model.addAttribute("loginLink", loginLink);

        if (exceptionObj instanceof org.springframework.validation.BindException bindEx) {
            model.addAttribute("fieldErrors", bindEx.getFieldErrors()
                    .stream()
                    .collect(java.util.stream.Collectors.toMap(
                            org.springframework.validation.FieldError::getField,
                            e -> e.getDefaultMessage() != null ? e.getDefaultMessage() : "Invalid value"
                    )));
        }

        return "error";
    }
}
