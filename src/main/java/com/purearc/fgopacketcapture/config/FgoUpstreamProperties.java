package com.purearc.fgopacketcapture.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "fgo.upstream")
public class FgoUpstreamProperties {
    private boolean enabled;
    private int connectTimeoutMs = 5000;
    private int readTimeoutMs = 15000;
    private String sendUrl;
    private String loginUrl;
    private Map<String, String> headers = new LinkedHashMap<>();
    private Map<String, String> commonParams = new LinkedHashMap<>();

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public int getConnectTimeoutMs() { return connectTimeoutMs; }
    public void setConnectTimeoutMs(int connectTimeoutMs) { this.connectTimeoutMs = connectTimeoutMs; }
    public int getReadTimeoutMs() { return readTimeoutMs; }
    public void setReadTimeoutMs(int readTimeoutMs) { this.readTimeoutMs = readTimeoutMs; }
    public String getSendUrl() { return sendUrl; }
    public void setSendUrl(String sendUrl) { this.sendUrl = sendUrl; }
    public String getLoginUrl() { return loginUrl; }
    public void setLoginUrl(String loginUrl) { this.loginUrl = loginUrl; }
    public Map<String, String> getHeaders() { return headers; }
    public void setHeaders(Map<String, String> headers) { this.headers = headers; }
    public Map<String, String> getCommonParams() { return commonParams; }
    public void setCommonParams(Map<String, String> commonParams) { this.commonParams = commonParams; }
}
