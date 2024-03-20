package an.rentalinhaee.service;

import an.rentalinhaee.domain.Rule;
import an.rentalinhaee.repository.RuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RuleService {

    private final RuleRepository ruleRepository;

    @Transactional
    public void updateAnnouncement(String title, String content) {
        Rule ruleById = ruleRepository.findAnnouncementById(1L);
        ruleById.setTitle(title);
        ruleById.setContent(content);
    }
}
