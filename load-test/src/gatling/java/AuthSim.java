import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import util.Generators;
import util.SimulationState;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static util.Protocol.HTTP_PROTOCOL;

public class AuthSim extends Simulation {
    static {
        var resourceIds = SimulationState.RESOURCES_WITH_POLICIES;
        var subjectIds = SimulationState.SUBJECT_IDS;
        if (subjectIds.isEmpty() || resourceIds.isEmpty())
            throw new RuntimeException("Populate DB with CreateSubjectResourceSimulation, no Resources");
    }


    ScenarioBuilder applyPolicyScenario = scenario("Check user auth")
            .exec(http("Apply policy")
                    .post("/authorization")
                    .body(StringBody(Generators.AUTH_GENERATOR)));

    {
        setUp(
                applyPolicyScenario.injectOpen(
                        constantUsersPerSec(100).during(10)
                        // rampUsersPerSec(10).to(1000).during(300)
                        //rampUsersPerSec(40).to(5000).during(120)
                )
        ).protocols(HTTP_PROTOCOL);
    }
}
