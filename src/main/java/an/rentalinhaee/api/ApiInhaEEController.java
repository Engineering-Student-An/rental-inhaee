package an.rentalinhaee.api;

import an.rentalinhaee.domain.dto.ParserDto;
import an.rentalinhaee.service.InhaEEService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiInhaEEController {

    private final InhaEEService inhaEEService;

    @GetMapping("/importantPosts")
    public List<ParserDto> fetchImportantPosts() {
        return inhaEEService.importantPostParser();
    }

    
}
