package com.security.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "user_details_tbl")
public class UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_detail_id")
    private Integer userDetailId;

    @Column(name = "uid", nullable = false, unique = true)
    private String uid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_tbl_fk", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_type_id", nullable = false)
    private UserType userType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private UserProfile userProfile;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "sex")
    @Enumerated(EnumType.STRING)
    private Gender sex;

    @Column(name = "id_proof")
    private String idProof;

    @Column(name = "contact_no")
    private String contactNo;

    @Column(name = "email")
    private String email;

    @Column(name = "street")
    private String street;

    @Column(name = "area")
    private String area;

    @Column(name = "pin")
    private String pin;

    @Column(name = "state")
    private String state;

    @Column(name = "country")
    private String country = "India";

    @Column(name = "profile_completion", nullable = false)
    private Short profileCompletion = 0;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @Column(name = "verification_date")
    private Instant verificationDate;

    @Column(name = "created_at", updatable = false, insertable = false)
    private Instant createdAt;

    @Column(name = "updated_at", insertable = false)
    private Instant updatedAt;



    public UserDetails() {
    }

    public UserDetails(Integer userDetailId, String uid, User user, UserType userType, UserProfile userProfile,
                       String firstName, String middleName, String lastName, Gender sex, String idProof,
                       String contactNo, String email, String street, String area, String pin, String state,
                       String country, Short profileCompletion, Boolean isVerified, Instant verificationDate,
                       Instant createdAt, Instant updatedAt) {
        this.userDetailId = userDetailId;
        this.uid = uid;
        this.user = user;
        this.userType = userType;
        this.userProfile = userProfile;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.sex = sex;
        this.idProof = idProof;
        this.contactNo = contactNo;
        this.email = email;
        this.street = street;
        this.area = area;
        this.pin = pin;
        this.state = state;
        this.country = country;
        this.profileCompletion = profileCompletion;
        this.isVerified = isVerified;
        this.verificationDate = verificationDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and setters (shortened below for readability, but you can generate them all using your IDE)

    public Integer getUserDetailId() {
        return userDetailId;
    }

    public void setUserDetailId(Integer userDetailId) {
        this.userDetailId = userDetailId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Gender getSex() {
        return sex;
    }

    public void setSex(Gender sex) {
        this.sex = sex;
    }

    public String getIdProof() {
        return idProof;
    }

    public void setIdProof(String idProof) {
        this.idProof = idProof;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Short getProfileCompletion() {
        return profileCompletion;
    }

    public void setProfileCompletion(Short profileCompletion) {
        this.profileCompletion = profileCompletion;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public Instant getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(Instant verificationDate) {
        this.verificationDate = verificationDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public enum Gender {
        Male, Female, Other, Prefer_not_to_say
    }
}