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
        return itemRequestRepository.findAllByOrderByIdDesc();
    }

    public ItemRequest findById(Long id) {
        return itemRequestRepository.findItemRequestById(id);
    }

    @Transactional
    public void check(Long id) {
        findById(id).check();
    }

    @Transactional
    public void delete(Long id) {
        itemRequestRepository.delete(findById(id));
    }
}
