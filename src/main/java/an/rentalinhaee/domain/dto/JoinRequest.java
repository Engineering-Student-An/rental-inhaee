package an.rentalinhaee.domain.dto;

import an.rentalinhaee.domain.Student;
import an.rentalinhaee.domain.StudentRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class JoinRequest {
    @NotBlank(message = "학번이 비어있습니다.")
    private String stuId;

    @NotBlank(message = "이름이 비어있습니다.")
    private String name;

    @NotBlank(message = "비밀번호가 비어있습니다.")
    private String password;
    private String passwordCheck;

    private StudentRole role = StudentRole.USER;
    // 비밀번호 암호화 X
    public Student toEntity(){
        return Student.builder()
                .stuId(this.stuId)
                .name(this.name)
                .password(this.password)
                .role(role)
                .build();
    }


//    // 비밀번호 암호화
//    public Student toEntity(String encodedPassword){
//        return Student.builder()
//                .stuId(this.stuId)
//                .name(this.name)
//                .password(encodedPassword)
//                .role(StudentRole.USER)
//                .build();
//    }
}
