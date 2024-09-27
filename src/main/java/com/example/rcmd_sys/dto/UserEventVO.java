package com.example.rcmd_sys.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class UserEventVO {

    private String timestamp;
    private Long userId;
    private Long storeId;
    private String ActionType;

}

