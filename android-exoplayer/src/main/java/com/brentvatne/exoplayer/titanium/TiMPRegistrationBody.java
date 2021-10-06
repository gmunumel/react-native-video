package com.brentvatne.exoplayer.titanium;

import com.google.gson.annotations.SerializedName;

class TiMPRegistrationBody {
    @SerializedName("LatensRegistration")
    TiMPRegistration tiMPRegistration;

    @SerializedName("Payload")
    String payload;

    @SerializedName("AuthToken")
    String authToken;
}
