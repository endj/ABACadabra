package se.edinjakupovic.iamwrite.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.edinjakupovic.iamwrite.persistence.ResourceRepository;
import se.edinjakupovic.iamwrite.persistence.SubjectRepository;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    private final SubjectRepository subjectRepository;
    private final ResourceRepository resourceRepository;

    public TestController(SubjectRepository repository,
                          ResourceRepository resourceRepository) {
        this.subjectRepository = repository;
        this.resourceRepository = resourceRepository;
    }

    @GetMapping("/subjectIds")
    List<String> subjectIds() {
        return subjectRepository.getAllSubjectIds();
    }

    @GetMapping("/resourceIds")
    List<String> resourceIds() {
        return resourceRepository.getAllResourceIds();
    }

    @GetMapping("/resourceWithPolicies")
    List<String> resourceWithPolicies() {
        return resourceRepository.resourcesWithPolicies();
    }
}
