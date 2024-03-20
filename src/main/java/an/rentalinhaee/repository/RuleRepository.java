package an.rentalinhaee.repository;

import an.rentalinhaee.domain.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleRepository extends JpaRepository<Rule, Long> {
    Rule findAnnouncementById(Long id);
}
