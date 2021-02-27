package com.tistory.starcue.cuetalk;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class AdressRoomItem {

    public String name;
    public String sex;
    public String age;
    public String km;
    public String pic;

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
