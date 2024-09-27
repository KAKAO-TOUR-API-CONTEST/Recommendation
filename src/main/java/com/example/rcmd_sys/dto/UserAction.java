package com.example.rcmd_sys.dto;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserAction {
    private Long userId;       // 사용자 ID
    private Long storeId;      // 상점 ID
    private String actionType;  // 행동 유형 (click, bookmark, registration)
    private String timestamp;   // 행동이 발생한 시간
}
