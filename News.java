package com.example.sendy.xmlsaxparsing;

/**
 * Created by Sendy on 27-Aug-16.
 */
public class News {

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDesc() {
        return ShortDesc;
    }

    public void setShortDesc(String shortDesc) {
        ShortDesc = shortDesc;
    }

    String title,ShortDesc;

    @Override
    public String toString() {
        return title+""+ShortDesc;

    }
}
