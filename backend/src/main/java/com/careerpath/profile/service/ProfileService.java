package com.careerpath.profile.service;

import com.careerpath.auth.domain.AppUser;
import com.careerpath.auth.domain.AppUserRepository;
import com.careerpath.profile.api.ProfileController.ProfileResponse;
import com.careerpath.profile.api.ProfileController.UpdateProfileRequest;
import com.careerpath.profile.domain.StudentProfile;
import com.careerpath.profile.domain.StudentProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
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
    public ProfileResponse uploadCv(String email, MultipartFile file) {
        if (file.isEmpty() || file.getSize() > 5_000_000 || !isPdf(file)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Upload a PDF CV smaller than 5 MB.");
        StudentProfile profile = findProfile(email);
        try {
            java.nio.file.Path directory = java.nio.file.Path.of("uploads", "cvs"); java.nio.file.Files.createDirectories(directory);
            String storedName = profile.getId() + "-cv.pdf";
            java.nio.file.Files.copy(file.getInputStream(), directory.resolve(storedName), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            profile.attachCv(storedName); return toResponse(profileRepository.save(profile));
        } catch (java.io.IOException exception) { throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not store your CV. Try again."); }
    }
    public CvTextResponse getCvText(String email) { return new CvTextResponse(findProfile(email).getCvText()); }
    public CvTextResponse updateCvText(String email, String text) { StudentProfile profile=findProfile(email); profile.updateCvText(optionalValue(text)); profileRepository.save(profile); return new CvTextResponse(profile.getCvText()); }

    private StudentProfile findProfile(String email) {
        AppUser user = userForEmail(email);
        return profileRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile was not found."));
    }

    private ProfileResponse toResponse(StudentProfile profile) {
        return new ProfileResponse(profile.getFullName(), profile.getUniversity(), profile.getAcademicYear(),
                profile.getGithubUrl(), profile.getLinkedinUrl(), profile.getTargetCareer(), profile.getCvFileName());
    }

    private String optionalValue(String value) {
        return value == null ? "" : value.trim();
    }
    private boolean isPdf(MultipartFile file) { String name = file.getOriginalFilename(); return (file.getContentType() != null && file.getContentType().equalsIgnoreCase("application/pdf")) || (name != null && name.toLowerCase(java.util.Locale.ROOT).endsWith(".pdf")); }

    public AppUser userForEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
    public record CvTextResponse(String text) { }
}
