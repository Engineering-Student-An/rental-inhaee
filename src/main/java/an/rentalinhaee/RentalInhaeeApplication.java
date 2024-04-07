package an.rentalinhaee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class RentalInhaeeApplication {

    public static void main(String[] args) {
        // 타임존 : Asia/Seoul 설정
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        SpringApplication.run(RentalInhaeeApplication.class, args);
    }

}
