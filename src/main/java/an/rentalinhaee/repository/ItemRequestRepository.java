package an.rentalinhaee.repository;

import an.rentalinhaee.domain.ItemRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

}
