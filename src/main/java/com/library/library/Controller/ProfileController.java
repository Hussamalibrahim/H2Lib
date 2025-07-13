package com.library.library.Controller;

import com.library.library.Model.Users;
import com.library.library.Security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class ProfileController {

    @GetMapping("/settings")
    public String logoutPage(Model model, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal != null) {
            Users user = userPrincipal.getUsers();
            model.addAttribute("user", user);
        }
        return "settings";
    }

}
