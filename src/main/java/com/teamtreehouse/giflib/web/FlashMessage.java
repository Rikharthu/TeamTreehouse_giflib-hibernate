package com.teamtreehouse.giflib.web;

public class FlashMessage {

    private String message;

    private Status status;

    public FlashMessage(String message, Status status) {
        this.message = message;
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {

        return message;
    }

    public static enum Status{
        SUCCESS, FAILURE
    }


}
