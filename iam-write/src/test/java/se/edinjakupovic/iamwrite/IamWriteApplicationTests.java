package se.edinjakupovic.iamwrite;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import se.edinjakupovic.iamwrite.service.AuthorizationService;
import se.edinjakupovic.iamwrite.service.ResourcePolicyService;
import se.edinjakupovic.iamwrite.service.ResourceService;
import se.edinjakupovic.iamwrite.service.SubjectService;
import se.edinjakupovic.iamwrite.service.domain.Attribute;
import se.edinjakupovic.iamwrite.service.domain.Policy;
import se.edinjakupovic.iamwrite.service.domain.Resource;
import se.edinjakupovic.iamwrite.service.domain.ResourcePolicy;
import se.edinjakupovic.iamwrite.service.domain.Subject;
import se.edinjakupovic.iamwrite.service.requests.CreatePolicy;
import se.edinjakupovic.iamwrite.service.requests.CreateResource;
import se.edinjakupovic.iamwrite.service.requests.CreateSubject;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class IamWriteApplicationTests {

    @Container
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0-debian"));
    @Autowired
    SubjectService subjectService;
    @Autowired
    ResourceService resourceService;
    @Autowired
    ResourcePolicyService resourcePolicyService;
    @Autowired
    AuthorizationService authorizationService;

    @AfterEach
    void clearDatabase(@Autowired Flyway flyway) {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void canCreateAndQuerySubject() {
        subjectService.create(new CreateSubject("email", "firstName", "lastName",
                List.of(
                        new Attribute("a", "b"),
                        new Attribute("b", "b"),
                        new Attribute("c", "b")
                )
        ));
        Collection<Subject> all = subjectService.getAll();
        String id = all.iterator().next().id();
        Subject subject = subjectService.getSubject(id);
        assertThat(subject)
                .isEqualTo(new Subject(id, "email", "firstName", "lastName",
                        List.of(
                                new Attribute("a", "b"),
                                new Attribute("b", "b"),
                                new Attribute("c", "b")
                        )
                ));
    }

    @Test
    void canCreateAndQueryResources() {
        resourceService.createResource(new CreateResource("name",
                List.of(new Attribute("a", "b"))));
        Collection<Resource> allResource = resourceService.getAllResource();
        String id = allResource.iterator().next().id();
        Resource resource = resourceService.getResource(id);
        assertThat(resource).isEqualTo(new Resource(id, "name", List.of(new Attribute("a", "b"))));
    }


    @Test
    void canCreateResourcePolicy() {
        String resourceId = resourceService.createResource(new CreateResource("name", List.of(new Attribute("a", "b"))));
        String policyId = resourcePolicyService.addPolicyToResource(resourceId, new CreatePolicy(
                "policyName",
                "a == b"
        ));
        ResourcePolicy resourcePolicy = resourcePolicyService.getResourcePolicy(resourceId);
        assertThat(resourcePolicy).isEqualTo(new ResourcePolicy(
                resourceId,
                policyId,
                new Policy(policyId, "policyName", "a == b")
        ));
    }


    @Test
    void authenticateResource() {
        String subjectId = subjectService.create(new CreateSubject("email", "firstName", "lastName",
                List.of(
                        new Attribute("a", "b"),
                        new Attribute("b", "b"),
                        new Attribute("c", "b")
                )
        ));
        String resourceId = resourceService.createResource(new CreateResource("name", List.of(new Attribute("c", "b"))));
        String policyId = resourcePolicyService.addPolicyToResource(resourceId, new CreatePolicy(
                "policyName",
                "c == b"
        ));
        authorizationService.isAuthorized(subjectId, resourceId);
    }

    @SuppressWarnings("unused")
    @DynamicPropertySource
    static void dbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> mySQLContainer.getJdbcUrl());
        registry.add("spring.datasource.driverClassName", () -> mySQLContainer.getDriverClassName());
        registry.add("spring.datasource.username", () -> mySQLContainer.getUsername());
        registry.add("spring.datasource.password", () -> mySQLContainer.getPassword());
        registry.add("spring.flyway.clean-disabled", () -> "false");
    }

}
