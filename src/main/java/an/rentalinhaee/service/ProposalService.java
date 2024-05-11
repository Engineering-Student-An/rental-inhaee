package an.rentalinhaee.service;

import an.rentalinhaee.domain.Proposal;
import an.rentalinhaee.repository.ProposalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProposalService {

    private final ProposalRepository proposalRepository;

    public Page<Proposal> findAll(Pageable pageable){
        return proposalRepository.findAll(pageable);
    }

    public void saveProposal(Proposal proposal) {
        proposalRepository.save(proposal);
    }

    public Proposal findOne(Long id) {
        return proposalRepository.findProposalById(id);
    }
}
