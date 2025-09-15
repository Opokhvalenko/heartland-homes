package booking.service.service.impl;

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
import booking.service.service.UserService;
import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder getPasswordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("Email already in use: " + requestDto.getEmail());
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(getPasswordEncoder.encode(user.getPassword()));

        Role userRole = roleRepository.findByName(RoleName.CUSTOMER)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: "
                        + RoleName.CUSTOMER));
        user.setRoles(Set.of(userRole));
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public Long getCurrentUserId() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getId();
    }

    @Override
    public UserResponseDto updateUserRole(Long userId, UpdateUserRoleRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found by id: " + userId));
        Role role = roleRepository.findByName(RoleName.valueOf(requestDto.getRole()))
                .orElseThrow(() -> new EntityNotFoundException("Role not found: "
                        + requestDto.getRole()));
        user.getRoles().clear();
        user.getRoles().add(role);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto getCurrentUserProfile() {
        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found by id: " + userId));
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto updateCurrentUserProfile(UpdateUserProfileRequestDto requestDto) {
        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found by id: " + userId));
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        user.setEmail(requestDto.getEmail());
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public Page<UserResponseDto> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }
}
