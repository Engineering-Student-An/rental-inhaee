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

    private String itemName;

    @Column(length = 5000)
    private String content;

    private boolean isChecked;

    protected ItemRequest() {}

    public ItemRequest(String stuId, String name, String itemName, String content, boolean isChecked) {
        this.stuId = stuId;
        this.name = name;
        this.itemName = itemName;
        this.content = content;
        this.isChecked = isChecked;
    }

    public void check() {
        this.isChecked = true;
    }
}
