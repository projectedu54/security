package com.security.dto;

public class UserDetailsResponse {
    private Integer userDetailId;
    private String uid;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String contactNo;
    private String roleName;
    private String userType;

    // Constructors
    public UserDetailsResponse() {}

    public UserDetailsResponse(Integer userDetailId, String uid, String firstName, String middleName,
                               String lastName, String email, String contactNo,
                               String roleName, String userType) {
        this.userDetailId = userDetailId;
        this.uid = uid;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.contactNo = contactNo;
        this.roleName = roleName;
        this.userType = userType;
    }

    // Getters and Setters (or use Lombok if preferred)
    public Integer getUserDetailId() { return userDetailId; }
    public void setUserDetailId(Integer userDetailId) { this.userDetailId = userDetailId; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContactNo() { return contactNo; }
    public void setContactNo(String contactNo) { this.contactNo = contactNo; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
}
