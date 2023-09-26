package com.shopspace.storage.dto;

public class FileDTO {
    private String base64;
    private String folder;

    public FileDTO() {
    }

    public FileDTO(String base64, String folder) {
        this.base64 = base64;
        this.folder = folder;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    @Override
    public String toString() {
        return "FileDTO{" +
                "base64='" + base64 + '\'' +
                ", folder='" + folder + '\'' +
                '}';
    }
}
