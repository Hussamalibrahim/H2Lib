package com.library.library.controller;

import com.library.library.model.Users;
import com.library.library.security.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    @GetMapping("/settings")
    public String settingPage(Model model, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal != null) {
            Users user = userPrincipal.getUsers();
            model.addAttribute("user", user);
        }
        return "settings";
    }
    @GetMapping("/profile")
    public String profilePage(Model model,@AuthenticationPrincipal UserPrincipal userPrincipal){
        if (userPrincipal == null) {
            return "redirect:/";
        }
        model.addAttribute("user",userPrincipal.getUsers());
        model.addAttribute("userCredentials",userPrincipal.getUsersCredentials());
//        model.addAttribute("books",userPrincipal.getUsers().getUploadedBooks());

        return "profile";
    }


}
