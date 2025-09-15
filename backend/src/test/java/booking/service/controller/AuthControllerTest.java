package booking.service.controller;

import static booking.service.util.UserUtil.createEmptyLoginRequest;
import static booking.service.util.UserUtil.createEmptyRegistrationRequest;
import static booking.service.util.UserUtil.createLoginRequestWithInvalidEmailFormat;
import static booking.service.util.UserUtil.createRegistrationRequestWithShortPassword;
import static booking.service.util.UserUtil.createUserResponseDtoWithShortPassword;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import booking.service.dto.user.UserLoginRequestDto;
import booking.service.dto.user.UserLoginResponseDto;
import booking.service.dto.user.UserRegistrationRequestDto;
import booking.service.dto.user.UserResponseDto;
import booking.service.security.AuthenticationService;
import booking.service.service.UserService;
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
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerTest {

    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    private DataSource dataSource;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthenticationService authenticationService;

    @BeforeAll
    void beforeAll() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();

        teardownDb();
    }

    @BeforeEach
    void beforeEach() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/insert-users-and-roles.sql"));
        }
    }

    @AfterEach
    void afterEach() {
        teardownDb();
    }

    @SneakyThrows
    void teardownDb() {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/delete-users-and-roles.sql"));
        }
    }

    @Test
    @DisplayName("Register with valid data returns 200 and user DTO")
    void register_ValidRequest_ReturnsOk() throws Exception {
        UserRegistrationRequestDto request = createRegistrationRequestWithShortPassword();
        request.setPassword("validPass123");
        request.setRepeatPassword("validPass123");
        UserResponseDto response = createUserResponseDtoWithShortPassword(4L);

        when(userService.register(any(UserRegistrationRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/auth/registration")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Register with invalid email returns 400")
    void register_InvalidEmail_ReturnsBadRequest() throws Exception {
        UserRegistrationRequestDto request = createRegistrationRequestWithShortPassword();
        request.setEmail("invalid-email");

        mockMvc.perform(post("/auth/registration")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Register with mismatched passwords returns 400")
    void register_MismatchedPasswords_ReturnsBadRequest() throws Exception {
        UserRegistrationRequestDto request = createRegistrationRequestWithShortPassword();
        request.setRepeatPassword("differentPassword");

        mockMvc.perform(post("/auth/registration")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Register with empty request returns 400")
    void register_EmptyRequest_ReturnsBadRequest() throws Exception {
        UserRegistrationRequestDto request = createEmptyRegistrationRequest();

        mockMvc.perform(post("/auth/registration")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Login with valid credentials returns token")
    void login_ValidCredentials_ReturnsToken() throws Exception {
        UserLoginRequestDto request = new UserLoginRequestDto()
                .setEmail("test@example.com")
                .setPassword("password");

        UserLoginResponseDto response = new UserLoginResponseDto("token123");

        when(authenticationService.authenticate(any(UserLoginRequestDto.class))).thenReturn(
                response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Login with invalid credentials returns 401")
    void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        UserLoginRequestDto request = createLoginRequestWithInvalidEmailFormat();
        request.setEmail("user@example.com");
        request.setPassword("wrongPassword");

        when(authenticationService.authenticate(any(UserLoginRequestDto.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Login with invalid credentials returns 401 - BadCredentialsException")
    void login_InvalidCredentials_BadCredentialsException_ReturnsUnauthorized() throws Exception {
        UserLoginRequestDto request = createLoginRequestWithInvalidEmailFormat();
        request.setEmail("user@example.com");
        request.setPassword("wrongPassword");

        when(authenticationService.authenticate(any(UserLoginRequestDto.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Login with empty request returns 400")
    void login_EmptyRequest_ReturnsBadRequest() throws Exception {
        UserLoginRequestDto request = createEmptyLoginRequest();

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
