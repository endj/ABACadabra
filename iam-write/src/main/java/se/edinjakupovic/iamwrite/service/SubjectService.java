package se.edinjakupovic.iamwrite.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.edinjakupovic.iamwrite.persistence.SubjectRepository;
import se.edinjakupovic.iamwrite.service.domain.Subject;
import se.edinjakupovic.iamwrite.service.requests.CreateSubject;

import java.util.Collection;

@Component
public class SubjectService {
    private static final Logger log = LoggerFactory.getLogger(SubjectService.class);
    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public Collection<Subject> getAll() {
        return subjectRepository.getAll();
    }

    public Subject getSubject(String subjectId) {
        return subjectRepository.getById(subjectId);
    }

    public String create(CreateSubject request) {
        return subjectRepository.create(request);
    }
}
