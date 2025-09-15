package booking.service.mapper;

import booking.service.config.MapperConfig;
import booking.service.dto.user.UserRegistrationRequestDto;
import booking.service.dto.user.UserResponseDto;
import booking.service.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    @Mapping(target = "role", expression = "java(getRoleName(user))")
    UserResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto requestDto);

    default String getRoleName(User user) {
        return user.getRoles().stream()
                .findFirst()
                .map(role -> role.getName().name())
                .orElse(null);
    }
}
