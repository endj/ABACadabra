import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import util.Generators;
import util.SimulationState;

import java.util.List;

import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static util.Protocol.HTTP_PROTOCOL;

public class GetSubjectSim extends Simulation {
    ScenarioBuilder randomResourceScenario = scenario("Get subject by id")
            .exec(http("Get subjectById")
                    .get(Generators.RANDOM_SUBJECT_URL));

    {
        List<String> subjectIds = SimulationState.SUBJECT_IDS;
        if (subjectIds.isEmpty())
            throw new RuntimeException("Populate DB with CreateSubjectResourceSimulation, no Resources");
        setUp(
                randomResourceScenario.injectOpen(
                        rampUsers(1000).during(10),
                        constantUsersPerSec(100).during(10)
                )
        ).protocols(HTTP_PROTOCOL);
    }
}
