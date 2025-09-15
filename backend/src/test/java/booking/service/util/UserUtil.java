package booking.service.util;

import booking.service.dto.user.UpdateUserProfileRequestDto;
import booking.service.dto.user.UpdateUserRoleRequestDto;
import booking.service.dto.user.UserLoginRequestDto;
import booking.service.dto.user.UserRegistrationRequestDto;
import booking.service.dto.user.UserResponseDto;
import booking.service.model.Role;
import booking.service.model.RoleName;
import booking.service.model.User;
import java.util.Set;

public class UserUtil {

    public static UserResponseDto createUserResponseDto(Long id) {
        return new UserResponseDto()
                .setId(id)
                .setEmail("test@example.com")
                .setFirstName("Test")
                .setLastName("User")
                .setRole("MANAGER");
    }

    public static UserResponseDto createUpdatedUserResponseDto(Long id) {
        return new UserResponseDto()
                .setId(id)
                .setEmail("updated@example.com")
                .setFirstName("Updated")
                .setLastName("User")
                .setRole("MANAGER");
    }

    public static UpdateUserRoleRequestDto createUpdateUserRoleRequestDto(String role) {
        UpdateUserRoleRequestDto dto = new UpdateUserRoleRequestDto();
        dto.setRole(role);
        return dto;
    }

    public static UpdateUserProfileRequestDto createUpdateUserRoleRequestDtoWithEmptyName() {
        return new UpdateUserProfileRequestDto()
                .setFirstName("")
                .setLastName("ValidLastName");
    }

    public static UpdateUserProfileRequestDto createUpdateUserProfileRequestDto() {
        UpdateUserProfileRequestDto dto = new UpdateUserProfileRequestDto();
        dto.setEmail("updated@example.com");
        dto.setFirstName("Updated");
        dto.setLastName("User");
        return dto;
    }

    public static UpdateUserRoleRequestDto createUpdateUserInvalidRoleRequestDto() {
        return new UpdateUserRoleRequestDto()
                .setRole(null);
    }

    public static UserRegistrationRequestDto createRegistrationRequestWithShortPassword() {
        return new UserRegistrationRequestDto()
                .setEmail("user@example.com")
                .setFirstName("John")
                .setLastName("Doe")
                .setPassword("123")
                .setRepeatPassword("123");
    }

    public static UserResponseDto createUserResponseDtoWithShortPassword(Long id) {
        return new UserResponseDto()
                .setId(id)
                .setEmail("user@example.com")
                .setFirstName("John")
                .setLastName("Doe");
    }

    public static User createUser(Long id) {
        return new User()
                .setId(id)
                .setEmail("test@example.com")
                .setFirstName("Test")
                .setLastName("User");
    }

    public static User createManagerUser(Long id) {
        return new User()
                .setId(id)
                .setEmail("manager@example.com")
                .setPassword("password")
                .setFirstName("Manager")
                .setLastName("User")
                .setRoles(Set.of(new Role().setName(RoleName.MANAGER)));
    }

    public static UserRegistrationRequestDto createUserRegistrationRequestDto() {
        return new UserRegistrationRequestDto()
                .setEmail("test@example.com")
                .setPassword("password")
                .setFirstName("Test")
                .setLastName("User");
    }

    public static UserRegistrationRequestDto createEmptyRegistrationRequest() {
        return new UserRegistrationRequestDto();
    }

    public static UserLoginRequestDto createEmptyLoginRequest() {
        return new UserLoginRequestDto();
    }

    public static UserLoginRequestDto createLoginRequestWithInvalidEmailFormat() {
        return new UserLoginRequestDto()
                .setEmail("invalid-email")
                .setPassword("somePassword123");
    }

    public static UserLoginRequestDto createLoginRequest() {
        return new UserLoginRequestDto()
                .setEmail("test@example.com")
                .setPassword("password");
    }

}
