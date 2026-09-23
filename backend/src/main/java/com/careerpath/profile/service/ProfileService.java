package com.careerpath.profile.service;

import com.careerpath.auth.domain.AppUser;
import com.careerpath.auth.domain.AppUserRepository;
import com.careerpath.profile.api.ProfileController.ProfileResponse;
import com.careerpath.profile.api.ProfileController.UpdateProfileRequest;
import com.careerpath.profile.domain.StudentProfile;
import com.careerpath.profile.domain.StudentProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProfileService {
    private final AppUserRepository userRepository;
    private final StudentProfileRepository profileRepository;

    public ProfileService(AppUserRepository userRepository, StudentProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    public ProfileResponse getProfile(String email) {
        return toResponse(findProfile(email));
    }

    public ProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        StudentProfile profile = findProfile(email);
        profile.update(request.fullName().trim(), optionalValue(request.university()), request.academicYear(),
                optionalValue(request.githubUrl()), optionalValue(request.linkedinUrl()), request.targetCareer());
        return toResponse(profileRepository.save(profile));
    }

    private StudentProfile findProfile(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        return profileRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile was not found."));
    }

    private ProfileResponse toResponse(StudentProfile profile) {
        return new ProfileResponse(profile.getFullName(), profile.getUniversity(), profile.getAcademicYear(),
                profile.getGithubUrl(), profile.getLinkedinUrl(), profile.getTargetCareer());
    }

    private String optionalValue(String value) {
        return value == null ? "" : value.trim();
    }
}
