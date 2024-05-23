package an.rentalinhaee.domain.dto;

import lombok.Data;

@Data
public class ItemRequestForm {

    private String stuId;
    private String name;

    private Long itemId;

    private String content;
}
