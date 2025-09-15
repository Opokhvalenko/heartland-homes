package booking.service.controller;

import static booking.service.util.PaymentUtil.createPaymentDto2;
import static booking.service.util.PaymentUtil.createPaymentRequestDto;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import booking.service.config.WithMockCustomUser;
import booking.service.dto.payment.CreatePaymentRequestDto;
import booking.service.dto.payment.PaymentDto;
import booking.service.dto.payment.PaymentResponseDto;
import booking.service.service.StripeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
        "classpath:database/delete-payments.sql",
        "classpath:database/delete-bookings.sql",
        "classpath:database/delete-accommodations.sql",
        "classpath:database/delete-users-and-roles.sql",
        "classpath:database/insert-users-and-roles.sql",
        "classpath:database/insert-accommodations.sql",
        "classpath:database/insert-bookings.sql",
        "classpath:database/insert-payments.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:database/delete-payments.sql",
        "classpath:database/delete-bookings.sql",
        "classpath:database/delete-accommodations.sql",
        "classpath:database/delete-users-and-roles.sql"
},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class PaymentControllerTest {

    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StripeService stripeService;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext context,
            @Autowired DataSource dataSource) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
    }

    @BeforeEach
    void setUp() {
        when(stripeService.createStripeSession(any(), any(), any(), any()))
                .thenReturn(new PaymentResponseDto("sess_1", "http://example.com/success1"));
    }

    @AfterEach
    void afterEach(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/delete-payments.sql"));
        }
    }

    @WithMockCustomUser(id = 1L, username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Create payment with valid data")
    void createPayment_ValidRequest_ReturnsPaymentDto() throws Exception {
        CreatePaymentRequestDto request = createPaymentRequestDto();
        PaymentDto expected = createPaymentDto2(4L);

        MvcResult result = mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        PaymentDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                PaymentDto.class);

        assertTrue(reflectionEquals(expected, actual, "id", "sessionUrl", "sessionId",
                "amountToPay"));
    }
}
