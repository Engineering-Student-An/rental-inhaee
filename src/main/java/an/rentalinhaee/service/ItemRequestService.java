package an.rentalinhaee.service;

import an.rentalinhaee.domain.ItemRequest;
import an.rentalinhaee.repository.ItemRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    public void save(ItemRequest itemRequest) {
        itemRequestRepository.save(itemRequest);
    }

    public List<ItemRequest> findAll() {
        return itemRequestRepository.findAll();
    }

    public ItemRequest findById(Long id) {
        return itemRequestRepository.findItemRequestById(id);
    }
}
