package an.rentalinhaee.service;

import an.rentalinhaee.domain.Rental;
import an.rentalinhaee.domain.RentalStatus;
import an.rentalinhaee.domain.Student;
import an.rentalinhaee.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AlarmService {

    private final RentalRepository rentalRepository;
    private final EmailService emailService;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")    // 매일 0시에 실행하게끔 스케쥴링
    public void alarmDueDate() {
        List<Rental> rentalsByStatus = rentalRepository.findRentalsByStatus(RentalStatus.ING);
        for (Rental rental : rentalsByStatus) {
            if(ChronoUnit.DAYS.between(rental.getRentalDate(), LocalDate.now()) == 1){
                Student student = rental.getStudent();
                String email = student.getEmail();
                String name = student.getName();
                String itemName = rental.getItem().getName();
                emailService.sendAlarm(email, name, itemName, "email/alarmD1");
            } else if(ChronoUnit.DAYS.between(rental.getRentalDate(), LocalDate.now()) == 0) {
                Student student = rental.getStudent();
                String email = student.getEmail();
                String name = student.getName();
                String itemName = rental.getItem().getName();
                emailService.sendAlarm(email, name, itemName, "email/alarmDDay");
            }
        }
    }
}
