package fr.mbds.securesms.models;

import android.support.annotation.NonNull;

public class User {

    private String username;
    private String resume;
    private String thumbnail;

    public User(String username, String resume) {
        this.username = username;
        this.resume = resume;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }


    @NonNull
    @Override
    public String toString() {
        return "User { "+ getUsername() +" , "+ getResume() +" }";
    }
}
