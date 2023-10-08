package se.edinjakupovic.iamwrite.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.edinjakupovic.iamwrite.persistence.SubjectRepository;
import se.edinjakupovic.iamwrite.service.domain.Subject;
import se.edinjakupovic.iamwrite.service.requests.CreateSubject;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class SubjectServiceTest {

    @Mock
    SubjectRepository repository;
    @InjectMocks
    SubjectService service;

    @Test
    void getAllSubjects() {
        var expectedSubjects = List.of(subject(), subject());
        when(repository.getAll()).thenReturn(expectedSubjects);
        assertThat(service.getAll()).isEqualTo(expectedSubjects);
    }


    @Test
    void getSubjectById() {
        var expectedId = "01BX5ZZKBKACTAV9WEVGEMMVRY";
        var expectedSubject = subject();
        when(repository.getById(expectedId)).thenReturn(expectedSubject);
        assertThat(service.getSubject(expectedId)).isEqualTo(expectedSubject);
    }

    @Test
    void createSubject() {
        CreateSubject createSubject = new CreateSubject(null, null, null, null);
        when(repository.create(createSubject)).thenReturn("id");
        assertThat(service.create(createSubject)).isEqualTo("id");
    }

    private static Subject subject() {
        return new Subject("01BX5ZZKBKACTAV9WEVGEMMVRY", "email", "firstName", "lastName", emptyList());
    }
}