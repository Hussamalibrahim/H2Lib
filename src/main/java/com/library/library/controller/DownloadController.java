package com.library.library.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
@PreAuthorize("hasRole('USER')")
public class DownloadController {
}
