package com.purearc.fgopacketcapture.api;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class OptSendRequest {
    @NotBlank
    @Pattern(regexp = "^[0-9]{5,20}$", message = "手机号格式不正确")
    private String otp_channel_no;

    public String getOtp_channel_no() { return otp_channel_no; }
    public void setOtp_channel_no(String otp_channel_no) { this.otp_channel_no = otp_channel_no; }
}
