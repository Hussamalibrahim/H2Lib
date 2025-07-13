package com.library.library.Exception;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object path = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        // Default values
        int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String errorMessage = "An unexpected error occurred";
        String template = "error/generic";

        if (status != null) {
            statusCode = Integer.parseInt(status.toString());

            // Set template and message based on status code
            switch (statusCode) {
                case 401 -> {
                    template = "error/401";
                    errorMessage = "Authentication required";
                }
                case 403 -> {
                    template = "error/403";
                    errorMessage = "You don't have permission";
                }
                case 404 -> {
                    template = "error/404";
                    errorMessage = "Page not found";
                }
                case 429 -> {
                    template = "error/429";
                    errorMessage = "Too many requests. Please try again later.";
                }
                case 500 -> {
                    template = "error/500";
                    errorMessage = "Internal server error";
                }
            }
        }

        // Add attributes to model
        model.addAttribute("timestamp", new java.util.Date());
        model.addAttribute("status", statusCode);
        model.addAttribute("path", path != null ? path : "Unknown path");
        model.addAttribute("error", exception != null ? exception.toString() : "Unknown error");
        model.addAttribute("message", errorMessage);

        return template;
    }
}