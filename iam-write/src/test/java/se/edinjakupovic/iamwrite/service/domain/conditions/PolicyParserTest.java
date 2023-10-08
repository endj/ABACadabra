package se.edinjakupovic.iamwrite.service.domain.conditions;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PolicyParserTest {
    private static final PolicyParser PARSER = new PolicyParser();


    @Test
    void shortCuts() {
        var test = "x == y || x == z && x in [y]";
        boolean passes = PARSER.passesConditions(
                test,
                Map.of("x", "123"),
                Map.of("y", "123",
                        "z", "234"
                )
        );
        assertThat(passes).isTrue();
    }

    @Test
    void orEvaluate() {
        var test = "x == z || x == y";
        boolean passes = PARSER.passesConditions(
                test,
                Map.of("x", "123"),
                Map.of("y", "123",
                        "z", "234"
                )
        );
        assertThat(passes).isTrue();
    }

    @Test
    void andEvaluateTrue() {
        var test = "x == z && x == y";
        boolean passes = PARSER.passesConditions(
                test,
                Map.of("x", "123"),
                Map.of("y", "123",
                        "z", "123"
                )
        );
        assertThat(passes).isTrue();
    }

    @Test
    void andEvaluateFalse() {
        var test = "x == z && x == y";
        boolean passes = PARSER.passesConditions(
                test,
                Map.of("x", "123"),
                Map.of("y", "123",
                        "z", "234"
                )
        );
        assertThat(passes).isFalse();
    }

    @Test
    void orFalseThenAndTrue() {
        var test = "x == z || x == y && x == x";
        boolean passes = PARSER.passesConditions(
                test,
                Map.of("x", "123"),
                Map.of("y", "123",
                        "z", "234",
                        "x", "123"
                )
        );
        assertThat(passes).isTrue();
    }

    @Test
    void throwOnInvalidTokenLength() {
        assertThatThrownBy(() -> PARSER.passesConditions(
                "abc", emptyMap(), emptyMap()
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("abc");
    }

    @Test
    void throwOnInvalidOperator() {
        assertThatThrownBy(() -> PARSER.passesConditions(
                "x != y", emptyMap(), emptyMap()
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("!=");
    }

    @Test
    void largerThan() {
        var test = "x > y";
        boolean passes = PARSER.passesConditions(
                test, Map.of("x", "5"), Map.of("y", "3")
        );
        assertThat(passes).isTrue();
    }

    @Test
    void notLargerThan() {
        var test = "x > y";
        boolean passes = PARSER.passesConditions(
                test, Map.of("x", "3"), Map.of("y", "5")
        );
        assertThat(passes).isFalse();
    }

    @Test
    void smallerThan() {
        var test = "x < y";
        boolean passes = PARSER.passesConditions(
                test, Map.of("x", "3"), Map.of("y", "5")
        );
        assertThat(passes).isTrue();
    }

    @Test
    void notSmallerThan() {
        var test = "x < y";
        boolean passes = PARSER.passesConditions(
                test, Map.of("x", "5"), Map.of("y", "3")
        );
        assertThat(passes).isFalse();
    }

    @Test
    void invalidNumberFormatGreaterThan() {
        var test = "x > y";
        Assertions.assertThatThrownBy(() -> PARSER.passesConditions(
                        test, Map.of("x", "abc"), Map.of("y", "3")
                )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Expected numbers, got abc and 3");
    }

    @Test
    void invalidNumberFormatSmallerThan() {
        var test = "x < y";
        Assertions.assertThatThrownBy(() -> PARSER.passesConditions(
                        test, Map.of("x", "abc"), Map.of("y", "3")
                )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Expected numbers, got abc and 3");
    }

    @Test
    void returnFalseIfMissingSubjectAttributeValue() {
        var test = "x == y";
        boolean passes = PARSER.passesConditions(test, emptyMap(), Map.of("y", "123"));
        assertThat(passes).isFalse();
    }

    @Test
    void returnFalseIfMissingResourceAttributeValue() {
        var test = "x == y";
        boolean passes = PARSER.passesConditions(test, Map.of("x", "123"), emptyMap());
        assertThat(passes).isFalse();
    }

    @Test
    void containedInTrue() {
        var test = "x in [y]";
        boolean passes = PARSER.passesConditions(test,
                Map.of("x", "123"),
                Map.of("y", "123"));
        assertThat(passes).isTrue();
    }

    @Test
    void containedInTrueMultiple() {
        var test = "x in [x,y,z]";
        boolean passes = PARSER.passesConditions(test,
                Map.of("x", "123"),
                Map.of("x", "345",
                        "y", "345",
                        "z", "123"
                ));
        assertThat(passes).isTrue();
    }

    @Test
    void notContainedInTrueMultiple() {
        var test = "x in [x,y,z]";
        boolean passes = PARSER.passesConditions(test,
                Map.of("x", "123"),
                Map.of("x", "345",
                        "y", "345",
                        "z", "345"
                ));
        assertThat(passes).isFalse();
    }

    @Test
    void allTestFalse() {
        var test = "x in [y] || x > z || x < z || x == y";
        boolean passes = PARSER.passesConditions(test,
                Map.of("x", "1"),
                Map.of("y", "2",
                        "z", "1"));
        assertThat(passes).isFalse();
    }
}