package an.rentalinhaee.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "announcement_id")
    private Long id;

    private String title;
    private String content;

    public Rule(String title, String content) {
        this.title = title;
        this.content = content;
    }

    protected Rule() { }


}
