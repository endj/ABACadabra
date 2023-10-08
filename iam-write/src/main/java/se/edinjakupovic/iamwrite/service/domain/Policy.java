package se.edinjakupovic.iamwrite.service.domain;

import static java.util.Objects.requireNonNull;

public record Policy(
        String id,
        String name,
        String policyRules
) {

    public Policy {
        requireNonNull(id);
        requireNonNull(name);
        requireNonNull(policyRules);
    }

}
