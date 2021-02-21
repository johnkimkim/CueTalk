package com.tistory.starcue.cuetalk;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class AdressRoomItem {

    public String name;
    public String sex;
    public String age;

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



    public AdressRoomItem() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AdressRoomItem(String name, String sex, String age) {
        this.name = name;
        this.sex = sex;
        this.age = age;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("sex", sex);
        result.put("age", age);
        return result;
    }



}
