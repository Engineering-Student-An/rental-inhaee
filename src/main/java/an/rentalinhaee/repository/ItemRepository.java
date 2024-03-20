package an.rentalinhaee.repository;

import an.rentalinhaee.domain.Item;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item){
        if (item.getId() == null) {
            em.persist(item);
        }
//        else{
//            em.merge(item);
//        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll(){
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }

    public List<Item> findListByCategory(String selectedCategory) {
        return em.createQuery("select i from Item i " +
                        "where i.category = :selectedCategory", Item.class)
                .setParameter("selectedCategory", selectedCategory)
                .getResultList();
    }

    // 아이템 삭제
    public void deleteItem(Long itemId){
        em.flush();
        em.clear();
        em.createQuery("delete from Item i where i.id = :itemId")
                .setParameter("itemId", itemId)
                .executeUpdate();
        em.clear();
    }

}
