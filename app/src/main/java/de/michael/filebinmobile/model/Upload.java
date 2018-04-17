package de.michael.filebinmobile.model;

public class Upload {

    private String uploadTitle, uploadSize;
    private String thumbnail, hash, mimeType, id;
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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getUploadTimeStamp() {
        return uploadTimeStamp;
    }
}
