package com.blogpostapp.blogpost.services;

public interface EmailService {
    void sendUpdateToSubscribers(String title, String url);
    void addSubscriber(String email);
    void removeSubscriber(String email);
}