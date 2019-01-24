package com.swagger.app.model;

/**
 * Created by imabh on 23-01-2019.
 */

public class Document {
    public String docName="";
    public String docId="";

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
    @Override
    public String toString() {
        return this.docName;
    }
}
