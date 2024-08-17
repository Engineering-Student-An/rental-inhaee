package an.rentalinhaee.api;

import an.rentalinhaee.service.ItemRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping("/item/request/{id}/check")
    public ResponseEntity<Void> checkRequest(@PathVariable("id") Long id) {

        itemRequestService.check(id);

        return ResponseEntity.ok().build();
    }
}
