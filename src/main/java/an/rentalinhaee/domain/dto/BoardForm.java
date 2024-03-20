package an.rentalinhaee.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BoardForm {

    private String stuId;
    private String name;

    private int likeNumber;

    private String title;
    private String content;

    private boolean notice;
}
