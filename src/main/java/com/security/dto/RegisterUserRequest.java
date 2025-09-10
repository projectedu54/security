package com.security.dto;

import lombok.Data;

@Data
public class RegisterUserRequest {
    private String userId;
    private String password;
    private Integer userRole;
    private Integer userTypeId;
    private String firstName;
    private String lastName;
    private String sex; // "Male", "Female", "Other"
    private String idProof;
    private String contactNo;
    private String street;
    private String area;
    private String pin;
    private String state;
    private String country;
}
