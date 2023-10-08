import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import util.Generators;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static util.Protocol.HTTP_PROTOCOL;

public class CreateSubResSim extends Simulation {

    ScenarioBuilder createSubjectScenario = scenario("create random subjects")
            .exec(http("create random subject")
                    .post("/subjects")
                    .body(StringBody(Generators.SUBJECT_GENERATOR)));

    ScenarioBuilder createResourceScenario = scenario("create random resource")
            .exec(http("create random resource")
                    .post("/resources")
                    .body(StringBody(Generators.RESOURCE_GENERATOR)));

    {
        setUp(
                createResourceScenario.injectOpen(
                        constantUsersPerSec(100)
                                .during(400)
                ),
                createSubjectScenario.injectOpen(
                        constantUsersPerSec(0)
                                .during(100)
                )
        ).protocols(HTTP_PROTOCOL);
    }
}
