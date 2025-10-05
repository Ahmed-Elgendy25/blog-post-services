package com.blogpostapp.blogpost.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blogpostapp.blogpost.dto.SubscriptionDTO;
import com.blogpostapp.blogpost.services.EmailService;
import com.blogpostapp.blogpost.services.EmailServiceImp;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    
    private final EmailService emailService;

    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/notify-new-post")
    @PreAuthorize("hasAuthority('author')")
    public ResponseEntity<String> notifySubscribers(@RequestBody Map<String, String> body) {
        String title = body.get("title");
        String url = body.get("url");
        
        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Title is required");
        }
        if (url == null || url.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("URL is required");
        }
        
        try {
            emailService.sendUpdateToSubscribers(title, url);
            return ResponseEntity.ok("Emails sent successfully to subscribers");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send emails: " + e.getMessage());
        }
    }
    
    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe(@RequestBody SubscriptionDTO subscriptionDto) {
        if (subscriptionDto.email() == null || !subscriptionDto.email().contains("@")) {
            return ResponseEntity.badRequest().body("Valid email address is required");
        }
        
        try {
            emailService.addSubscriber(subscriptionDto.email());
            return ResponseEntity.ok("Successfully subscribed: " + subscriptionDto.email());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to subscribe: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/unsubscribe")
    public ResponseEntity<String> unsubscribe(@RequestParam String email) {
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email parameter is required");
        }
        
        try {
            emailService.removeSubscriber(email);
            return ResponseEntity.ok("Successfully unsubscribed: " + email);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to unsubscribe: " + e.getMessage());
        }
    }
    
    @GetMapping("/subscribers")
    @PreAuthorize("hasAuthority('author')")
    public ResponseEntity<Map<String, Object>> getSubscribers() {
        try {
            EmailServiceImp emailServiceImp = (EmailServiceImp) emailService;
            List<String> subscribers = emailServiceImp.getAllSubscribers();
            int count = emailServiceImp.getSubscriberCount();
            
            Map<String, Object> response = Map.of(
                "subscribers", subscribers,
                "total", count
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to retrieve subscribers: " + e.getMessage()));
        }
    }
    
    @GetMapping("/subscribers/count")
    public ResponseEntity<Map<String, Integer>> getSubscriberCount() {
        try {
            EmailServiceImp emailServiceImp = (EmailServiceImp) emailService;
            int count = emailServiceImp.getSubscriberCount();
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", -1));
        }
    }
}
