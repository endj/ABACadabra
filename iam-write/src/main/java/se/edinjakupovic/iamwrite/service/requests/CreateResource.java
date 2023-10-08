package se.edinjakupovic.iamwrite.service.requests;

import se.edinjakupovic.iamwrite.service.domain.Attribute;

import java.util.List;

public record CreateResource(
        String name,
        List<Attribute> attributes
) {
}
