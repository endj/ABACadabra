package se.edinjakupovic.iamwrite.service.domain;

import java.util.List;

import static java.util.Objects.requireNonNull;

public record Subject(
        String id,
        String subjectEmail,
        String firstName,
        String lastName,
        List<Attribute> attributes
) {

    public Subject {
        requireNonNull(id);
        requireNonNull(subjectEmail);
        requireNonNull(firstName);
        requireNonNull(lastName);
        requireNonNull(attributes);
    }
}
