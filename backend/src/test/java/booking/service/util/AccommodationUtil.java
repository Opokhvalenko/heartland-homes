package booking.service.util;

import booking.service.dto.accommodation.AccommodationDto;
import booking.service.dto.accommodation.CreateAccommodationRequestDto;
import booking.service.model.Accommodation;
import booking.service.model.AccommodationStatus;
import booking.service.model.AccommodationType;
import java.math.BigDecimal;
import java.util.List;

public class AccommodationUtil {
    public static AccommodationDto createAccommodationDto(Long id) {
        return new AccommodationDto()
                .setId(id)
                .setName("Затишний будинок у центрі Києва")
                .setType(AccommodationType.HOUSE)
                .setLocation("Київська область, м. Київ, вул. Хрещатик, 12")
                .setCity("Київ")
                .setLatitude("50.4501")
                .setLongitude("30.5234")
                .setSize("3 Спальні")
                .setAmenities(List.of("Wi-Fi", "Кондиціонер", "Пральна машина"))
                .setDailyRate(BigDecimal.valueOf(2500.00))
                .setImage("https://www.dropbox.com/scl/fi/rbh8v6jjkhd3txxyhrfhg/photo1.jpg?rlkey=a4lrfbg4pz7zef6fv8jhcq12e&st=w0pq32wx&dl=0")
                .setAccommodationStatus(AccommodationStatus.PERMITTED)
                .setOwnerEmail("test@example.com");
    }

    public static List<Accommodation> createListOfAccommodations() {
        Accommodation a1 = new Accommodation()
                .setId(1L)
                .setType(AccommodationType.HOUSE)
                .setLocation("Київська область, м. Київ, вул. Хрещатик, 12")
                .setCity("Київ")
                .setSize("3 Спальні")
                .setAmenities(List.of("Wi-Fi", "Кондиціонер", "Пральна машина"))
                .setDailyRate(BigDecimal.valueOf(2500.00))
                .setImage("https://www.dropbox.com/scl/fi/rbh8v6jjkhd3txxyhrfhg/photo1.jpg?rlkey=a4lrfbg4pz7zef6fv8jhcq12e&st=w0pq32wx&dl=0")
                .setDeleted(false);

        Accommodation a2 = new Accommodation()
                .setId(2L)
                .setType(AccommodationType.APARTMENT)
                .setLocation("Львівська область, м. Львів, просп. Свободи, 45, кв. 12, поверх 3")
                .setCity("Львів")
                .setSize("1 Спальня")
                .setAmenities(List.of("Wi-Fi", "Кухня", "Балкон"))
                .setDailyRate(BigDecimal.valueOf(1800.00))
                .setImage("https://www.dropbox.com/scl/fi/72shzge7jjptym0m4lqpb/photo2.jpg?rlkey=6fq88kcxwwysv0epfj25oxs92&st=3iloeccy&dl=0")
                .setDeleted(false);

        Accommodation a3 = new Accommodation()
                .setId(3L)
                .setType(AccommodationType.VACATION_HOME)
                .setLocation("Одеська область, м. Одеса, вул. Шевченка, 8")
                .setCity("Одеса")
                .setSize("4 Спальні")
                .setAmenities(List.of("Wi-Fi", "Кухня", "Парковка"))
                .setDailyRate(BigDecimal.valueOf(3200.00))
                .setImage("https://www.dropbox.com/scl/fi/kp6w4a02drpbembtsxd7l/photo3.jpg?rlkey=dsnglnv4gejqkza4hvu7zmrt7&st=irkh4lf4&dl=0")
                .setDeleted(false);

        Accommodation a4 = new Accommodation()
                .setId(4L)
                .setType(AccommodationType.HOTEL)
                .setLocation("Харківська область, м. Харків, вул. Сумська, 101, кв. 45, поверх 7")
                .setCity("Харків")
                .setSize("2 Спальні")
                .setAmenities(List.of("Wi-Fi", "Кондиціонер", "Пральна машина"))
                .setDailyRate(BigDecimal.valueOf(2100.00))
                .setImage("https://www.dropbox.com/scl/fi/nq2hgr5sn16je0yaepr7w/photo4.jpeg?rlkey=03ug8ay3t233tk3zmbd5nfm5q&st=3hjd1al5&dl=0")
                .setDeleted(false);

        return List.of(a1, a2, a3, a4);
    }

    public static CreateAccommodationRequestDto createAccommodationRequestDto() {
        return new CreateAccommodationRequestDto()
                .setName("Затишний будинок у центрі Києва")
                .setType(AccommodationType.HOUSE)
                .setLocation("Київська область, м. Київ, вул. Хрещатик, 12")
                .setCity("Київ")
                .setLatitude("50.4501")
                .setLongitude("30.5234")
                .setSize("3 Спальні")
                .setAmenities(List.of("Wi-Fi", "Кондиціонер", "Пральна машина"))
                .setDailyRate(BigDecimal.valueOf(2500.00))
                .setImage("https://www.dropbox.com/scl/fi/rbh8v6jjkhd3txxyhrfhg/photo1.jpg?rlkey=a4lrfbg4pz7zef6fv8jhcq12e&st=w0pq32wx&dl=0");
    }

    public static CreateAccommodationRequestDto createAccommodationBadRequestDto() {
        return new CreateAccommodationRequestDto()
                .setType(null)
                .setLocation("")
                .setCity("")
                .setSize("")
                .setAmenities(List.of(""))
                .setDailyRate(BigDecimal.valueOf(-1))
                .setImage("");
    }

    public static CreateAccommodationRequestDto createUpdatedAccommodationRequestDto() {
        return new CreateAccommodationRequestDto()
                .setName("Апартаменти біля Оперного театру")
                .setType(AccommodationType.APARTMENT)
                .setLocation("Львівська область, м. Львів, просп. Свободи, 45, кв. 12, поверх 3")
                .setCity("Львів")
                .setLatitude("49.8397")
                .setLongitude("24.0297")
                .setSize("1 Спальня")
                .setAmenities(List.of("Wi-Fi", "Кондиціонер", "Пральна машина"))
                .setDailyRate(BigDecimal.valueOf(1800.00))
                .setImage("https://www.dropbox.com/scl/fi/72shzge7jjptym0m4lqpb/photo2.jpg?rlkey=6fq88kcxwwysv0epfj25oxs92&st=3iloeccy&dl=0");
    }

    public static CreateAccommodationRequestDto createAccommodationRequestDto2() {
        return new CreateAccommodationRequestDto()
                .setType(AccommodationType.VACATION_HOME)
                .setLocation("Одеська область, м. Одеса, вул. Шевченка, 8")
                .setCity("Одеса")
                .setSize("4 Спальні")
                .setAmenities(List.of("Wi-Fi", "Кухня"))
                .setDailyRate(BigDecimal.valueOf(3000.00))
                .setImage("https://www.dropbox.com/scl/fi/kp6w4a02drpbembtsxd7l/photo3.jpg?rlkey=dsnglnv4gejqkza4hvu7zmrt7&st=irkh4lf4&dl=0");
    }

    public static Accommodation createAccommodation2(Long id) {
        return new Accommodation()
                .setId(id)
                .setType(AccommodationType.VACATION_HOME)
                .setLocation("Одеська область, м. Одеса, вул. Шевченка, 8")
                .setCity("Одеса")
                .setSize("4 Спальні")
                .setAmenities(List.of("Wi-Fi", "Кухня"))
                .setDailyRate(BigDecimal.valueOf(3000.00))
                .setImage("https://www.dropbox.com/scl/fi/kp6w4a02drpbembtsxd7l/photo3.jpg?rlkey=dsnglnv4gejqkza4hvu7zmrt7&st=irkh4lf4&dl=0")
                .setDeleted(false);
    }

    public static AccommodationDto createAccommodationDto2(Long id) {
        return new AccommodationDto()
                .setId(id)
                .setType(AccommodationType.VACATION_HOME)
                .setLocation("Одеська область, м. Одеса, вул. Шевченка, 8")
                .setCity("Одеса")
                .setSize("4 Спальні")
                .setAmenities(List.of("Wi-Fi", "Кухня"))
                .setDailyRate(BigDecimal.valueOf(3000.00))
                .setImage("https://www.dropbox.com/scl/fi/kp6w4a02drpbembtsxd7l/photo3.jpg?rlkey=dsnglnv4gejqkza4hvu7zmrt7&st=irkh4lf4&dl=0");
    }
}
