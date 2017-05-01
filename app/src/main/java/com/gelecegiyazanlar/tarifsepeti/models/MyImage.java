package com.gelecegiyazanlar.tarifsepeti.models;


/**
 * Created by serdar on 26.07.2016
 */

public class MyImage {

    String imgName;
    String imgLink;

    public MyImage() {
    }

    public MyImage(String imgName, String imgLink) {
        this.imgName = imgName;
        this.imgLink = imgLink;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getImgLink() {
        return imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }

}
