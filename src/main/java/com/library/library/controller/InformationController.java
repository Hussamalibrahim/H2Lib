package com.library.library.controller;

import com.library.library.model.Users;
import com.library.library.security.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/us")
public class InformationController {

    @GetMapping("contact-us")
    public String contactUs(Model model, @AuthenticationPrincipal UserPrincipal userPrincipal){
        if (userPrincipal != null) {
            Users user = userPrincipal.getUsers();
            model.addAttribute("user", user);
        }

        return "contact-us";
    }
    @GetMapping("terms")
    public String termOfService(Model model, @AuthenticationPrincipal UserPrincipal userPrincipal){
        if (userPrincipal != null) {
            Users user = userPrincipal.getUsers();
            model.addAttribute("user", user);
        }

        return "terms";
    }
    @GetMapping("privacy-policy")
    public String privacyPolicy(Model model, @AuthenticationPrincipal UserPrincipal userPrincipal){
        if (userPrincipal != null) {
            Users user = userPrincipal.getUsers();
            model.addAttribute("user", user);
        }

        return "privacy";
    }
}
