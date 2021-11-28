package arche.cloud.netty.model;

import com.google.gson.annotations.SerializedName;

public class Ticket {
    @SerializedName("open_id")
    private String openId;
    @SerializedName("expires_in")
    private long expiresIn;
    private String sign;


    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
