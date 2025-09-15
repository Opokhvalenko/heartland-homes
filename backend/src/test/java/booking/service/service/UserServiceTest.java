package booking.service.service;

import static booking.service.util.UserUtil.createUpdateUserProfileRequestDto;
import static booking.service.util.UserUtil.createUpdateUserRoleRequestDto;
import static booking.service.util.UserUtil.createUpdatedUserResponseDto;
import static booking.service.util.UserUtil.createUser;
import static booking.service.util.UserUtil.createUserRegistrationRequestDto;
import static booking.service.util.UserUtil.createUserResponseDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import booking.service.dto.user.UpdateUserProfileRequestDto;
import booking.service.dto.user.UpdateUserRoleRequestDto;
import booking.service.dto.user.UserRegistrationRequestDto;
import booking.service.dto.user.UserResponseDto;
import booking.service.exception.EntityNotFoundException;
import booking.service.exception.RegistrationException;
import booking.service.mapper.UserMapper;
import booking.service.model.Role;
import booking.service.model.RoleName;
import booking.service.model.User;
import booking.service.repository.role.RoleRepository;
import booking.service.repository.user.UserRepository;
import booking.service.service.impl.UserServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserResponseDto userResponseDto;
    private Role customerRole;

    @BeforeEach
    void setUp() {
        user = createUser(1L);
        userResponseDto = createUserResponseDto(1L);
        customerRole = new Role().setName(RoleName.CUSTOMER);
    }

    @Test
    void register_success() throws RegistrationException {
        UserRegistrationRequestDto requestDto = createUserRegistrationRequestDto();

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByName(RoleName.CUSTOMER)).thenReturn(Optional.of(customerRole));
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.register(requestDto);

        assertThat(result).isEqualTo(userResponseDto);
        assertThat(user.getPassword()).isEqualTo("encodedPassword");
        assertThat(user.getRoles()).containsExactly(customerRole);

        verify(userRepository).save(user);
    }

    @Test
    void register_throwsRegistrationExceptionIfEmailExists() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto()
                .setEmail("test@example.com");

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.register(requestDto))
                .isInstanceOf(RegistrationException.class)
                .hasMessageContaining("Email already in use");
    }

    @Test
    void register_throwsEntityNotFoundExceptionIfRoleNotFound() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto()
                .setEmail("test@example.com")
                .setPassword("password");

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByName(RoleName.CUSTOMER)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.register(requestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Role not found");
    }

    @Test
    void getCurrentUserId_returnsUserIdFromSecurityContext() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getPrincipal()).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Long userId = userService.getCurrentUserId();

        assertThat(userId).isEqualTo(user.getId());
    }

    @Test
    void updateUserRole_success() {
        Long userId = 1L;
        UpdateUserRoleRequestDto requestDto = createUpdateUserRoleRequestDto("MANAGER");
        Role managerRole = new Role().setName(RoleName.MANAGER);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(RoleName.MANAGER)).thenReturn(Optional.of(managerRole));
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.updateUserRole(userId, requestDto);

        assertThat(user.getRoles()).containsExactly(managerRole);
        assertThat(result).isEqualTo(userResponseDto);
        verify(userRepository).save(user);
    }

    @Test
    void updateUserRole_throwsEntityNotFoundExceptionIfUserNotFound() {
        Long userId = 1L;
        UpdateUserRoleRequestDto requestDto = createUpdateUserRoleRequestDto("MANAGER");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUserRole(userId, requestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void updateUserRole_throwsEntityNotFoundExceptionIfRoleNotFound() {
        Long userId = 1L;
        UpdateUserRoleRequestDto requestDto = createUpdateUserRoleRequestDto("MANAGER");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(RoleName.MANAGER)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUserRole(userId, requestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Role not found");
    }

    @Test
    void getCurrentUserProfile_success() {
        Long userId = 1L;

        UserServiceImpl spyUserService = Mockito.spy(userService);
        doReturn(userId).when(spyUserService).getCurrentUserId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = spyUserService.getCurrentUserProfile();

        assertThat(result).isEqualTo(userResponseDto);
    }

    @Test
    void getCurrentUserProfile_throwsEntityNotFoundException() {
        Long userId = 1L;

        UserServiceImpl spyUserService = Mockito.spy(userService);
        doReturn(userId).when(spyUserService).getCurrentUserId();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(spyUserService::getCurrentUserProfile)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void updateCurrentUserProfile_success() {
        Long userId = 1L;
        UpdateUserProfileRequestDto updateDto = createUpdateUserProfileRequestDto();
        UserResponseDto updatedResponseDto = createUpdatedUserResponseDto(userId);

        UserServiceImpl spyUserService = Mockito.spy(userService);
        doReturn(userId).when(spyUserService).getCurrentUserId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(updatedResponseDto);

        UserResponseDto result = spyUserService.updateCurrentUserProfile(updateDto);

        assertThat(user.getFirstName()).isEqualTo(updateDto.getFirstName());
        assertThat(user.getLastName()).isEqualTo(updateDto.getLastName());
        assertThat(user.getEmail()).isEqualTo(updateDto.getEmail());
        assertThat(result).isEqualTo(updatedResponseDto);

        verify(userRepository).save(user);
    }

    @Test
    void updateCurrentUserProfile_throwsEntityNotFoundException() {
        Long userId = 1L;
        UpdateUserProfileRequestDto updateDto = createUpdateUserProfileRequestDto();

        UserServiceImpl spyUserService = Mockito.spy(userService);
        doReturn(userId).when(spyUserService).getCurrentUserId();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> spyUserService.updateCurrentUserProfile(updateDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found");
    }
}

