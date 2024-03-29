package an.rentalinhaee.service;

import an.rentalinhaee.domain.Item;
import an.rentalinhaee.repository.ItemRepository;
import an.rentalinhaee.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final RentalRepository rentalRepository;

    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    @Transactional
    public void updateItem(Long itemId, String name, int allStockQuantity, String category){
        Item item = itemRepository.findItemById(itemId);
        item.updateInfo(name, allStockQuantity, category);
    }


    // orphan removal 로 바꿀 순 없는가?
    @Transactional
    public void deleteItem(Long itemId){
        rentalRepository.deleteRentalByItem_Id(itemId);

        itemRepository.delete(itemRepository.findItemById(itemId));
    }

    public List<Item> findAllItems() {
        return itemRepository.findAll();
    }

    public Item findOneItem(Long itemId){
        return itemRepository.findItemById(itemId);
    }

    public Set<String> findCategories () {

        Set<String> categories = new HashSet<>();

        for (Item item : itemRepository.findAll()) {
            categories.add(item.getCategory());
        }

        return categories;
    }

    public List<Item> findItemsByCategory(String category) {
        return itemRepository.findItemByCategory(category);
    }

    public Page<Item> findAllItems(Pageable pageable) { return itemRepository.findAll(pageable); }

    public Page<Item> findItemsByCategoryAndName(String category, String name, Pageable pageable) { return itemRepository.findItemsByCategoryContainingAndNameContaining(category, name, pageable); }
}
