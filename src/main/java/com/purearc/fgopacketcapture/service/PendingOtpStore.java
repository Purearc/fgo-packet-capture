package com.purearc.fgopacketcapture.service;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PendingOtpStore {
    private static final Duration TTL = Duration.ofMinutes(5);
    private final Map<String, PendingOtp> entries = new ConcurrentHashMap<>();

    public void put(String mobile, String upstreamResponse) { entries.put(mobile, new PendingOtp(upstreamResponse, Instant.now())); }

    public Optional<String> get(String mobile) {
        PendingOtp value = entries.get(mobile);
        if (value == null) return Optional.empty();
        if (value.createdAt.plus(TTL).isBefore(Instant.now())) {
            entries.remove(mobile);
            return Optional.empty();
        }
        return Optional.of(value.response);
    }

    public void remove(String mobile) { entries.remove(mobile); }

    private static class PendingOtp {
        private final String response;
        private final Instant createdAt;
        private PendingOtp(String response, Instant createdAt) { this.response = response; this.createdAt = createdAt; }
    }
}
