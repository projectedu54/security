package com.security.controller;

import com.security.dto.UserDetailsRequest;
import com.security.dto.UserDetailsResponse;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;

@RestController
@RequestMapping("${project.name}/userDetails")
public class UserDetailsController {

    private final UserRepository userRepository;
    private final UserTypeRepository userTypeRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserDetailsRepository userDetailsRepository;

    // Constructor (manually added in place of @RequiredArgsConstructor)
    public UserDetailsController(
            UserRepository userRepository,
            UserTypeRepository userTypeRepository,
            UserProfileRepository userProfileRepository,
            UserDetailsRepository userDetailsRepository
    ) {
        this.userRepository = userRepository;
        this.userTypeRepository = userTypeRepository;
        this.userProfileRepository = userProfileRepository;
        this.userDetailsRepository = userDetailsRepository;
    }

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
                userDetails.setVerificationDate(Instant.now());
            }

            userDetailsRepository.save(userDetails);

            return ResponseEntity.ok("User details saved successfully.");
        } catch (DataIntegrityViolationException ex) {
            String rootCause = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
            if (rootCause != null && rootCause.contains("uk_uid")) {
                throw new DuplicateResourceException("UID '" + request.getUid() + "' already exists.");
            }
            throw ex; // rethrow if it's a different constraint
        }
    }

    // Get user details by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDetailsResponse> getUserDetails(@PathVariable Integer id) {
        UserDetails userDetails = userDetailsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User details not found for ID: " + id));

        UserDetailsResponse response = new UserDetailsResponse(
                userDetails.getUserDetailId(),
                userDetails.getUid(),
                userDetails.getFirstName(),
                userDetails.getMiddleName(),
                userDetails.getLastName(),
                userDetails.getEmail(),
                userDetails.getContactNo(),
                userDetails.getUser().getRole().getRoleName(),
                userDetails.getUserType().getTypeName()
        );

        return ResponseEntity.ok(response);
    }


    // we are updating user details using id
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserDetails(@PathVariable Integer id, @RequestBody UserDetailsRequest request) {
        UserDetails userDetails = userDetailsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserDetails not found with id: " + id));

        // Update foreign key entities if provided
        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invalid user ID: " + request.getUserId()));
            userDetails.setUser(user);
        }

        if (request.getUserTypeId() != null) {
            UserType userType = userTypeRepository.findById(request.getUserTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invalid user type ID: " + request.getUserTypeId()));
            userDetails.setUserType(userType);
        }

        if (request.getProfileId() != null) {
            UserProfile profile = userProfileRepository.findById(request.getProfileId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invalid profile ID: " + request.getProfileId()));
            userDetails.setUserProfile(profile);
        }

        // Update other fields
        if (request.getUid() != null) userDetails.setUid(request.getUid());
        if (request.getFirstName() != null) userDetails.setFirstName(request.getFirstName());
        if (request.getMiddleName() != null) userDetails.setMiddleName(request.getMiddleName());
        if (request.getLastName() != null) userDetails.setLastName(request.getLastName());
        if (request.getSex() != null) {
            userDetails.setSex(UserDetails.Gender.valueOf(request.getSex().replace(" ", "_")));
        }
        if (request.getIdProof() != null) userDetails.setIdProof(request.getIdProof());
        if (request.getContactNo() != null) userDetails.setContactNo(request.getContactNo());
        if (request.getEmail() != null) userDetails.setEmail(request.getEmail());
        if (request.getStreet() != null) userDetails.setStreet(request.getStreet());
        if (request.getArea() != null) userDetails.setArea(request.getArea());
        if (request.getPin() != null) userDetails.setPin(request.getPin());
        if (request.getState() != null) userDetails.setState(request.getState());
        if (request.getCountry() != null) userDetails.setCountry(request.getCountry());
        if (request.getProfileCompletion() != null) userDetails.setProfileCompletion(request.getProfileCompletion());
        if (request.getIsVerified() != null) {
            userDetails.setIsVerified(request.getIsVerified());
            if (request.getIsVerified()) {
                userDetails.setVerificationDate(Instant.now());
            } else {
                userDetails.setVerificationDate(null);
            }
        }

        userDetailsRepository.save(userDetails);
        return ResponseEntity.ok("User details updated successfully.");
    }
}
