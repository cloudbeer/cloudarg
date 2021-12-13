package arche.cloud.netty.model;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.google.gson.annotations.SerializedName;

import arche.cloud.netty.utils.GsonUtil;

public class User {

    public String toString() {
        return GsonUtil.toString(this);
    }

    public String toHeaderString() {
        return Base64.getEncoder().encodeToString(toString().getBytes(StandardCharsets.UTF_8));
    }

    private Long id;
    @SerializedName("open_id")
    private String openId;
    private String mobile;
    private String nick;
    private String email;
    private String[] roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }
}
