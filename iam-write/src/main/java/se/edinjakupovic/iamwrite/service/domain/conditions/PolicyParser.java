package se.edinjakupovic.iamwrite.service.domain.conditions;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Condition Rules
 * attributeX == attributeY
 * attributeX == attributeY || attributeX == attributeZ
 * attributeX == attributeY && attributeX == attributeZ
 * attributeX > attributeY
 * attributeX < attributeY
 * attributeX in [attributeY, attributeZ]
 * <p>
 * attributeX == attributeY || attributeX == attributeZ && attributeX in [attributeY]
 */
@Component
public class PolicyParser {

    private static final Set<String> OPERATIONS = new HashSet<>();

    static {
        OPERATIONS.add("==");
        OPERATIONS.add(">");
        OPERATIONS.add("<");
        OPERATIONS.add("in");
    }

    public boolean passesConditions(String policyRules,
                                    Map<String, String> subjectAttributes,
                                    Map<String, String> resourceAttributes) {
        String[] orConditions = policyRules.split("\\|\\|");
        for (String orCondition : orConditions) {
            if (evaluateCondition(orCondition, subjectAttributes, resourceAttributes))
                return true;
        }
        return false;
    }

    private boolean evaluateCondition(String conditions,
                                      Map<String, String> subjectAttributes,
                                      Map<String, String> resourceAttributes) {
        String[] andConditions = conditions.split("&&");

        for (String andCondition : andConditions) {
            String[] tokens = andCondition.trim().split("\\s+");
            if (tokens.length != 3)
                throw new IllegalArgumentException(andCondition);

            String subjectAttributeKey = tokens[0];
            String operator = tokens[1];
            String resourceAttributeKey = tokens[2];

            if (!OPERATIONS.contains(operator))
                throw new IllegalArgumentException("Unknown operator " + operator);

            String attributeValue = subjectAttributes.get(subjectAttributeKey);
            if (attributeValue == null)
                return false;

            String resourceValue = resourceAttributes.get(resourceAttributeKey);

            var passes = switch (operator) {
                case "==" -> attributeValue.equals(resourceValue);
                case ">" -> greaterThan(attributeValue, resourceValue);
                case "<" -> lessThan(attributeValue, resourceValue);
                case "in" -> contained(attributeValue, resourceAttributeKey, resourceAttributes);
                default -> throw new IllegalStateException("Unexpected value: " + operator);
            };
            if (!passes)
                return false;

        }
        return true;
    }

    private boolean contained(String attribute, String resourceKey, Map<String, String> resourceAttributes) {
        String substring = resourceKey.substring(1, resourceKey.length() - 1);
        for (String possible : substring.split(",")) {
            var resourceAttributeValue = resourceAttributes.get(possible);
            if (attribute.equals(resourceAttributeValue))
                return true;
        }
        return false;
    }

    private boolean lessThan(String attribute, String value) {
        try {
            long attributeValue = Long.parseLong(attribute);
            long comparedValue = Long.parseLong(value);
            return attributeValue < comparedValue;
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Expected numbers, got %s and %s ".formatted(attribute, value));
        }
    }

    private boolean greaterThan(String attribute, String value) {
        try {
            long attributeValue = Long.parseLong(attribute);
            long comparedValue = Long.parseLong(value);
            return attributeValue > comparedValue;
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Expected numbers, got %s and %s ".formatted(attribute, value));
        }
    }


}
