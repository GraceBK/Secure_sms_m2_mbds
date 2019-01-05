package fr.mbds.securesms.utils;

public enum MyURL {

    CREATE_USER ("/api/createUser"),
    LOGIN ("/api/login"),
    SEND_SMS ("/api/sendMsg"),
    GET_SMS ("/api/fetchMessages"),
    IS_VALID_TOKEN ("/api/validate");

    private String url;
    private final String HOST = "http://baobab.tokidev.fr";

    MyURL(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return this.HOST + "" + url;
    }
}
