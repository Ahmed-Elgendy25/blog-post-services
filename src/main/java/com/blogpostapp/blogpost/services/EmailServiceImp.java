package com.blogpostapp.blogpost.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.blogpostapp.blogpost.dao.SubscribersRepository;
import com.blogpostapp.blogpost.entities.SubscribersEntity;

@Service
public class EmailServiceImp implements EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImp.class);
    
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SubscribersRepository subscribersRepository;
    
    @Value("${spring.mail.username:noreply@blogpost.com}")
    private String fromEmail;
    
    @Value("${email.mock.enabled:false}")
    private boolean mockEmailEnabled;

    @Override
    public void sendUpdateToSubscribers(String title, String url) {
        List<String> subscribers = subscribersRepository.findAllEmails();

        if (subscribers.isEmpty()) {
            logger.info("No subscribers to notify for post: {}", title);
            return;
        }

        logger.info("Sending notification to {} subscribers for post: {}", subscribers.size(), title);

        int success = 0;
        int fail = 0;

        for (String subscriberEmail : subscribers) {
            try {
                sendEmailToSubscriber(subscriberEmail, title, url);
                logger.debug("Notification sent successfully to: {}", subscriberEmail);
                success++;
            } catch (MailAuthenticationException e) {
                logger.error("Failed to send notification to {}: Email authentication failed. Check SMTP username/password or app password.", subscriberEmail);
                fail++;
            } catch (Exception e) {
                logger.error("Failed to send notification to {}: {}", subscriberEmail, e.getMessage());
                fail++;
            }
        }

        logger.info("Email sending completed for post '{}': {} successful, {} failed", title, success, fail);
    }
    
    @Override
    public void addSubscriber(String email) {
        if (email == null || !email.contains("@")) {
            logger.warn("Invalid email address provided: {}", email);
            return;
        }
        String normalized = email.toLowerCase().trim();
        if (subscribersRepository.existsByEmailIgnoreCase(normalized)) {
            logger.info("Subscriber already exists: {}", normalized);
            return;
        }
        subscribersRepository.save(new SubscribersEntity(normalized));
        logger.info("Added new subscriber: {}", normalized);
    }
    
    @Override
    public void removeSubscriber(String email) {
        if (email == null || email.trim().isEmpty()) {
            logger.warn("Email is required to remove subscriber");
            return;
        }
        long deleted = subscribersRepository.deleteByEmailIgnoreCase(email.trim());
        if (deleted > 0) {
            logger.info("Removed subscriber: {}", email);
        } else {
            logger.warn("Subscriber not found: {}", email);
        }
    }
    
    private void sendEmailToSubscriber(String email, String title, String url) {
        if (mockEmailEnabled) {
            logger.info("MOCK EMAIL - Would send to {} | Subject: New Blog Post: {}", email, title);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("New Blog Post: " + title);
        message.setText(buildEmailContent(title, url));

        // Let exceptions bubble up to be logged once in the caller
        mailSender.send(message);
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
    
    // Utility methods backed by the repository
    @Override
    public List<String> getAllSubscribers() {
        return subscribersRepository.findAllEmails();
    }
    
    @Override
    public int getSubscriberCount() {
        return (int) subscribersRepository.count();
    }
}