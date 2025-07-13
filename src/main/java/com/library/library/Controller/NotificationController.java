package com.library.library.Controller;

import com.library.library.Model.Notification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class NotificationController {

    // this function to check the security
    @PreAuthorize("#notification.recipient.id == principal.id")
    @GetMapping("/notifications/{id}")
    public Notification getNotification(@PathVariable Notification notification) {
        return notification;
    }

//    FIXME when user click in notification he go here to get him to the url
//    @GetMapping("/notifications/{notificationId}/redirect")
//    public String redirectToBook(@PathVariable Long notificationId) {
//        Notification notification = notificationRepository.findById(notificationId)
//                .orElseThrow(() -> new NotFoundException("Notification not found"));
//
//        Book book = bookRepository.findById(notification.getBookId())
//                .orElseThrow(() -> new ChangeSetPersister.NotFoundException("Book not found"));
//
//        // Generate SEO-friendly URL
//        String url = String.format("/%s/%s/%d",
//                book.getDisplayName(),
//                book.getKey(),
//                book.getId());
//
//        return "redirect:" + url;
//    }

}
