package com.blogpostapp.blogpost.services;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImp implements EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImp.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username:noreply@blogpost.com}")
    private String fromEmail;
    
    // In-memory storage for subscribers (in production, use database)
    private final Set<String> subscribers = ConcurrentHashMap.newKeySet();
    
    // Default subscribers for demo purposes
    public EmailServiceImp() {
        // Add some default demo subscribers
        subscribers.add("subscriber1@example.com");
        subscribers.add("subscriber2@example.com");
        subscribers.add("admin@blogpost.com");
    }
    
    @Override
    public void sendUpdateToSubscribers(String title, String url) {
        if (subscribers.isEmpty()) {
            logger.info("No subscribers to notify for post: {}", title);
            return;
        }
        
        logger.info("Sending notification to {} subscribers for post: {}", subscribers.size(), title);
        
        for (String subscriberEmail : subscribers) {
            try {
                sendEmailToSubscriber(subscriberEmail, title, url);
                logger.debug("Notification sent successfully to: {}", subscriberEmail);
            } catch (Exception e) {
                logger.error("Failed to send notification to {}: {}", subscriberEmail, e.getMessage());
            }
        }
        
        logger.info("Finished sending notifications for post: {}", title);
    }
    
    @Override
    public void addSubscriber(String email) {
        if (email != null && email.contains("@")) {
            subscribers.add(email.toLowerCase().trim());
            logger.info("Added new subscriber: {}", email);
        } else {
            logger.warn("Invalid email address provided: {}", email);
        }
    }
    
    @Override
    public void removeSubscriber(String email) {
        if (email != null) {
            boolean removed = subscribers.remove(email.toLowerCase().trim());
            if (removed) {
                logger.info("Removed subscriber: {}", email);
            } else {
                logger.warn("Subscriber not found: {}", email);
            }
        }
    }
    
    private void sendEmailToSubscriber(String email, String title, String url) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("New Blog Post: " + title);
            message.setText(buildEmailContent(title, url));
            
            mailSender.send(message);
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", email, e.getMessage());
            throw e;
        }
    }
    
    private String buildEmailContent(String title, String url) {
        return String.format("""
            Hello!
            
            A new blog post has been published:
            
            Title: %s
            
            Read it here: %s
            
            Thank you for subscribing to our blog!
            
            Best regards,
            The Blog Team
            
            ---
            To unsubscribe, please contact support.
            """, title, url);
    }
    
    // Utility methods for managing subscribers
    public List<String> getAllSubscribers() {
        return List.copyOf(subscribers);
    }
    
    public int getSubscriberCount() {
        return subscribers.size();
    }
}