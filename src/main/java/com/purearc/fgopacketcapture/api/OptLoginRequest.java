package com.purearc.fgopacketcapture.api;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class OptLoginRequest {
    @NotBlank
    @Pattern(regexp = "^[0-9]{5,20}$", message = "手机号格式不正确")
    private String mobile;
    @NotBlank
    @Pattern(regexp = "^[0-9]{6}$", message = "验证码必须是 6 位数字")
    private String otp;

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
}
