# Lab08 — Mockito mock объектууд

**Оюутан:** Н.Мөнхбаяр
**Код:** B242270058

---

## 1. Орчны мэдээлэл

`java -version`:

debain13@debian:~/Lab08-Gobi-Bagsh$ java -version
openjdk version "21.0.13" 2026-10-20
OpenJDK Runtime Environment (build 21.0.13+9-1-deb13u2-Debian)
OpenJDK 64-Bit Server VM (build 21.0.13+9-1-deb13u2-Debian, mixed mode, sharing)


`mvn -version`:

debain13@debian:~/Lab08-Gobi-Bagsh$ mvn -version
Apache Maven 3.9.9
Maven home: /usr/share/maven
Java version: 17.0.21, vendor: Eclipse Adoptium, runtime: /usr/lib/jvm/temurin-17-jdk-amd64
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "6.12.117+deb13-amd64", arch: "amd64", family: "unix"



Дэлгэцийн зургууд: [java-version](Screenshot/java-version.png), [maven-version](Screenshot/maven-version.png).

---

## 2. Тестийн тоо

`results/mvn-test.txt` файлын сүүлийн `Tests run:` мөрийн дагуу **нийт 10 тест** амжилттай ажилласан (7 энгийн тест + A8-ийн 3 мөр).

```text
Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## 3. A8-ийн `lenient()` тайлбар

A8 параметржүүлсэн тестэд мөр бүр өөр stub ашигладаг тул тухайн мөр ажиллах үед бусад мөрийн stub-ууд ашиглагдахгүй үлддэг. Иймд Mockito-гийн strict stubs горим `UnnecessaryStubbingException` алдаа өгдөг тул эдгээр stub-ыг `lenient()`-ээр зарласан.

```java
lenient().when(repository.findStudent("SX")).thenReturn(null);
lenient().when(repository.courseExists("C1")).thenReturn(true);
lenient().when(repository.prerequisites("C2")).thenReturn(List.of("C1", "C0"));
```

---

## 4. Хэсэг B — Мутацийн хүснэгт

### 4.1 Мутант 1 — `!student.active()` шалгалтыг комментолсон

Эвдрэл: `CourseRegistrationService.java` дотор идэвхгүй оюутныг шалгах `if (!student.active()) ...` мөрийг комментолсон.

`results/mvn-test-mutant-1.txt`:

```text
Tests run: 10, Failures: 2, Errors: 0, Skipped: 0
BUILD FAILURE
```

| Унасан тест | Тайлбар |
| --- | --- |
| `гурванАлдааныАнги_параметржүүлсэн[2]` (A8) | `ERROR_INACTIVE_STUDENT` хүлээсэн ч `OK` гарсан |
| `идэвхгүйОюутанд_ERROR_INACTIVE_STUDENT_имэйлтэйХарьцахгүй` (A3) | `ERROR_INACTIVE_STUDENT` хүлээсэн ч `ERROR_NO_COURSE` гарсан |

Илрүүлсэн: 2 тест (A3, A8). Бусад 8 тест ногоон хэвээр.

### 4.2 Мутант 2 — `saveRegistration` дуудалтыг комментолсон

Эвдрэл: `register()` дотор `repository.saveRegistration(studentId, courseId)` дуудалтыг комментолсон.

`results/mvn-test-mutant-2.txt`:

```text
Tests run: 10, Failures: 2, Errors: 0, Skipped: 0
BUILD FAILURE
```

| Унасан тест | Тайлбар |
| --- | --- |
| `идэвхтэйОюутанБүртгүүлэхэд_OK_буцаж_хадгалж_имэйлИлгэнэ` (A1) | `Wanted but not invoked: repository.saveRegistration("S1", "C1")` |
| `имэйлИлгээгчШидсэнЧ_бүртгэл_OK_хадгалалтХийгдсэн` (A5) | `Wanted but not invoked: repository.saveRegistration("S4", "C1")` |

Илрүүлсэн: 2 тест (A1, A5).

**A7 яагаад унасангүй вэ?** A7 нь зөвхөн `emailSender.send(...)`-ийг `ArgumentCaptor`-оор шалгадаг, `saveRegistration()`-ийг verify хийдэггүй. Мутант 2 нь хадгалалтыг хассан ч и-мэйл хэвээр илгээгддэг тул A7 ногоон хэвээр үлдсэн. Энэ нь A7-ийн сул тал бөгөөд `verify(repository).saveRegistration("S5", "C9")` нэмбэл A7 мөн мутант 2-ыг барих байсан.

### 4.3 Мутант 3 — `|| courseId == null` хэсгийг устгасан

Эвдрэл: `register()`-ийн эхний `if`-ээс `|| courseId == null` хэсгийг устгаж, зөвхөн `studentId == null`-ийг шалгадаг болгосон.

`results/mvn-test-mutant-3.txt`:

```text
Tests run: 10, Failures: 1, Errors: 0, Skipped: 0
BUILD FAILURE
```

| Унасан тест | Тайлбар |
| --- | --- |
| `nullОролтод_IllegalArgumentException_хамаарлуудтайХарьцахгүй` (A6) | `IllegalArgumentException` хүлээсэн ч юу ч шидэгдээгүй |

Илрүүлсэн: 1 тест (A6). A6 нь `(null, "C1")`, `("S1", null)`, `(null, null)` гурвыг тус тусад нь шалгадаг тул мутант 3-ыг анхнаасаа барьсан, нэмж засвар хийх шаардлагагүй болсон.

### 4.4 Хураангуй хүснэгт

| Мутант | Эвдрэл | Унасан тестүүд | Тоо |
| --- | --- | --- | --- |
| 1 | `!student.active()` комментолсон | A8 `[2]`, A3 | 2 |
| 2 | `saveRegistration` комментолсон | A1, A5 | 2 |
| 3 | `\|\| courseId == null` устгасан | A6 | 1 |

Бүх 3 мутантыг дор хаяж нэг тест илрүүлсэн. Харин мутант 2-ыг A7 илрүүлээгүй нь A7-д `saveRegistration`-ийн verify дутуу байгааг харуулж байна.

---

## 5. Файлын бүтэц

```text
Lab08-Gobi-Bagsh/
|-- .gitignore              # target/, .idea/, .DS_Store
|-- pom.xml
|-- README.md
|-- Screenshot/             # дэлгэцийн зургууд (нотолгоонд тооцогдохгүй, зөвхөн дагалдах)
|-- results/
|   |-- mvn-test.txt
|   |-- mvn-test-mutant-1.txt
|   |-- mvn-test-mutant-2.txt
|   `-- mvn-test-mutant-3.txt
`-- src/
    |-- main/java/mn/edu/must/sqat/
    |   |-- CourseRegistrationService.java
    |   |-- EmailSender.java
    |   |-- Student.java
    |   `-- StudentRepository.java
    `-- test/java/mn/edu/must/sqat/
        `-- CourseRegistrationServiceTest.java
```

`target/` хавтас build-ийн гаралт тул `.gitignore`-д орсон бөгөөд репод байхгүй.

---

## 6. Дүгнэлт

Mock объект нь гадаад хамаарлыг (сан, SMTP) бодит хэрэгжүүлэлтгүйгээр тусгаарлаж, дуудалт болон аргументыг шалгах боломж олгодог. Гараар бичсэн fake нь интерфейсийн бүх методыг өөрөө хэрэгжүүлэх тул код илүү гарах боловч зан төлөв нь тодорхой, уншихад ойлгомжтой байдаг. Утга бүрдүүлэгч (value object), record (жишээ нь `Student`), энгийн utility класс зэргийг mock хийх ёсгүй — бодит объектыг шууд ашиглах нь хялбар бөгөөд найдвартай. `verify`-г хэтрүүлэн ашиглавал тест нь бизнес шаардлагаас илүү дотоод хэрэгжүүлэлтийн дэлгэрэнгүйд нягт холбогдож, refactoring хийхэд шаардлагагүй унадаг болно. Мутацийн шинжилгээ нь тестийн чанарыг шалгах сайн арга бөгөөд ямар ч тест унагаадаггүй эвдрэл нь тестийн цоорхойг илтгэнэ. Энэ лабораторид 3 мутантыг бүгдийг нь дор хаяж нэг тест барьж, логикийн гол салбарууд шалгагдаж байгааг баталсан. Гэсэн хэдий ч мутант 2-ыг A7 барьж чадаагүй нь тест бүр өөрийн гэсэн зорилготой байдаг ч нэг тест бүх зүйлийг шалгадаггүйг харуулсан.
