package an.rentalinhaee.repository;

import an.rentalinhaee.domain.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, Long> {

    Proposal findProposalById(Long id);
}
