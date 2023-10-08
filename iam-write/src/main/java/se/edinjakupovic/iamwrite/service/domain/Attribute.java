package se.edinjakupovic.iamwrite.service.domain;

import static java.util.Objects.requireNonNull;

public record Attribute(String key, String value) {

    // TODO: 2023-10-04 Check known keys
    public Attribute {
        requireNonNull(key);
        requireNonNull(value);
        if (key.contains(",")
            || key.contains(":")
            || value.contains(",")
            || value.contains(":")) {
            throw new RuntimeException("Illegal char");
        }
    }
}
