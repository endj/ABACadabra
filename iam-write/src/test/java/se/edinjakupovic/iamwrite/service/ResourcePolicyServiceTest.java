package se.edinjakupovic.iamwrite.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.edinjakupovic.iamwrite.persistence.ResourcePolicyRepository;
import se.edinjakupovic.iamwrite.service.domain.ResourcePolicy;
import se.edinjakupovic.iamwrite.service.requests.CreatePolicy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourcePolicyServiceTest {

    @Mock
    ResourcePolicyRepository repository;
    @InjectMocks
    ResourcePolicyService service;

    @Test
    void addPolicyToResource() {
        var resourceId = "01BX5ZZKBKACTAV9WEVGEMMVRY";
        var expectedPolicyId = "WEVGEMMVRY01BX5ZZKBKACTAV9";
        var policy = new CreatePolicy("name", "x == y");
        when(repository.create(resourceId, policy)).thenReturn(expectedPolicyId);
        assertThat(service.addPolicyToResource(resourceId, policy)).isEqualTo(expectedPolicyId);
    }

    @Test
    void getResourcePolicy() {
        var resourceId = "01BX5ZZKBKACTAV9WEVGEMMVRY";
        var expectedPolicyId = "WEVGEMMVRY01BX5ZZKBKACTAV9";
        var resourcePolicy = new ResourcePolicy(resourceId, expectedPolicyId, null);
        when(repository.resourcePolicy(resourceId)).thenReturn(resourcePolicy);
        assertThat(service.getResourcePolicy(resourceId)).isEqualTo(resourcePolicy);
    }
}