package mn.edu.must.sqat;

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
	@Test
void байхгүйОюутанд_ERROR_NO_STUDENT_хадгалахгүй_имэйлЯвахгүй() {
    when(repository.findStudent("SX")).thenReturn(null);

    String result = service.register("SX", "C1");

    assertEquals("ERROR_NO_STUDENT", result);
    verify(repository, never()).saveRegistration(anyString(), anyString());
    verify(emailSender, never()).send(anyString(), anyString(), anyString());
}
	@Test
void идэвхгүйОюутанд_ERROR_INACTIVE_STUDENT_имэйлтэйХарьцахгүй() {
    Student inactive = new Student("S2", "s2@must.edu.mn", false, List.of());
    when(repository.findStudent("S2")).thenReturn(inactive);

    String result = service.register("S2", "C1");

    assertEquals("ERROR_INACTIVE_STUDENT", result);
    verifyNoInteractions(emailSender);
}
	@Test
void өмнөхХолбооДутууБол_ERROR_PREREQUISITES_хадгалахгүй() {
    Student student = new Student("S3", "s3@must.edu.mn", true, List.of());
    when(repository.findStudent("S3")).thenReturn(student);
    when(repository.courseExists("C2")).thenReturn(true);
    when(repository.prerequisites("C2")).thenReturn(List.of("C1", "C0"));

    String result = service.register("S3", "C2");

    assertEquals("ERROR_PREREQUISITES", result);
    verify(repository, never()).saveRegistration(anyString(), anyString());
}
	@Test
void имэйлИлгээгчШидсэнЧ_бүртгэл_OK_хадгалалтХийгдсэн() {
    Student student = new Student("S4", "s4@must.edu.mn", true, List.of("C0"));
    when(repository.findStudent("S4")).thenReturn(student);
    when(repository.courseExists("C1")).thenReturn(true);
    when(repository.prerequisites("C1")).thenReturn(List.of("C0"));
    doThrow(new RuntimeException("SMTP down"))
        .when(emailSender).send(anyString(), anyString(), anyString());

    String result = service.register("S4", "C1");

    assertEquals("OK", result);
    verify(repository).saveRegistration("S4", "C1");
}
	@Test
void nullОролтод_IllegalArgumentException_хамаарлуудтайХарьцахгүй() {
    assertThrows(IllegalArgumentException.class,
        () -> service.register(null, "C1"));
    assertThrows(IllegalArgumentException.class,
        () -> service.register("S1", null));
    assertThrows(IllegalArgumentException.class,
        () -> service.register(null, null));

    verifyNoInteractions(repository, emailSender);
}
	@Test
void илгээгдсэнИмэйлийнБиед_хичээлийнКодОрсон() {
    Student student = new Student("S5", "s5@must.edu.mn", true, List.of("C0"));
    when(repository.findStudent("S5")).thenReturn(student);
    when(repository.courseExists("C9")).thenReturn(true);
    when(repository.prerequisites("C9")).thenReturn(List.of("C0"));

    service.register("S5", "C9");

    ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
    verify(emailSender).send(eq("s5@must.edu.mn"), anyString(), bodyCaptor.capture());
    assertTrue(bodyCaptor.getValue().contains("C9"));
}
	@ParameterizedTest
@CsvSource({
    "SX, C1, ERROR_NO_STUDENT",
    "S2, C1, ERROR_INACTIVE_STUDENT",
    "S3, C2, ERROR_PREREQUISITES"
})
void гурванАлдааныАнги_параметржүүлсэн(String studentId, String courseId, String expected) {
    lenient().when(repository.findStudent("SX")).thenReturn(null);

    lenient().when(repository.findStudent("S2"))
        .thenReturn(new Student("S2", "s2@must.edu.mn", false, List.of()));

    lenient().when(repository.findStudent("S3"))
        .thenReturn(new Student("S3", "s3@must.edu.mn", true, List.of()));
    lenient().when(repository.courseExists("C1")).thenReturn(true);
    lenient().when(repository.courseExists("C2")).thenReturn(true);
    lenient().when(repository.prerequisites("C2")).thenReturn(List.of("C1", "C0"));

    String result = service.register(studentId, courseId);

    assertEquals(expected, result);
}
}

