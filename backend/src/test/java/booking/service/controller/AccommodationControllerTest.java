package booking.service.controller;

import static booking.service.util.AccommodationUtil.createAccommodationBadRequestDto;
import static booking.service.util.AccommodationUtil.createAccommodationDto;
import static booking.service.util.AccommodationUtil.createAccommodationRequestDto;
import static booking.service.util.AccommodationUtil.createListOfAccommodations;
import static booking.service.util.AccommodationUtil.createUpdatedAccommodationRequestDto;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import booking.service.config.WithMockCustomUser;
import booking.service.dto.accommodation.AccommodationDto;
import booking.service.dto.accommodation.CreateAccommodationRequestDto;
import booking.service.model.AccommodationStatus;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
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
class AccommodationControllerTest {

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
                    new ClassPathResource("database/insert-accommodations.sql")
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
                    new ClassPathResource("database/delete-accommodations.sql")
            );
        }
    }

    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    @DisplayName("Get all accommodations with pagination and compare content")
    void findAll_WithValidPagination_ReturnsPagedAccommodations() throws Exception {
        List<?> accommodations = createListOfAccommodations();
        List<AccommodationDto> expected = accommodations.stream()
                .map(a -> createAccommodationDto(
                        ((booking.service.model.Accommodation) a).getId()))
                .toList();

        MvcResult result = mockMvc.perform(get("/accommodations")
                        .param("page", "0")
                        .param("accommodationSize", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsByteArray());
        JsonNode contentNode = root.get("content");
        List<AccommodationDto> actual = objectMapper.readValue(
                contentNode.toString(),
                new TypeReference<>() {}
        );
        assertEquals(expected.size(), actual.size());
    }

    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    @DisplayName("Get accommodation by id and validate content")
    void findById_WithValidId_ReturnsAccommodationDto() throws Exception {
        AccommodationDto expected = createAccommodationDto(1L);
        MvcResult result = mockMvc.perform(get("/accommodations/{id}", 1))
                .andExpect(status().isOk())
                .andReturn();

        AccommodationDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AccommodationDto.class);
        assertTrue(reflectionEquals(expected, actual, "dailyRate", "ownerEmail"));
    }

    @WithMockCustomUser(id = 1L, username = "test@example.com", roles = {"MANAGER"})
    @Test
    @DisplayName("Create a new accommodation as manager")
    void save_WithValidRequestDto_ReturnsCreatedAccommodation() throws Exception {
        CreateAccommodationRequestDto requestDto = createAccommodationRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/accommodations")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        AccommodationDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AccommodationDto.class);

        assertEquals(AccommodationStatus.PERMITTED, actual.getAccommodationStatus());
        assertEquals(requestDto.getName(), actual.getName());
        assertEquals(requestDto.getCity(), actual.getCity());
        assertEquals(requestDto.getType(), actual.getType());
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Update accommodation by id")
    void update_WithValidIdAndDto_ReturnsUpdatedAccommodation() throws Exception {
        CreateAccommodationRequestDto requestDto = createUpdatedAccommodationRequestDto();
        AccommodationDto expected = new AccommodationDto()
                .setId(1L)
                .setName(requestDto.getName())
                .setType(requestDto.getType())
                .setLocation(requestDto.getLocation())
                .setCity(requestDto.getCity())
                .setLatitude(requestDto.getLatitude())
                .setLongitude(requestDto.getLongitude())
                .setSize(requestDto.getSize())
                .setAmenities(requestDto.getAmenities())
                .setDailyRate(requestDto.getDailyRate())
                .setImage(requestDto.getImage())
                .setAccommodationStatus(AccommodationStatus.PERMITTED);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/accommodations/{id}", 1)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        AccommodationDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AccommodationDto.class);
        assertTrue(reflectionEquals(expected, actual, "ownerEmail"));
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Delete accommodation by id")
    void delete_WithValidId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/accommodations/{id}", 1))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    @DisplayName("Get accommodation by non-existing id")
    void findById_WithInvalidId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/accommodations/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Update accommodation by non-existing id")
    void update_WithInvalidId_ReturnsNotFound() throws Exception {
        CreateAccommodationRequestDto requestDto = createAccommodationRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        mockMvc.perform(put("/accommodations/{id}", 999)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Delete accommodation with invalid id")
    void delete_WithInvalidId_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/accommodations/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Create accommodation with invalid data")
    void save_WithInvalidRequestDto_ReturnsBadRequest() throws Exception {
        CreateAccommodationRequestDto invalidRequest = createAccommodationBadRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(invalidRequest);
        mockMvc.perform(post("/accommodations")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}


