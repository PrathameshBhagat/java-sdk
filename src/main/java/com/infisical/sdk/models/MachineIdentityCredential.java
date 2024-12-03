package com.infisical.sdk.models;


import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MachineIdentityCredential {

    @SerializedName("accessToken")
    private String accessToken = null;

    @SerializedName("expiresIn")
    private BigDecimal expiresIn = null;

    @SerializedName("accessTokenMaxTTL")
    private BigDecimal accessTokenMaxTTL = null;

    @SerializedName("tokenType")
    private String tokenType = null;
}