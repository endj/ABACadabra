package se.edinjakupovic.iamwrite.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.edinjakupovic.iamwrite.service.SubjectService;
import se.edinjakupovic.iamwrite.service.domain.Subject;
import se.edinjakupovic.iamwrite.service.requests.CreateSubject;

import java.util.Collection;

@RestController
@RequestMapping("/subjects")
public class SubjectController {

    private final SubjectService service;

    public SubjectController(SubjectService service) {
        this.service = service;
    }

    @GetMapping
    Collection<Subject> getSubjects() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    Subject byId(@PathVariable String id) {
        return service.getSubject(id);
    }

    @PostMapping
    String create(@RequestBody CreateSubject subject) {
        return service.create(subject);
    }
}
