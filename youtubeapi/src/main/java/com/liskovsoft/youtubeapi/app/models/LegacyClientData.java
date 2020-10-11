package com.liskovsoft.youtubeapi.app.models;

import com.liskovsoft.youtubeapi.common.converters.regexp.RegExp;

/**
 * Cobalt/Legacy
 */
public class LegacyClientData implements ClientData {
    @RegExp("clientId:\"([-\\w]+\\.apps\\.googleusercontent\\.com)\",[\\w]{2}:\"\\w+\"")
    private String mClientId;

    @RegExp("clientId:\"[-\\w]+\\.apps\\.googleusercontent\\.com\",[\\w]{2}:\"(\\w+)\"")
    private String mClientSecret;

    @Override
    public String getClientId() {
        return mClientId;
    }

    @Override
    public String getClientSecret() {
        return mClientSecret;
    }
}
