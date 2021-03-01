package com.tistory.starcue.cuetalk;

import android.widget.Button;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class AdressRoomItem {

    public String name;
    public String sex;
    public String age;
    public String km;
    public String pic;
    public String latitude;
    public String longitude;
    public String uid;
    public Button btn;
    public int ischat;

    public int isIschat() {
        return ischat;
    }

    public void setIschat(int ischat) {
        this.ischat = ischat;
    }

    public Button getBtn() {
        return btn;
    }

    public void setBtn(Button btn) {
        this.btn = btn;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getKm() {
        return km;
    }

    public void setKm(String km) {
        this.km = km;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
