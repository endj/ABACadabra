package se.edinjakupovic.iamwrite.service;

import org.springframework.stereotype.Component;
import se.edinjakupovic.iamwrite.persistence.AuthorizationRepository;
import se.edinjakupovic.iamwrite.service.domain.Attribute;
import se.edinjakupovic.iamwrite.service.domain.conditions.PolicyParser;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Component
public class AuthorizationService {

    private final AuthorizationRepository repository;
    private final PolicyParser policyParser;

    public AuthorizationService(AuthorizationRepository repository,
                                PolicyParser policyParser) {
        this.repository = repository;
        this.policyParser = policyParser;
    }

    public boolean isAuthorized(String subjectId, String resourceId) {
        var ctx = repository.fetchAuthContext(subjectId, resourceId);
        var subjectAttributes = ctx.subjectAttributes();
        var resourceAttributes = ctx.resourceAttributes();
        var policy = ctx.policy();

        var subAttributeMap = subjectAttributes.stream().collect(Collectors.toMap(Attribute::key, Attribute::value));
        var resAttributeMap = resourceAttributes.stream().collect(Collectors.toMap(Attribute::key, Attribute::value));

        return hasRequiredAttributes(subAttributeMap, resAttributeMap) && matchesPolicy(subjectAttributes, resourceAttributes, policy);
    }

    private boolean matchesPolicy(Collection<Attribute> subjectAttributes,
                                  Collection<Attribute> resourceAttributes,
                                  String policy) {
        return policyParser.passesConditions(
                requireNonNull(policy),
                subjectAttributes.stream().collect(Collectors.toMap(Attribute::key, Attribute::value)),
                resourceAttributes.stream().collect(Collectors.toMap(Attribute::key, Attribute::value))
        );
    }

    private boolean hasRequiredAttributes(Map<String, String> subjectAttributes, Map<String, String> resourceAttributes) {
        return resourceAttributes.keySet().stream().allMatch(subjectAttributes::containsKey);
    }
}
