package an.rentalinhaee.domain;

import an.rentalinhaee.service.ItemService;
import an.rentalinhaee.service.RentalService;
import an.rentalinhaee.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class InsertAdmin implements CommandLineRunner {

    private final StudentService studentService;
    private final ItemService itemService;
    private final RentalService rentalService;

    // 스프링 띄워지면서 자동으로 실행됨 => 일단은 관리자랑 몇개 데이터 추가
    @Override
    public void run(String... args) {
//        JoinRequest joinRequest = new JoinRequest();
//        joinRequest.setName("관리자");
//        joinRequest.setRole(StudentRole.ADMIN);
//        joinRequest.setStuId("ADMIN");
//        joinRequest.setPassword("ADMIN");
//        joinRequest.setPasswordCheck("ADMIN");
//        studentService.join(joinRequest, "chm20060@gmail.com");


//        Rule announcement = new Rule("필독!", "규칙1. ㅅㄱ");
//        ruleRepository.save(announcement);

    }
}
