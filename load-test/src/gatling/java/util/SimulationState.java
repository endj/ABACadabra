package util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class SimulationState {
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public static final List<String> SUBJECT_IDS = getIds("http://localhost:8080/test/subjectIds");
    public static final List<String> RESOURCE_IDS = getIds("http://localhost:8080/test/resourceIds");
    public static final List<String> RESOURCES_WITH_POLICIES = getIds("http://localhost:8080/test/resourceWithPolicies");
    public static final Deque<String> RESOURCES_WITHOUT_POLICIES = getResourcesWithoutPolicies();

    private static Deque<String> getResourcesWithoutPolicies() {
        var withPolicies = new HashSet<>(RESOURCES_WITH_POLICIES);
        var queue = new ArrayDeque<String>();
        for (String resourceId : RESOURCE_IDS) {
            if (!withPolicies.contains(resourceId)) {
                queue.add(resourceId);
            }
        }
        return queue;
    }

    private static List<String> getIds(String uri) {
        try {
            HttpClient httpClient = HttpClient.newBuilder().build();
            HttpResponse<String> response = httpClient.send(HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(uri))
                    .build(), HttpResponse.BodyHandlers.ofString());
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.body(), new TypeReference<List<String>>() {
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }


    static {
        System.out.println("Loading ID's");
        System.out.printf("Loaded %d subjects%n", SUBJECT_IDS.size());
        System.out.printf("Loaded %d resources%n", RESOURCE_IDS.size());
        System.out.printf("Loaded %d resources with policies%n", RESOURCES_WITH_POLICIES.size());
        System.out.printf("Loaded %d resources without policies%n", RESOURCES_WITHOUT_POLICIES.size());
    }

    public static String randomResource() {
        return RESOURCE_IDS.get(RANDOM.nextInt(RESOURCE_IDS.size()));
    }

    public static String randomSubject() {
        return SUBJECT_IDS.get(RANDOM.nextInt(SUBJECT_IDS.size()));
    }

    public static String randomResourceWithPolicy() {
        if (RESOURCES_WITH_POLICIES.isEmpty())
            throw new RuntimeException("No resources with policies to fetch");
        return RESOURCES_WITH_POLICIES.get(RANDOM.nextInt(RESOURCES_WITH_POLICIES.size()));
    }

    public static String resourceWithoutPolicy() {
        return Objects.requireNonNull(RESOURCES_WITHOUT_POLICIES.poll(), "No more resources without policeis");
    }

    public static void main(String[] args) {
        new SimulationState();
    }
}
