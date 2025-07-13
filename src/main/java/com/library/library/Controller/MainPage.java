package com.library.library.Controller;


import com.library.library.Model.Dto.BookDto;
import com.library.library.Model.Dto.BookTypeDto;
import com.library.library.Model.Users;
import com.library.library.Security.UserPrincipal;
import com.library.library.Service.BookService;
import com.library.library.Service.BookTypeService;
import com.library.library.Service.LibraryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.IOException;

@Slf4j
@Controller
public class MainPage {

    @Autowired
    LibraryService libraryService;

    @Autowired
    BookTypeService bookTypeService;

    @Autowired
    BookService bookService;



    @GetMapping(produces = {MediaType.TEXT_HTML_VALUE})
    public String getMainPage(Model model, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal != null) {
            Users user = userPrincipal.getUsers();
            model.addAttribute("user", user);
            log.info("\n\nn\n\n {} user sign in \n\nn\n\n",user.getId());
        }
            model.addAttribute("categories", bookTypeService.findAll());


        model.addAttribute("categories", bookTypeService.findAll());
        model.addAttribute("books", bookService.findAll());
        model.addAttribute("authentication", userPrincipal);

        System.out.println(bookTypeService.findAll());

        return "home";
    }

    @GetMapping("/post")
    public String pushBook(Model model, @ModelAttribute("book details ") BookDto bookDto, BookTypeDto bookTypeDto) {


        return "book-details";
    }
    @GetMapping("/check-keystore")
    public ResponseEntity<String> checkKeystore() throws IOException {
        Resource resource = new ClassPathResource("docs/keystore.p12");
        return ResponseEntity.ok(
                resource.exists() ? "Found at: " + resource.getURI() : "NOT FOUND"
        );
    }
}
