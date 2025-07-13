package com.library.library.Controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@PreAuthorize("hasRole('USER')")
@RequestMapping("/H2LIB")
public class UserController {

    // there is no need to jwt or user details because it is local for every body
    @RequestMapping()
    public ModelAndView showBooks(@ModelAttribute ModelAndView modelAndView){




        return modelAndView;
    }



}
