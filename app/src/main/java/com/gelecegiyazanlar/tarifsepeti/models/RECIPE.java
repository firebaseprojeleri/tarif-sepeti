package com.gelecegiyazanlar.tarifsepeti.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by serdar on 24.07.2016
 */

public class RECIPE {

    public String title;
    public String body;
    public String imgLink;
    public  int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();
//    public String author;
//    public String timeStamp;
    public String category;

    public RECIPE() {
    }

    public RECIPE(String title, String body, String imgLink, String category) {

        this.title = title;
        this.body = body;
        this.imgLink = imgLink;
        this.category = category;

    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImgLink() {
        return imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
