package com.brentvatne.exoplayer.titanium;
import android.os.Build;

import com.google.gson.annotations.SerializedName;

class TiMPDeviceInfo {
    @SerializedName("FormatVersion")
    String formatVersion = "1";

    @SerializedName("DeviceType")
    String deviceType = "Device";

    @SerializedName("OSType")
    String osType = Build.VERSION.RELEASE;

    @SerializedName("OSVersion")
    String osVersion = Integer.toString(Build.VERSION.SDK_INT);

    @SerializedName("DRMType")
    String drmType = "Widevine CDM";

    @SerializedName("DRMProvider")
    String drmProvider = "Google";

    @SerializedName("DRMVersion")
    String drmVersion = "1.0";

    @SerializedName("DeviceVendor")
    String deviceVendor = Build.MANUFACTURER;

    @SerializedName("DeviceModel")
    String deviceModel = Build.MODEL;
}
