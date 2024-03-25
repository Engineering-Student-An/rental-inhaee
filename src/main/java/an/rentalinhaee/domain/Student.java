package an.rentalinhaee.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long id;

    private String stuId;
    private String name;
    private String password;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private StudentRole role;   // [USER, ADMIN]

    @OneToMany(mappedBy = "student")
    private List<Rental> rentals = new ArrayList<>();

    public void editPassword(String password) {
        this.password = password;
    }

    public Student(String stuId, String name) {
        this.stuId = stuId;
        this.name = name;
    }
}
