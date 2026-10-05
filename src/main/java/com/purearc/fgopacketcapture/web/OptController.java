package com.purearc.fgopacketcapture.web;

import com.purearc.fgopacketcapture.api.ApiResponse;
import com.purearc.fgopacketcapture.api.OptLoginRequest;
import com.purearc.fgopacketcapture.api.OptSendRequest;
import com.purearc.fgopacketcapture.service.FgoUpstreamService;
import com.purearc.fgopacketcapture.service.PendingOtpStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@Validated
public class OptController {
    private final FgoUpstreamService upstream;
    private final PendingOtpStore otpStore;

    public OptController(FgoUpstreamService upstream, PendingOtpStore otpStore) { this.upstream = upstream; this.otpStore = otpStore; }

    @PostMapping("/opt-send")
    public ResponseEntity<ApiResponse<String>> send(@Valid @RequestBody OptSendRequest request) {
        String response = upstream.send(request.getOtp_channel_no());
        otpStore.put(request.getOtp_channel_no(), response);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/opt-login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody OptLoginRequest request) {
        String sendResponse = otpStore.get(request.getMobile()).orElseThrow(() -> new IllegalStateException("未找到该手机号的有效发送验证码记录，请先发送验证码"));
        String response = upstream.login(request.getMobile(), request.getOtp(), sendResponse);
        otpStore.remove(request.getMobile());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ApiResponse.fail(ex.getMessage()));
    }
}
