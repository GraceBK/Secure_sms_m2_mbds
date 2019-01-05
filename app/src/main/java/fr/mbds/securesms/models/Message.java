package fr.mbds.securesms.models;

import android.support.annotation.NonNull;

public class Message {

    private String username;
    private String resume;
    private boolean currentUser;

    public Message(String username, String resume, boolean currentUser) {
        this.username = username;
        this.resume = resume;
        this.currentUser = currentUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public boolean isCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(boolean currentUser) {
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public String toString() {
        return "User { "+ getUsername() +" , "+ getResume() +" }";
    }
}
