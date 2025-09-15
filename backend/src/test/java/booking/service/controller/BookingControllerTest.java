package booking.service.controller;

import static booking.service.util.BookingUtil.createBookingRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import booking.service.config.WithMockCustomUser;
import booking.service.dto.booking.BookingDto;
import booking.service.dto.booking.CreateBookingRequestDto;
import booking.service.model.BookingStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.time.LocalDate;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Sql(scripts = {
        "classpath:database/delete-bookings.sql",
        "classpath:database/delete-accommodations.sql",
        "classpath:database/delete-users-and-roles.sql",
        "classpath:database/insert-users-and-roles.sql",
        "classpath:database/insert-accommodations.sql",
        "classpath:database/insert-bookings.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:database/delete-bookings.sql",
        "classpath:database/delete-accommodations.sql",
        "classpath:database/delete-users-and-roles.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookingControllerTest {

    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext context,
            @Autowired DataSource dataSource) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
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
                    new ClassPathResource("database/delete-bookings.sql"));
        }
    }

    @WithMockCustomUser(id = 2L, username = "test2@example.com", roles = {"CUSTOMER"})
    @Test
    @DisplayName("Create booking with valid request")
    void createBooking_ValidRequest_ReturnsCreatedBooking() throws Exception {
        CreateBookingRequestDto requestDto = createBookingRequestDto();

        MvcResult result = mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        BookingDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookingDto.class);

        assertEquals(requestDto.getAccommodationId(), actual.getAccommodationId());
        assertEquals(BookingStatus.PENDING, actual.getStatus());
        assertNotNull(actual.getId());
        assertEquals(2L, actual.getUserId());
    }

    @WithMockCustomUser(id = 2L, username = "customer", roles = {"CUSTOMER"})
    @Test
    @DisplayName("Get booking by valid ID")
    void getBookingById_ValidId_ReturnsBookingDto() throws Exception {
        MvcResult result = mockMvc.perform(get("/bookings/{id}", 1L))
                .andExpect(status().isOk())
                .andReturn();

        BookingDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookingDto.class);

        assertEquals(1L, actual.getId());
        assertEquals("CONFIRMED", actual.getStatus().name());
    }

    @WithMockCustomUser(id = 2L, username = "test2@example.com", roles = {"CUSTOMER"})
    @Test
    @DisplayName("Update booking with valid request")
    void updateBooking_ValidId_ReturnsUpdatedBooking() throws Exception {
        CreateBookingRequestDto requestDto = new CreateBookingRequestDto()
                .setCheckInDate(LocalDate.now().plusDays(10))
                .setCheckOutDate(LocalDate.now().plusDays(15))
                .setAccommodationId(1L); // залишаємо старе житло

        MvcResult result = mockMvc.perform(put("/bookings/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        BookingDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookingDto.class);

        assertEquals(1L, actual.getId());
        assertEquals(BookingStatus.CONFIRMED, actual.getStatus());
        assertEquals(requestDto.getCheckInDate(), actual.getCheckInDate());
        assertEquals(requestDto.getCheckOutDate(), actual.getCheckOutDate());
    }
}
