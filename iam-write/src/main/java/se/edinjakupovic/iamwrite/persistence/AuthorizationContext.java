package se.edinjakupovic.iamwrite.persistence;

import se.edinjakupovic.iamwrite.service.domain.Attribute;

import java.util.Collection;

import static java.util.Objects.requireNonNull;

public record
AuthorizationContext(
        Collection<Attribute> subjectAttributes,
        Collection<Attribute> resourceAttributes,
        String policy
) {

    public AuthorizationContext {
        requireNonNull(subjectAttributes);
        requireNonNull(resourceAttributes);
        requireNonNull(policy);
    }
}
