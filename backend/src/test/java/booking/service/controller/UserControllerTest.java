package booking.service.controller;

import static booking.service.util.UserUtil.createUpdateUserInvalidRoleRequestDto;
import static booking.service.util.UserUtil.createUpdateUserProfileRequestDto;
import static booking.service.util.UserUtil.createUpdateUserRoleRequestDto;
import static booking.service.util.UserUtil.createUpdateUserRoleRequestDtoWithEmptyName;
import static booking.service.util.UserUtil.createUpdatedUserResponseDto;
import static booking.service.util.UserUtil.createUserResponseDto;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import booking.service.config.WithMockCustomUser;
import booking.service.dto.user.UpdateUserProfileRequestDto;
import booking.service.dto.user.UpdateUserRoleRequestDto;
import booking.service.dto.user.UserResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext,
            @Autowired DataSource dataSource
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
    }

    @BeforeEach
    void beforeEach(@Autowired DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/insert-users-and-roles.sql")
            );
        }
    }

    @AfterEach
    void afterEach(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/delete"
                            + "-users-and-roles.sql")
            );
        }
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Update user role with valid ID and role")
    void updateUserRole_ValidRequest_ReturnsUpdatedUser() throws Exception {
        UpdateUserRoleRequestDto requestDto = createUpdateUserRoleRequestDto("MANAGER");
        UserResponseDto expected = createUserResponseDto(1L);

        MvcResult result = mockMvc.perform(put("/users/{id}/role", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                UserResponseDto.class);
        assertTrue(reflectionEquals(expected, actual));
    }

    @WithMockCustomUser(id = 1L, username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Get current user's profile")
    void getCurrentUserProfile_ReturnsUserDto() throws Exception {
        UserResponseDto expected = createUserResponseDto(1L);

        MvcResult result = mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                UserResponseDto.class);
        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @WithMockCustomUser(id = 1L, username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Update current user's profile")
    void updateCurrentUserProfile_ValidRequest_ReturnsUpdatedUser() throws Exception {
        UpdateUserProfileRequestDto requestDto = createUpdateUserProfileRequestDto();
        UserResponseDto expected = createUpdatedUserResponseDto(1L);

        MvcResult result = mockMvc.perform(put("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                UserResponseDto.class);

        System.out.println(expected);
        System.out.println(actual);
        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @WithMockCustomUser(id = 1L, username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Update user role with invalid role name returns 400")
    void updateUserRole_InvalidRole_ReturnsBadRequest() throws Exception {
        UpdateUserRoleRequestDto requestDto = createUpdateUserInvalidRoleRequestDto();

        mockMvc.perform(put("/users/{id}/role", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(id = 1L, username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Update current user profile with empty name returns 400")
    void updateCurrentUserProfile_EmptyName_ReturnsBadRequest() throws Exception {
        UpdateUserProfileRequestDto requestDto = createUpdateUserRoleRequestDtoWithEmptyName();
        mockMvc.perform(put("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(id = 1L, username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Update current user profile with too long last name returns 400")
    void updateCurrentUserProfile_TooLongLastName_ReturnsBadRequest() throws Exception {
        String longLastName = "L".repeat(300);
        UpdateUserProfileRequestDto requestDto = new UpdateUserProfileRequestDto()
                .setFirstName("ValidName")
                .setLastName(longLastName);

        mockMvc.perform(put("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }
}
