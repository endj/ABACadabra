package se.edinjakupovic.iamwrite.service.requests;

import se.edinjakupovic.iamwrite.service.domain.Attribute;

import java.util.List;

public record CreateSubject(
        String email,
        String firstName,
        String lastName,
        List<Attribute> attributes
) {
}
