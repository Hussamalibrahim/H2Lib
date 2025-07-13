package com.library.library.Controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("admin")
public class AdminController {
    //  /admin
    // admin could
    //  /admin + user / to get the user can pend
    //  /admin + book / book request
    //  /admin + author / author request of changing

}
