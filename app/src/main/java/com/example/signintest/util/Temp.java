package com.example.signintest.util;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class Temp extends LitePalSupport {
    private float tempture;
    @Column(defaultValue = "unknown")
    private String gettime;

    public float getTempture() {
        return tempture;
    }

    public String getGettime() {
        return gettime;
    }

    public void setTempture(float tempture) {
        this.tempture = tempture;
    }

    public void setGettime(String gettime) {
        this.gettime = gettime;
    }
}
