package com.kyc.user.service;

import com.kyc.user.dto.UserDtos.*;
import com.kyc.user.entity.UserProfile;
import com.kyc.user.exception.UserException;
import com.kyc.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserProfileRepository userProfileRepository;

    @Transactional(readOnly = true)
    public UserProfileResponse getUserById(Long id) {
        UserProfile user = userProfileRepository.findById(id)
            .orElseThrow(() -> UserException.notFound("User not found with id: " + id));
        return UserProfileResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserByEmail(String email) {
        UserProfile user = userProfileRepository.findByEmail(email)
            .orElseThrow(() -> UserException.notFound("User not found with email: " + email));
        return UserProfileResponse.from(user);
    }

    @Transactional
    public UserProfileResponse updateUser(Long id, UpdateProfileRequest request, String requestingEmail) {
        UserProfile user = userProfileRepository.findById(id)
            .orElseThrow(() -> UserException.notFound("User not found with id: " + id));

        // Only allow self-update unless admin
        if (!user.getEmail().equals(requestingEmail)) {
            throw UserException.forbidden("You can only update your own profile");
        }

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhone() != null) {
            if (userProfileRepository.existsByPhone(request.getPhone())
                    && !request.getPhone().equals(user.getPhone()))
                throw UserException.conflict("Phone already in use");
            user.setPhone(request.getPhone());
        }
        if (request.getDateOfBirth() != null) user.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getCitizenship() != null) user.setCitizenship(request.getCitizenship());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getCity() != null) user.setCity(request.getCity());
        if (request.getState() != null) user.setState(request.getState());
        if (request.getProfession() != null) user.setProfession(request.getProfession());
        if (request.getAadhaarNumber() != null) {
            if (userProfileRepository.existsByAadhaarNumber(request.getAadhaarNumber())
                    && !request.getAadhaarNumber().equals(user.getAadhaarNumber()))
                throw UserException.conflict("Aadhaar already registered");
            user.setAadhaarNumber(request.getAadhaarNumber());
        }
        if (request.getPanNumber() != null) {
            if (userProfileRepository.existsByPanNumber(request.getPanNumber())
                    && !request.getPanNumber().equals(user.getPanNumber()))
                throw UserException.conflict("PAN already registered");
            user.setPanNumber(request.getPanNumber());
        }
        if (request.getVoterIdNumber() != null) user.setVoterIdNumber(request.getVoterIdNumber());

        return UserProfileResponse.from(userProfileRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id, String requestingEmail) {
        UserProfile user = userProfileRepository.findById(id)
            .orElseThrow(() -> UserException.notFound("User not found with id: " + id));
        if (!user.getEmail().equals(requestingEmail))
            throw UserException.forbidden("You can only delete your own account");
        user.setStatus(UserProfile.UserStatus.INACTIVE);
        userProfileRepository.save(user);
        log.info("User {} soft-deleted by {}", id, requestingEmail);
    }
}
