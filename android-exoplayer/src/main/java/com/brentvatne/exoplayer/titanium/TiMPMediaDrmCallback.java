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

        Log.d(TAG, "TiMPMediaDrmCallback");
        Log.d(TAG, "defaultLicenseUrl " + defaultLicenseUrl);
        Log.d(TAG, "portalId " + portalId);
        Log.d(TAG, "customerName " + customerName);
        Log.d(TAG, "dataSourceFactory " + dataSourceFactory);
    }

    private String createLatensRegistration(byte[] payload) {
        Log.d(TAG, "createLatensRegistration");
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
        //String requestBody = "eyJhbGciOiJBMTI4S1ciLCJlbmMiOiJBMTI4R0NNIiwia2lkIjoiOTMyMjJmMTItYzdjZi00ZWE2LTgwMzAtNDgyMDUyOWFkNjE3IiwiaXNzIjoiTXVsdGlUcnVzdCIsImV4cCI6IjE2MzMwODY4NjU5NzMiLCJpYXQiOiIxNjMzMDg2ODA1OTczIiwidmVyIjoiMiJ9Cg.XaRaY3_LIs8WBehhfBkLog.UAMxsosW5pBewSfHYKlatg.faLV1f5U6yOI1IrGCHLMebM3E_0HARbwenGOtsZGSfeBIZWIjk64TbMs21LOD4dxM6ExfTa1BsorTKyMOlOV9R7Ex37t7dRSvlYF9wsc_3LBhQSy7L8mIkdUsSZCjPGmoA_quwj5gX55MGWD12rawygG8uTqNVGZ5ZtDdOK6Vmmfap4O-mr37e1rJhh0U_SWMCOV53tnZQSP85K4XtBWFZvamoEamFBnIOxHTDQmC2KiVEm35KFV2PiblbYfBdR2ONRRq3JncmQpB5l5teryj8_HhCHXBOkc8PwN19_p-RuMhbMKQN3FdvVFxnvGYu168fVKRLO6rg9nxyOjioUWKFuUDnKaI_Bl1rFFs72llvNR_gZR_xZ2vZMnbPLROVolMLNR_m3PeF-_0DdxSIKdwMC-I9avrVTaytCpkQZbqI1da_lLowBv1rMxePAslkJWTr8pHLlGKyiq7B8NW4pg1xXQlTzU6HIKFigVThmipIPd8_EWwgM9A8_e5wFjJ_8hfi8W9FcYlUaKk4TTsQIlq6K9Rqmq2nenEqiVruBLQcyhDyquQ8Qbr0TSi2eUEZTwrtfZcSzD5lQqyAneWs3LPjLQxYBQiKP4qxpO0nLQLucxkYz0dtiu8_Q66pPmc687BD7b-RURte_g1N57f2dEqg5Zfe2GD1E58DtnRMk0SLVH1kPKmBzfICuXapRfK7cBv58MkM_tserG_G5T_61z2I6Pp3CxgVh3qEn2gbIur989rJoiVkjHOgcba3LRu2XijsjGMFwG5jAOlumC26BT-LfutA5m9QxFo07MzQL0bzPs7Kl-3pbl9DF9IjCA7tO7MruL7-50BriBwpQjfrevO9q3Wvg3296eG6wNiLglacvYRlH6FHYqYdKEliOz6Asm4MfFzq9AuHIDa3b_t0lij6b8Ie5vg5ssR75uKaGevmJB0GjLGZ2HSerj_B3KrzE944A4ziX3lxiEB2sOnhlJPTWlqPg7Wx9KozuxhgOEUSrXFwaWGwMuUsfqIVOicI3o5R17THu1JymBzKyCfzVoKeMo_b2-2jSqYqsWszuKEGELdtnRNhBOzEQhq1kuJMwRrdWdUqJJrLRjqGMq2FG1dPYVpsLGHofS8Pd02wZjRI1fZPDZHh8Kng82icD9zajdNMQOKC8Rkml33myL_cIc7KAD9mmrINoC5egB7p0mlQyCLA_QfLQfn7MWYK-gKsOziXXVP3m1zKhSA7gbzsj1.MC4HKn4hGlFPRbdM7ngFqA";
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
