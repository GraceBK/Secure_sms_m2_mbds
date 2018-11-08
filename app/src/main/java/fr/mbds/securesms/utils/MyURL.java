package fr.mbds.securesms.utils;

public enum MyURL {

    SIGNUP ("/api/createUser"),
    SIGNIN ("/api/login");

    private String url;
    private final String host = "http://baobab.tokidev.fr";

    MyURL(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return this.host + "" + url;
    }
}
