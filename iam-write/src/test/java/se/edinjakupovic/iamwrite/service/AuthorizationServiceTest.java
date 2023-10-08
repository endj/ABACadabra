package se.edinjakupovic.iamwrite.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.edinjakupovic.iamwrite.persistence.AuthorizationContext;
import se.edinjakupovic.iamwrite.persistence.AuthorizationRepository;
import se.edinjakupovic.iamwrite.service.domain.Policy;
import se.edinjakupovic.iamwrite.service.domain.ResourcePolicy;
import se.edinjakupovic.iamwrite.service.domain.conditions.PolicyParser;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    AuthorizationRepository repository;
    @Mock
    PolicyParser policyParser;
    @InjectMocks
    AuthorizationService service;

    @Test
    void isAuthorized() {
        var subjectId = "1L";
        var resourceId = "2L";
        var resourcePolicy = new ResourcePolicy("id1", "id2", new Policy(
                "id2",
                "myPolicy",
                "x == y"
        ));
        when(policyParser.passesConditions(
                resourcePolicy.policy().policyRules(),
                emptyMap(),
                emptyMap())).thenReturn(false);
        when(repository.fetchAuthContext(subjectId, resourceId))
                .thenReturn(new AuthorizationContext(
                        emptyList(),
                        emptyList(),
                        "x == y"
                ));
        assertThat(service.isAuthorized(subjectId, resourceId)).isFalse();
    }
}