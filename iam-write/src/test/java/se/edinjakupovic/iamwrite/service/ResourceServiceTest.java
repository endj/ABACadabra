package se.edinjakupovic.iamwrite.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.edinjakupovic.iamwrite.persistence.ResourceRepository;
import se.edinjakupovic.iamwrite.service.domain.Attribute;
import se.edinjakupovic.iamwrite.service.domain.Resource;
import se.edinjakupovic.iamwrite.service.requests.CreateResource;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    ResourceRepository repository;
    @InjectMocks
    ResourceService service;

    @Test
    void getAllResources() {
        var expectedResources = List.of(resource("id1"), resource("id2"));
        when(repository.getAll()).thenReturn(expectedResources);
        assertThat(service.getAllResource()).isEqualTo(expectedResources);
    }


    @Test
    void getResourceById() {
        var expectedId = "01BX5ZZKBKACTAV9WEVGEMMVRY";
        var expectedResource = resource(expectedId);
        when(repository.getById(expectedId)).thenReturn(expectedResource);
        assertThat(service.getResource(expectedId)).isEqualTo(expectedResource);
    }

    @Test
    void createResource() {
        var expectedId = "01BX5ZZKBKACTAV9WEVGEMMVRY";
        var createResource = new CreateResource("name", List.of(new Attribute("key", "val")));
        when(repository.create(createResource)).thenReturn(expectedId);
        assertThat(service.createResource(createResource)).isEqualTo(expectedId);
    }

    private static Resource resource(String id) {
        return new Resource(id, "name", emptyList());
    }

    private static Attribute attribute() {
        return new Attribute("key", "value");
    }

}