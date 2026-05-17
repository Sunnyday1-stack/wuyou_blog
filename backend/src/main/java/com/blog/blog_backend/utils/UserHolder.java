package com.blog.blog_backend.utils;

import com.blog.blog_backend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserHolder {
    private static final ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    public static void setUser(User user) {
        userThreadLocal.set(user);
    }

    public static User getUser() {
        return userThreadLocal.get();
    }

    public static void remove() {
        userThreadLocal.remove();
    }

    public static User getCurrentUser() {
        return getUser();
    }
}