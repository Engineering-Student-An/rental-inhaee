package an.rentalinhaee.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Getter @Setter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rental {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rental_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private LocalDate rentalDate;
    private LocalDate finishRentalDate;
    private Long rentalDateDiff;

    @Enumerated(EnumType.STRING)
    private RentalStatus status;

    public void setStudent(Student student) {
        this.student = student;
        student.getRentals().add(this);
    }

    public void setItem(Item item) {
        this.item = item;
        item.getRentals().add(this);
    }

    /**
     * 대여 시작 (주문서 개념)
     */
    public static Rental createRental(Student student, Item item) {
        Rental rental = new Rental();

        rental.setStudent(student);
        rental.setItem(item);
        rental.setStatus(RentalStatus.ING);
        rental.setRentalDate(LocalDate.now().minusDays(2));
        item.removeStock();

        return rental;
    }

    /**
     * 대여 완료
     */
    public void finish(){
        this.setFinishRentalDate(LocalDate.now());
        setRentalDateDiff();

        if(this.status == RentalStatus.OVERDUE) {
            this.setStatus(RentalStatus.FINISH_OVERDUE);
        } else {
            this.setStatus(RentalStatus.FINISH);
        }

        item.addStock();
    }

    /**
     * 반납 날짜 - 대여 시작 날짜
     */
    public void setRentalDateDiff(){
        this.rentalDateDiff = ChronoUnit.DAYS.between(rentalDate,finishRentalDate);
    }

    public void updateStatus(RentalStatus status){
         this.status = status;
    }
}
