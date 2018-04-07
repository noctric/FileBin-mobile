package de.michael.filebinmobile.model;

public class Upload {

    private String uploadTitle, uploadSize;
    private long uploadTimeStamp;

    public Upload(String uploadTitle, String uploadSize, long uploadTimeStamp) {
        this.uploadTitle = uploadTitle;
        this.uploadSize = uploadSize;
        this.uploadTimeStamp = uploadTimeStamp;
    }

    public String getUploadTitle() {
        return uploadTitle;
    }

    public String getUploadSize() {
        return uploadSize;
    }

    public long getUploadTimeStamp() {
        return uploadTimeStamp;
    }
}
