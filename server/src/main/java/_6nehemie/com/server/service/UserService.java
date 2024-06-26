package _6nehemie.com.server.service;

import _6nehemie.com.server.dto.ResponseDto;
import _6nehemie.com.server.dto.address.AddressResponseDto;
import _6nehemie.com.server.dto.avatar.UpdateAvatarResponseDto;
import _6nehemie.com.server.dto.s3.S3UploadDto;
import _6nehemie.com.server.dto.user.*;
import _6nehemie.com.server.enums.Registration;
import _6nehemie.com.server.exception.BadRequestException;
import _6nehemie.com.server.exception.NotFoundException;
import _6nehemie.com.server.model.Address;
import _6nehemie.com.server.model.User;
import _6nehemie.com.server.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final CodeGeneratorService codeGeneratorService;
    private final EmailService emailService;
    private final S3Service s3Service;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthService authService, CodeGeneratorService codeGeneratorService, EmailService emailService, S3Service s3Service) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
        this.codeGeneratorService = codeGeneratorService;
        this.emailService = emailService;
        this.s3Service = s3Service;
    }


    /**
     * Get user infroamtion
     * @param userDetails - the user details of the authenticated user
     * @return - the user response
     */
    public GetUserResponseDto getUser(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new NotFoundException("User not found!"));


        return new GetUserResponseDto(
                user.getId(),
                user.getAvatar(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUsername(),
                user.getDescription(),
                user.isVerified(),
                user.getAuthType(),
                user.getAddress()
        );
    }

    /**
     * Update the name of a user.
     * @param userDetails The details of the authenticated user.
     * @param request The request object containing the new first and last names.
     * @return A FullNameResponseDto object containing the updated first and last names, a success message, and the HTTP status.
     */
    public FullNameResponseDto updateName(UserDetails userDetails, UpdateNameDto request) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new NotFoundException("User not found!"));

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        userRepository.save(user);

        return new FullNameResponseDto(
                user.getFirstName(),
                user.getLastName(),
                "Name updated successfully!",
                HttpStatus.OK.value()
        );
    }

    /**
     * Updates the description of a user.
     *
     * @param userDetails The details of the authenticated user.
     * @param request The request object containing the new description.
     * @return A DescriptionResponseDto object containing the updated description, a success message, and the HTTP status.
     * @throws NotFoundException If the user is not found.
     */
    public DescriptionResponseDto updateDescription(UserDetails userDetails, UpdateDescription request) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new NotFoundException("User not found!"));

        user.setDescription(request.description());
        userRepository.save(user);

        return new DescriptionResponseDto(
                user.getDescription(),
                "Description updated successfully!",
                HttpStatus.OK.value()
        );
    }

    /**
     * Updates the email of a user.
     *
     * @param userDetails The details of the authenticated user.
     * @param request The request object containing the new email and the user's password.
     * @return An EmailResponseDto object containing the updated email, a success message, and the HTTP status.
     * @throws NotFoundException If the user is not found.
     * @throws BadRequestException If the user's authentication type is not CREDENTIALS, the password is invalid, or the new email already exists.
     *
     */
    public EmailResponseDto updateEmail(UserDetails userDetails, UpdateEmailDto request) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new NotFoundException("User not found!"));

        if (!user.getAuthType().equals(Registration.CREDENTIALS)) {
            throw new BadRequestException("Email cannot be updated!");
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadRequestException("Invalid password!");
        }

        if (userRepository.existsByEmail(request.email()) && !user.getEmail().equals(request.email())) {
            throw new BadRequestException("Email already exists!");
        }

        user.setEmail(request.email());
        user.setUsername(request.email());
        user.setVerified(false);
        user.setVerificationCode(codeGeneratorService.generateSixDigitCode());

        userRepository.save(user);

        //? Send verification code to user
        emailService.sendEmail(
                user.getEmail(),
                "Verification Code",
                "Your verification code is: " + user.getVerificationCode()
        );


        // To make the user login back again
        authService.revokeAllTokens(user);

        return new EmailResponseDto(
                user.getEmail(),
                "Email updated successfully!",
                HttpStatus.OK.value()
        );
    }

    /**
     * Updates the password of a user.
     *
     * @param userDetails The details of the authenticated user.
     * @param request The request object containing the old and new passwords.
     * @return A PasswordResponseDto object containing a success message and the HTTP status.
     * @throws NotFoundException If the user is not found.
     * @throws BadRequestException If the user's authentication type is not CREDENTIALS, the old password is invalid.
     */
    public PasswordResponseDto updatePassword(UserDetails userDetails, UpdatePasswordDto request) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new NotFoundException("User not found!"));

        if (!user.getAuthType().equals(Registration.CREDENTIALS)) {
            throw new BadRequestException("Password cannot be updated!");
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadRequestException("Invalid old password!");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        return new PasswordResponseDto(
                "Password updated successfully!",
                HttpStatus.OK.value()
        );
    }

    /**
     * Updates the address of a user.
     * @param userDetails The details of the authenticated user.
     * @param request The request object containing the new address.
     * @return
     */
    public AddressResponseDto updateAddress(UserDetails userDetails, UpdateAddressDto request) {

        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new NotFoundException("User not found!"));

        Address address = new Address();

        address.setStreet(request.street());
        address.setCity(request.city());
        address.setState(request.state());
        address.setCountry(request.country());
        address.setZip(request.zip());

        user.setAddress(address);

        System.out.println(user);

        userRepository.save(user);

        return new AddressResponseDto(
                address,
                "Address updated successfully!",
                HttpStatus.OK.value()
        );
    }

    /**
     * Deletes the address of a user.
     * @param userDetails The details of the authenticated user.
     * @return An AddressResponseDto object containing a success message and the HTTP status.
     */
    public AddressResponseDto deleteAddress(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new NotFoundException("User not found!"));

        user.setAddress(null);
        userRepository.save(user);

        return new AddressResponseDto(
                null,
                "Address deleted successfully!",
                HttpStatus.OK.value()
        );
    }

    /**
     * Deletes a user.
     * @param userDetails The details of the authenticated user.
     * @return A ResponseDto object containing a success message and the HTTP status.
     */
    public ResponseDto deleteUser(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new NotFoundException("User not found!"));
        
        userRepository.delete(user);

        return new ResponseDto(
                "User deleted successfully!",
                HttpStatus.OK.value()
        );
    }

    /**
     * Updates the avatar of a user.
     * @param userDetails The details of the authenticated user.
     * @param avatar The new avatar.
     * @return An UpdateAvatarResponseDto object containing the new avatar URL, a success message, and the HTTP status.
     */
    public UpdateAvatarResponseDto updateAvatar(UserDetails userDetails, MultipartFile avatar) {

        // Find user by username
        Optional<User> optionalUser = userRepository.findByUsername(userDetails.getUsername());
        User user = optionalUser.orElseThrow(() -> new NotFoundException("User not found"));

        // Remove previous avatar from S3

        //? Check if user has an avatar and delete it from S3
        if (user.getAvatar() != null && user.getAvatarKey() != null) {
            s3Service.deleteFile(user.getAvatarKey());
        }

        // Upload new avatar to S3
        S3UploadDto avatarUrl = s3Service.uploadFile(avatar);

        //? Update the new user avatar URL and KEY in the database
        user.setAvatar(avatarUrl.fileUrl());
        user.setAvatarKey(avatarUrl.fileKey());
        userRepository.save(user);

        return new UpdateAvatarResponseDto(user.getAvatar(), "Avatar was updated", HttpStatus.CREATED.value());
    }

    /**
     * Deletes the avatar of a user.
     * @param userDetails The details of the authenticated user.
     * @return An UpdateAvatarResponseDto object containing a success message and the HTTP status.
     */
    public UpdateAvatarResponseDto deleteAvatar(UserDetails userDetails) {

        // Find user by username
        Optional<User> optionalUser = userRepository.findByUsername(userDetails.getUsername());
        User user = optionalUser.orElseThrow(() -> new NotFoundException("User not found"));

        //? Check if user has an avatar and delete it from S3
        if (user.getAvatarKey() != null) {
            s3Service.deleteFile(user.getAvatarKey());
        }

        //? Update the new user avatar URL and KEY in the database
        user.setAvatar(null);
        user.setAvatarKey(null);
        userRepository.save(user);

        return new UpdateAvatarResponseDto(null,"Avatar was deleted", HttpStatus.OK.value());
    }
}
