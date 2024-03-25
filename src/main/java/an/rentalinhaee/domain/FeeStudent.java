package an.rentalinhaee.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeStudent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stuId;
    private String name;

    public FeeStudent(String stuId, String name) {
        this.stuId = stuId;
        this.name = name;
    }
}
