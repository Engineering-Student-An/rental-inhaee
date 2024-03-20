package an.rentalinhaee.repository;

import an.rentalinhaee.domain.RentalStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RentalSearch {

    private String stuId;
    private RentalStatus rentalStatus;  // ING, FINISH, OVERDUE
    private String itemName;
}
