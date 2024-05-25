package an.rentalinhaee.service;

import an.rentalinhaee.domain.ItemRequest;
import an.rentalinhaee.repository.ItemRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    public List<ItemRequest> findAll() {
        return itemRequestRepository.findAll();
    }
}
