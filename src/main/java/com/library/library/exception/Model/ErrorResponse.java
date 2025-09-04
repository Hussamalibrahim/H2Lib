package com.library.library.exception.Model;


import lombok.Data;
import java.util.List;

@Data
public class ErrorResponse {
  private String message;
  private int status;
  private String path;
  private List<String> details;
  private String timestamp; // Added for better error tracking

  public ErrorResponse(String message, int status, String path, List<String> details) {
    this.message = message;
    this.status = status;
    this.path = path;
    this.details = details;
    this.timestamp = java.time.LocalDateTime.now().toString();
  }
}