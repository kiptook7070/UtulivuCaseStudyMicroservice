package com.eclectics.usersservice.Security.jwt;

import com.eclectics.usersservice.utils.Session.Activesession;

public class CurrentUserContext {
    private static ThreadLocal<Activesession> currentActiveUser = new InheritableThreadLocal<>();

    public static Activesession getCurrentActiveUser() {
        return currentActiveUser.get();
    }

    public static void setCurrentActiveUser(Activesession entity) {
        currentActiveUser.set(entity);
    }

    public static void clear() {
        currentActiveUser.set(null);
    }
}