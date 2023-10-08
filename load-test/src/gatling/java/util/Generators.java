package util;

import io.gatling.javaapi.core.Session;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.UUID.randomUUID;

public class Generators {

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public static final Function<Session, String> SUBJECT_GENERATOR = s -> """
            {"email": "%s","firstName": "John","lastName": "Doe","attributes": [%s]}
            """.formatted(randomUUID(), randomAttributes());

    public static final Function<Session, String> RESOURCE_GENERATOR = s -> """
            {"name":"%s","attributes": [%s]}
            """.formatted(randomUUID(), randomAttributes());

    public static final Function<Session, String> POLICY_GENERATOR = s -> """
            {"name":"%s","policyRules":"%s"}
            """.formatted(randomUUID(), randomPolicy());

    public static final Function<Session, String> AUTH_GENERATOR = s -> """
            {"subjectId":"%s","resourceId":"%s"}
            """.formatted(
            SimulationState.randomSubject(),
            SimulationState.randomResourceWithPolicy()
    );

    public static final Function<Session, String> RESOURCE_WITHOUT_POLICY_URL = s -> "/resources/" + SimulationState.resourceWithoutPolicy() + "/policy";

    public static final Function<Session, String> RANDOM_RESOURCE_POLICY_URL = s -> "/resources/" + SimulationState.randomResource() + "/policy";
    public static final Function<Session, String> RANDOM_RESOURCE_URL = s -> "/resources/" + SimulationState.randomResource();
    public static final Function<Session, String> RANDOM_SUBJECT_URL = s -> "/subjects/" + SimulationState.randomSubject();


    private static final String[] OPERATORS = {"<", ">", "==", "in"};
    private static final String[] CONDITION = {"||", "&&"};

    public static String randomPolicy() {
        int parts = RANDOM.nextInt(1, 5);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts; i++) {

            String operation = OPERATORS[RANDOM.nextInt(OPERATORS.length)];
            var op = switch (operation) {
                case "<" -> "a < b";
                case ">" -> "a > b";
                case "==" -> "a == b";
                case "in" -> "a in [b]";
                default -> throw new IllegalStateException("Unexpected value: " + operation);
            };
            sb.append(op);
            var lastOperation = i == parts - 1;
            if (!lastOperation) {
                String condition = CONDITION[RANDOM.nextInt(CONDITION.length)];
                sb.append(" ").append(condition).append(" ");
            }
        }
        return sb.toString();
    }


    private static String randomAttributes() {
        return attributes(RANDOM.nextInt(1, 10));
    }

    private static String attributes(int number) {
        List<String> list = IntStream.range(0, number)
                .mapToObj("""
                        {"key": "a%d", "value": "b"},
                        """::formatted).collect(Collectors.toList());
        list.add("""
                {"key": "a%d", "value": "b"}
                """.formatted(number));
        return String.join("", list);
    }
}
