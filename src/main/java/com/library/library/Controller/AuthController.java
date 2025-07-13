package com.library.library.Controller;

import com.library.library.Model.Dto.LoginRequest;
import com.library.library.Model.Dto.RegistrationDto;
import com.library.library.Security.JWT.JwtService;
import com.library.library.Security.UserPrincipal;
import com.library.library.Service.UserCredentialsService;
import com.library.library.Service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserCredentialsService userCredentialsService;



    @GetMapping("/login")
    public String loginPage(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal != null && userPrincipal.isOAuth2User()) {
            return "redirect:/";
        }
        return "login";
    }


    @GetMapping("/register")
    public String registerPage(@AuthenticationPrincipal UserPrincipal userPrincipal) {

        if (userPrincipal != null && userPrincipal.isOAuth2User()) {
            return "redirect:/";
        }
        return "register";
    }

    @GetMapping("/logout")
    public String logoutPage(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return "redirect:/";
        }
        return "logout";
    }

    @PostMapping("/login-back")
    public String loginBack(@RequestBody LoginRequest loginRequest,
                            @AuthenticationPrincipal UserPrincipal userPrincipal,
                            HttpServletRequest request,
                            RedirectAttributes redirectAttributes,
                            HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getPassword(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtService.generateToken(userPrincipal);

            request.getSession().setAttribute("JWT", jwt);

            return "redirect:/";

        } catch (AuthenticationException e) {
            redirectAttributes.addAttribute("error", "Invalid email or password");
            return "redirect:/login";
        }
    }

    @PostMapping("/register-back")
    public String registerBack(@RequestBody RegistrationDto registrationRequest,
                               RedirectAttributes redirectAttributes) {
        try {
            if (!registrationRequest.getPassword().equals(registrationRequest.getConfirmPassword())) {
                redirectAttributes.addAttribute("error", "Passwords do not match");
                return "redirect:/register";
            }

            RegistrationDto registrationDto = new RegistrationDto();
            registrationDto.setName(registrationRequest.getName());
            registrationDto.setEmail(registrationRequest.getEmail());
            registrationDto.setPassword(registrationRequest.getPassword());
            registrationDto.setConfirmPassword(registrationRequest.getConfirmPassword());

            userCredentialsService.create(registrationDto);

            redirectAttributes.addAttribute("success", "Registration successful. Please login.");
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }
}