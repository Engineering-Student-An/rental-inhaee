package an.rentalinhaee.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = "비밀번호가 비어있습니다.")
    private String changePassword;
    private String changePasswordCheck;
}
