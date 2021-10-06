package com.brentvatne.exoplayer.titanium;

import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.drm.ExoMediaDrm;
import com.google.android.exoplayer2.drm.MediaDrmCallback;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.UUID;

import com.google.android.exoplayer2.upstream.DataSourceInputStream;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;

public class TiMPMediaDrmCallback implements MediaDrmCallback {

    private static final String TAG = "TiMPMediaDrmCallback";

    private final HttpDataSource.Factory dataSourceFactory;

    private String defaultLicenseUrl;

    private String portalId;

    private String customerName;

    public TiMPMediaDrmCallback(String defaultLicenseUrl, String portalId, String customerName, HttpDataSource.Factory dataSourceFactory) {
        this.defaultLicenseUrl = defaultLicenseUrl;
        this.portalId = portalId;
        this.customerName = customerName;
        this.dataSourceFactory = dataSourceFactory;
    }

    private String createLatensRegistration(byte[] payload) {
        TiMPDeviceInfo deviceInfo = new TiMPDeviceInfo();
        TiMPRegistration tiMPRegistration = new TiMPRegistration();
        tiMPRegistration.customerName = this.customerName;
        tiMPRegistration.accountName = "PlayReadyAccount";
        tiMPRegistration.portalId = this.portalId;
        tiMPRegistration.friendlyName = "tadaam";
        tiMPRegistration.deviceInfo = deviceInfo;
        TiMPRegistrationBody body = new TiMPRegistrationBody();
        body.tiMPRegistration = tiMPRegistration;
        try {
            body.payload = new String(Base64.encode(payload, 0), "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        String jsonBody = gson.toJson(body);
        Log.d(TAG, jsonBody);
        return Base64.encodeToString(jsonBody.getBytes(), Base64.DEFAULT);
    }

    @Override
    public byte[] executeProvisionRequest(UUID uuid, ExoMediaDrm.ProvisionRequest request) throws IOException {
        String url = request.getDefaultUrl() + "&signedRequest=" + new String(request.getData());
        return executePost(dataSourceFactory, url, new byte[0], null);
    }

    @Override
    public byte[] executeKeyRequest(UUID uuid, ExoMediaDrm.KeyRequest request) throws Exception {
        String url = defaultLicenseUrl;
        String requestBody = createLatensRegistration(request.getData());
        byte[] keyResponse = executePost(dataSourceFactory, url, requestBody.getBytes(), null);
        Gson gson = new Gson();
        String response = new String(keyResponse);
        TiMPLicenseResponse license = gson.fromJson(response, TiMPLicenseResponse.class);
        return Base64.decode(license.license, Base64.DEFAULT);
    }

    private static byte[] executePost(HttpDataSource.Factory dataSourceFactory, String url,
                                      byte[] data, Map<String, String> requestProperties) throws IOException {
        HttpDataSource dataSource = dataSourceFactory.createDataSource();
        if (requestProperties != null) {
            for (Map.Entry<String, String> requestProperty : requestProperties.entrySet()) {
                dataSource.setRequestProperty(requestProperty.getKey(), requestProperty.getValue());
            }
        }
        DataSpec dataSpec = new DataSpec(Uri.parse(url), data, 0, 0, C.LENGTH_UNSET, null,
                DataSpec.FLAG_ALLOW_GZIP);
        DataSourceInputStream inputStream = new DataSourceInputStream(dataSource, dataSpec);
        try {
            return Util.toByteArray(inputStream);
        } finally {
            Util.closeQuietly(inputStream);
        }
    }
}
