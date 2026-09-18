 package com.shopbear.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshRequest {

    private String refreshToken; // Refresh Token Client gửi lên để xin token mới.
}
