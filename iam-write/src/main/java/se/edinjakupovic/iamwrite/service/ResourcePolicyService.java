package se.edinjakupovic.iamwrite.service;

import org.springframework.stereotype.Component;
import se.edinjakupovic.iamwrite.persistence.ResourcePolicyRepository;
import se.edinjakupovic.iamwrite.service.domain.ResourcePolicy;
import se.edinjakupovic.iamwrite.service.requests.CreatePolicy;

@Component
public class ResourcePolicyService {

    private final ResourcePolicyRepository repository;

    public ResourcePolicyService(ResourcePolicyRepository repository) {
        this.repository = repository;
    }

    public String addPolicyToResource(String resourceId, CreatePolicy policy) {
        return repository.create(resourceId, policy);
    }

    public ResourcePolicy getResourcePolicy(String resourceId) {
        return repository.resourcePolicy(resourceId);
    }
}
