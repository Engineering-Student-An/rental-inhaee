package an.rentalinhaee.domain;

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
public class RentalStatusUpdater {

    private final RentalRepository rentalRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")    // 매일 0시에 실행하게끔 스케쥴링
    public void updateRentalStatus() {
        List<Rental> rentalsByStatus = rentalRepository.findRentalsByStatus(RentalStatus.ING);
        for (Rental rental : rentalsByStatus) {
            if(ChronoUnit.DAYS.between(rental.getRentalDate(), LocalDate.now()) > 3){
                rental.updateStatus(RentalStatus.OVERDUE);
            }
        }
    }

}
