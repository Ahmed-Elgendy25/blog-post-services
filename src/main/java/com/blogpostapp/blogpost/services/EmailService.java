package com.blogpostapp.blogpost.services;

import java.util.List;

public interface EmailService {
    void sendUpdateToSubscribers(String title, String url);
    void addSubscriber(String email);
    void removeSubscriber(String email);

    // Expose for controllers/clients (avoids casting to impl)
    List<String> getAllSubscribers();
    int getSubscriberCount();
}