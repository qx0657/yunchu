package fun.qianxiao.yunchu.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class UserInfo implements Serializable {
    private long id;
    private String qq;
    private String mobile;
    private String username;
    private String email;
    @SerializedName("a-grade")
    private String a_grade;
    private Date registered_time;
    private String appid;
    private String appkey;

    public UserInfo() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getA_grade() {
        return a_grade;
    }

    public void setA_grade(String a_grade) {
        this.a_grade = a_grade;
    }

    public Date getRegistered_time() {
        return registered_time;
    }

    public void setRegistered_time(Date registered_time) {
        this.registered_time = registered_time;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", qq='" + qq + '\'' +
                ", mobile='" + mobile + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", a_grade='" + a_grade + '\'' +
                ", registered_time=" + registered_time +
                ", appid='" + appid + '\'' +
                ", appkey='" + appkey + '\'' +
                '}';
    }
}
