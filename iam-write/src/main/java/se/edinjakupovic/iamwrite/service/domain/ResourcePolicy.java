package se.edinjakupovic.iamwrite.service.domain;

public record ResourcePolicy(
        String resourceId,
        String policyId,
        Policy policy
) {
}
