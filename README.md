# Lab08 — Mockito

**Оюутан:** Н.Мөнхбаяр 
**Код:** B242270058 
**Огноо:** 2026-11-07

---

## 1. Орчны мэдээлэл

| Хэрэгсэл | Хувилбар |
|---|---|
| Java | ![Java Version](Screenshot/java-version.png) |
| Maven | ![Maven Version](Screenshot/maven-version.png) |

---

## 2. Тестийн тоо

`results/mvn-test.txt` файлын сүүлийн `Tests run:` мөрийн дагуу **нийт 10 тест** амжилттай ажилласан.
Tests run: 10, Failures: 0, Errors: 0, Skipped: 0

---

## 3. A8-ийн `lenient()` тайлбар

A8 тестэд мөр бүр өөр stub ашигладаг. Тухайн мөр ажиллахад бусад мөрийн stub-ууд ашиглагдахгүй үлддэг тул Mockito-гийн **strict stubs** горим `UnnecessaryStubbingException` алдаа өгдөг. Иймд `lenient()`-оор тэдгээрийг зөвшөөрсөн — ингэснээр ашиглагдаагүй stub-ууд алдаа үүсгэхгүй.

```java
lenient().when(repo.findById(any())).thenReturn(Optional.of(student));

4. Хэсэг B — Мутацийн хүснэгт
4.1 Мутант 1 — !student.active() комментыг хассан
Тест	Үр дүн	Тайлбар
A3	❌ Fail	Идэвхгүй оюутныг шалгадаг
A8	❌ Fail	S2 мөрөнд идэвхгүй оюутныг шалгадаг
Бусад	✅ Pass	Энэ мутантад нөлөөлөхгүй
4.2 Мутант 2 — saveRegistration комментыг хассан
Тест	Үр дүн	Тайлбар
A1	❌ Fail	saveRegistration дуудагдахыг verify хийдэг
A5	❌ Fail	Давхар хэрэглэгчийг хадгалахыг шалгадаг
A7	✅ Pass	saveRegistration-ийг verify хийдэггүй
A7 яагаад унасангүй вэ? UserService.registerUser() дотор saveRegistration()-ийг дуудаж байгаа хэсгийг мутант 2 өөрчилсөн ч, A7 тест нь зөвхөн emailService.sendWelcome()-ийг verify хийдэг. saveRegistration()-ийг verify хийдэггүй тул мутант 2-ын өөрчлөлт A7-д нөлөөлөхгүй.

Тестийн сул тал: A7-д verify(userRepository).saveRegistration(user) нэмэх шаардлагатай — ингэснээр мутант 2-ыг илрүүлэх боломжтой болно.

4.3 Мутант 3 — courseId == null шалгалтыг устгасан
Тест	Үр дүн	Тайлбар
A6	❌ Fail	courseId null байх үеийн шалгалтыг хийдэг
Бусад	✅ Pass	Энэ мутантад нөлөөлөхгүй
4.4 Хураангуй хүснэгт
Мутант	Эвдрэл	Унасан тестүүд	Илрүүлсэн
1	!student.active() коммент	A3, A8	✅ 2 тест
2	saveRegistration коммент	A1, A5	✅ 2 тест
3	courseId == null устгасан	A6	✅ 1 тест
Нийт: Бүх 3 мутантыг дор хаяж нэг тест илрүүлсэн. Мутант 2-ыг A7 илрүүлээгүй — энэ нь тестийн сул тал бөгөөд A7-д saveRegistration-ийг verify хийх шаардлагатай.

5. Мутант 2-ын дэлгэрэнгүй тайлбар
5.1 Эх код
public void registerUser(User user) {
    userRepository.saveRegistration(user);
    emailService.sendWelcome(user);
}
5.2 Мутант 2
public void registerUser(User user) {
    // userRepository.saveRegistration(user);  ← хасагдсан
    emailService.sendWelcome(user);
}
5.3 Тестийн үр дүн
$ mvn test
...
[ERROR] Failures:
[ERROR]   UserServiceTest.registerUser_shouldSaveUser:45
[ERROR]   UserServiceTest.registerUser_shouldRejectDuplicate:67
[INFO] Tests run: 10, Failures: 2, Errors: 0, Skipped: 0
mvn-test-mutant-2.txt файлд зөвхөн A1, A5 гэсэн хоёр failure байна — A7 унасангүй.

6. Дүгнэлт
Mock объект нь гадаад хамаарлыг (сан, SMTP) бодит хэрэгжүүлэлтгүйгээр тусгаарлаж тестлэх боломж олгодог бол гараар бичсэн fake нь интерфейсийн бүх методыг хэрэгжүүлэх шаардлагатай бөгөөд илүү код бичих шаардлагатай. Гэхдээ fake нь илүү уншигдахуйц, зан төлөвийг тодорхой илэрхийлдэг давуу талтай. Юуг mock хийх ёсгүй вэ гэвэл — утга бүрдүүлэгч (value object), record, энгийн utility класс зэргийг mock хийх нь илүүдэл бөгөөд бодит объект ашиглах нь дээр. verify-г хэтрүүлэн ашиглавал тест нь хэрэгжүүлэлтийн дэлгэрэнгүй мэдээлэлд хэт холбогдож, refactoring хийхэд эмзэг болдог — өөрөөр хэлбэл "хамаг хэт холбогдол" үүсдэг. Мөн зарим verify нь бизнес шаардлага биш, харин дотоод дуудлагыг шалгаж байгаа тохиолдол гардаг. Мутацийн шинжилгээ нь тестийн чанарыг шалгах сайн арга бөгөөд аль ч тест унагаадаггүй эвдрэл нь тестийн цоорхойг илтгэнэ. Энэ лабораторид бичсэн тестүүд 3 мутантыг барьж чадсанаар логикийн гол салбаруудыг зөв шалгаж байгааг баталлаа — гэхдээ A7-ийн saveRegistration verify дутуу байгаа нь мутант 2-ын бүрэн илрүүлэлтийг хангаагүй.
