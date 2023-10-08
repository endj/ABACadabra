package se.edinjakupovic.iamwrite.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.edinjakupovic.iamwrite.service.ResourcePolicyService;
import se.edinjakupovic.iamwrite.service.ResourceService;
import se.edinjakupovic.iamwrite.service.domain.Resource;
import se.edinjakupovic.iamwrite.service.domain.ResourcePolicy;
import se.edinjakupovic.iamwrite.service.requests.CreatePolicy;
import se.edinjakupovic.iamwrite.service.requests.CreateResource;

import java.util.Collection;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;
    private final ResourcePolicyService resourcePolicyService;

    public ResourceController(ResourceService resourceService,
                              ResourcePolicyService resourcePolicyService) {
        this.resourceService = resourceService;
        this.resourcePolicyService = resourcePolicyService;
    }

    @GetMapping
    Collection<Resource> getResource() {
        return resourceService.getAllResource();
    }

    @GetMapping("/{id}")
    Resource byId(@PathVariable String id) {
        return resourceService.getResource(id);
    }

    @GetMapping("/{id}/policy")
    ResourcePolicy policy(@PathVariable String id) {
        return resourcePolicyService.getResourcePolicy(id);
    }

    @PostMapping
    String create(@RequestBody CreateResource resource) {
        return resourceService.createResource(resource);
    }

    @PostMapping("/{id}/policy")
    String addPolicy(@PathVariable String id, @RequestBody CreatePolicy policy) {
        return resourcePolicyService.addPolicyToResource(id, policy);
    }


}
