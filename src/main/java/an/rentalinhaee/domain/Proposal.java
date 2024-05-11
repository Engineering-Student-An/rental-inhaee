package an.rentalinhaee.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Proposal {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proposal_id")
    private Long id;

    private String stuId;
    private String name;

    private String title;

    @Column(length = 5000)
    private String content;

    private LocalDateTime writeTime;

    private boolean isSecret;

    protected Proposal() { }
    public Proposal(String stuId, String name, String title, String content, LocalDateTime writeTime, boolean isSecret) {
        this.stuId = stuId;
        this.name = name;
        this.title = title;
        this.content = content;
        this.writeTime = writeTime;
        this.isSecret = isSecret;
    }
}
