package com.purearc.fgopacketcapture.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.purearc.fgopacketcapture.config.FgoUpstreamProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class FgoUpstreamService {
    private final RestTemplate restTemplate;
    private final FgoUpstreamProperties properties;
    private final ObjectMapper objectMapper;

    public FgoUpstreamService(RestTemplate restTemplate, FgoUpstreamProperties properties, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public String send(String mobile) {
        Map<String, String> params = new LinkedHashMap<>(properties.getCommonParams());
        params.put("otp_channel_no", mobile);
        return post(properties.getSendUrl(), params);
    }

    public String login(String mobile, String otp, String sendResponse) {
        Map<String, String> params = new LinkedHashMap<>(properties.getCommonParams());
        params.put("mobile", mobile);
        params.put("otp", otp);
        String captchaKey = findText(sendResponse, "captcha_key");
        if (captchaKey != null) params.put("captcha_key", captchaKey);
        return post(properties.getLoginUrl(), params);
    }

    private String post(String url, Map<String, String> params) {
        if (!properties.isEnabled()) throw new IllegalStateException("上游转发未启用，请先在 application.yml 配置 fgo.upstream.enabled=true");
        if (url == null || url.isBlank()) throw new IllegalStateException("上游 URL 未配置");
        HttpHeaders headers = new HttpHeaders();
        properties.getHeaders().forEach(headers::set);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        params.forEach(form::add);
        try {
            return restTemplate.postForObject(url, new HttpEntity<>(form, headers), String.class);
        } catch (RestClientException ex) {
            throw new IllegalStateException("上游请求失败: " + ex.getMessage(), ex);
        }
    }

    private String findText(String raw, String field) {
        if (raw == null || raw.isBlank()) return null;
        try { return findText(objectMapper.readTree(raw), field); } catch (Exception ignored) { return null; }
    }

    private String findText(JsonNode node, String field) {
        if (node == null) return null;
        if (node.isObject()) {
            JsonNode value = node.get(field);
            if (value != null && value.isValueNode()) return value.asText();
            for (JsonNode child : node) { String found = findText(child, field); if (found != null) return found; }
        } else if (node.isArray()) {
            for (JsonNode child : node) { String found = findText(child, field); if (found != null) return found; }
        }
        return null;
    }
}
