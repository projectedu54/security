package com.security.dto;

import lombok.Data;

@Data
public class UserRequest {
    private String userName;
    private String password;
    private Integer roleId;
    private Boolean isActive = true;
}
