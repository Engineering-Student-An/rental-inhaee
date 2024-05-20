package an.rentalinhaee.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ItemRequest {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_request_id")
    private Long id;

    private String stuId;
    private String name;

    private Long itemId;

    @Column(length = 5000)
    private String content;

}
