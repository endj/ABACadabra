package se.edinjakupovic.iamwrite.service.domain;


import java.util.List;

import static java.util.Objects.requireNonNull;

public record Resource(
        String id,
        String name,
        List<Attribute> attributes
) {

    public Resource {
        requireNonNull(id);
        requireNonNull(name);
    }
}
