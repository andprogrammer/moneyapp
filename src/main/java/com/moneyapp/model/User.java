package com.moneyapp.model;

import java.util.UUID;

public class User {

    private String id;
    private String name;
    private String email;

    public User(String name, String email) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        User user = (User) o;
//        if (!id.equals(user.id))
//            return false;
        if (!name.equals(user.name))
            return false;
        return email.equals(user.email);
    }

    @Override
    public String toString() {
        return "Name=" + name + " Email=" + email;
    }
}
