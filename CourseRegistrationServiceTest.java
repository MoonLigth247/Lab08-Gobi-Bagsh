
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseRegistrationServiceTest {

    @Mock StudentRepository repository;
    @Mock EmailSender emailSender;

    CourseRegistrationService service;

    @BeforeEach
    void setUp() {
        service = new CourseRegistrationService(repository, emailSender);
    }
  @Test
void идэвхтэйОюутанБүртгүүлэхэд_OK_буцаж_хадгалж_имэйлИлгэнэ() {
    Student student = new Student("S1", "s1@must.edu.mn", true, List.of("C0"));
    when(repository.findStudent("S1")).thenReturn(student);
    when(repository.courseExists("C1")).thenReturn(true);
    when(repository.prerequisites("C1")).thenReturn(List.of("C0"));

    String result = service.register("S1", "C1");

    assertEquals("OK", result);
    verify(repository).saveRegistration("S1", "C1");
    verify(emailSender).send(eq("s1@must.edu.mn"), anyString(), contains("C1"));
}
}
