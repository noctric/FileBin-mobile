package de.michael.filebinmobile.model;

public class MultiPasteUpload  {
    private String urlId;
    private long date;
    private String[] ids;

    public MultiPasteUpload(String urlId, long date, String[] ids) {
        this.urlId = urlId;
        this.date = date;
        this.ids = ids;
    }

    public String getUrlId() {
        return urlId;
    }

    public long getDate() {
        return date;
    }

    public String[] getIds() {
        return ids;
    }
}
