package com.security.controller;

import com.security.dto.UserDetailsRequest;
import com.security.entity.User;
import com.security.entity.UserDetails;
import com.security.entity.UserProfile;
import com.security.entity.UserType;
import com.security.exception.customException.DuplicateResourceException;
import com.security.exception.customException.ResourceNotFoundException;
import com.security.repository.UserDetailsRepository;
import com.security.repository.UserProfileRepository;
import com.security.repository.UserRepository;
import com.security.repository.UserTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;

@RestController
@RequestMapping("${project.name}/userDetails")
@RequiredArgsConstructor
public class UserDetailsController {

    private final UserRepository userRepository;
    private final UserTypeRepository userTypeRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserDetailsRepository userDetailsRepository;

    @PostMapping
    public ResponseEntity<?> createUserDetails(@RequestBody UserDetailsRequest request) {

        try {

            // Fetch and validate foreign keys with custom exceptions
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invalid user ID: " + request.getUserId()));

            UserType userType = userTypeRepository.findById(request.getUserTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invalid user type ID: " + request.getUserTypeId()));

            UserProfile profile = userProfileRepository.findById(request.getProfileId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invalid profile ID: " + request.getProfileId()));

            // Build entity
            UserDetails userDetails = new UserDetails();
            userDetails.setUser(user);
            userDetails.setUserType(userType);
            userDetails.setUserProfile(profile);
            userDetails.setUid(request.getUid());
            userDetails.setFirstName(request.getFirstName());
            userDetails.setMiddleName(request.getMiddleName());
            userDetails.setLastName(request.getLastName());

            if (request.getSex() != null) {
                userDetails.setSex(UserDetails.Gender.valueOf(request.getSex().replace(" ", "_")));
            }

            userDetails.setIdProof(request.getIdProof());
            userDetails.setContactNo(request.getContactNo());
            userDetails.setEmail(request.getEmail());
            userDetails.setStreet(request.getStreet());
            userDetails.setArea(request.getArea());
            userDetails.setPin(request.getPin());
            userDetails.setState(request.getState());
            userDetails.setCountry(request.getCountry() != null ? request.getCountry() : "India");
            userDetails.setProfileCompletion(request.getProfileCompletion() != null ? request.getProfileCompletion() : 0);
            userDetails.setIsVerified(request.getIsVerified() != null && request.getIsVerified());

            if (userDetails.getIsVerified()) {
                userDetails.setVerificationDate(Timestamp.from(java.time.Instant.now()));
            }

            userDetailsRepository.save(userDetails);

            return ResponseEntity.ok("User details saved successfully.");
        }catch (DataIntegrityViolationException ex) {
            String rootCause = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
            if (rootCause != null && rootCause.contains("uk_uid")) {
                throw new DuplicateResourceException("UID '" + request.getUid() + "' already exists.");
            }
            throw ex; // rethrow if it's a different constraint
        }
    }
}