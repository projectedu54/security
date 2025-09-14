package com.security.dto;

import lombok.Data;

@Data
public class UserDetailsRequest {
    private Integer userId;
    private Integer userTypeId;
    private String uid;
    private String firstName;
    private String middleName;
    private String lastName;
    private String sex;
    private String idProof;
    private String contactNo;
    private String email;
    private String street;
    private String area;
    private String pin;
    private String state;
    private String country;
    private Short profileCompletion;
    private Integer profileId;
    private Boolean isVerified;
}
