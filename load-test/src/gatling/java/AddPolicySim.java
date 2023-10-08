import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import util.Generators;
import util.SimulationState;

import java.util.List;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static util.Protocol.HTTP_PROTOCOL;

public class AddPolicySim extends Simulation {
    static {
        List<String> resourceIds = SimulationState.RESOURCE_IDS;
        if (resourceIds.isEmpty())
            throw new RuntimeException("Populate DB with CreateSubjectResourceSimulation, no Resources");
    }

    ScenarioBuilder applyPolicyScenario = scenario("Apply policy to resource")
            .exec(http("Apply policy")
                    .post(Generators.RESOURCE_WITHOUT_POLICY_URL)
                    .body(StringBody(Generators.POLICY_GENERATOR)));

    {
        setUp(
                applyPolicyScenario.injectOpen(
                        constantUsersPerSec(100).during(10)
                )
        ).protocols(HTTP_PROTOCOL);
    }
}
