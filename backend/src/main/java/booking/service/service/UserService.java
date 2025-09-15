package booking.service.service;

import booking.service.dto.user.UpdateUserProfileRequestDto;
import booking.service.dto.user.UpdateUserRoleRequestDto;
import booking.service.dto.user.UserRegistrationRequestDto;
import booking.service.dto.user.UserResponseDto;
import booking.service.exception.RegistrationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;

    Long getCurrentUserId();

    UserResponseDto updateUserRole(Long userId, UpdateUserRoleRequestDto requestDto);

    UserResponseDto getCurrentUserProfile();

    UserResponseDto updateCurrentUserProfile(UpdateUserProfileRequestDto requestDto);

    Page<UserResponseDto> findAll(Pageable pageable);
}
