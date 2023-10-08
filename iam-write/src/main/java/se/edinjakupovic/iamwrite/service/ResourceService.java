package se.edinjakupovic.iamwrite.service;

import org.springframework.stereotype.Component;
import se.edinjakupovic.iamwrite.persistence.ResourceRepository;
import se.edinjakupovic.iamwrite.service.domain.Resource;
import se.edinjakupovic.iamwrite.service.requests.CreateResource;

import java.util.Collection;


@Component
public class ResourceService {

    private final ResourceRepository repository;

    public ResourceService(ResourceRepository repository) {
        this.repository = repository;
    }

    public Collection<Resource> getAllResource() {
        return repository.getAll();
    }

    public Resource getResource(String resourceId) {
        return repository.getById(resourceId);
    }

    public String createResource(CreateResource request) {
        return repository.create(request);
    }
}
