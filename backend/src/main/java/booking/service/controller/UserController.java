package booking.service.controller;

import booking.service.dto.user.UpdateUserProfileRequestDto;
import booking.service.dto.user.UpdateUserRoleRequestDto;
import booking.service.dto.user.UserResponseDto;
import booking.service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User management", description = "Endpoints for managing users")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}/role")
    @Operation(summary = "Update user role",
            description = "Update the role of a specific user by their ID")
    public UserResponseDto updateUserRole(@PathVariable Long id,
            @RequestBody @Valid UpdateUserRoleRequestDto requestDto) {
        return userService.updateUserRole(id, requestDto);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user's profile",
            description = "Retrieve the profile information of the currently authenticated user")
    public UserResponseDto getCurrentUserProfile() {
        return userService.getCurrentUserProfile();
    }

    @Operation(summary = "Update current user's profile",
            description = "Update profile information of the currently authenticated user")
    @PutMapping("/me")
    public UserResponseDto updateCurrentUserProfile(
            @RequestBody @Valid UpdateUserProfileRequestDto requestDto) {
        return userService.updateCurrentUserProfile(requestDto);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping
    @Operation(summary = "Get all users (for managers only)",
            description = "Retrieve a paginated list of all users.")
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        return userService.findAll(pageable);
    }
}
