package in.shivam.rupeeroot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching; // Import this
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableCaching // <--- Add this annotation
@SpringBootApplication
public class MoneymanagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoneymanagerApplication.class, args);
    }

}