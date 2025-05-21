package com.globits.hr.dto;

public class SimpleAttachment {
    private String filename;
    private String contentType;
    private byte[] content;

    public SimpleAttachment(String filename, String contentType, byte[] content) {
        this.filename = filename;
        this.contentType = contentType;
        this.content = content;
    }

    public String getFilename() {
        return filename;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getContent() {
        return content;
    }
}
