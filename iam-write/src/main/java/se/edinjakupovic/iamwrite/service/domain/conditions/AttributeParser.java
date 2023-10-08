package se.edinjakupovic.iamwrite.service.domain.conditions;

import se.edinjakupovic.iamwrite.service.domain.Attribute;

import java.util.ArrayList;
import java.util.List;

public final class AttributeParser {
    private AttributeParser() {
    }

    public static List<Attribute> parseAttributes(String attributeString) {
        String[] attributeStrings = attributeString.split(",");
        List<Attribute> attributes = new ArrayList<>(attributeStrings.length);
        for (String attribute : attributeStrings) {
            String[] parts = attribute.split(":");
            attributes.add(new Attribute(parts[0], parts[1]));
        }
        return attributes;
    }
}
