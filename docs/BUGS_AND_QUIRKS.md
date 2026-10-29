# Bugs and Quirks: Android client (v1.0.3)

This is a review of the app's source code, as of branch `1.0.3`, after the API v1.0.3 and language-selection changes. Each item gives where it is, what goes wrong, and the fix.

All paths are relative to `src/main/java/com/example/spanishflashcards/`.

**Severity**
- **High**: users will hit it in normal use, or it can crash the app or lose their work.
- **Medium**: wrong or confusing behaviour in some situations.
- **Low**: polish, or code tidiness with no visible effect.

---

## Summary

| # | Severity | Area | Issue |
|---|---|---|---|
| 1 | High | Networking | ✅ **Fixed.** Requests time out while the Render server wakes up |
| 2 | High | Edit/Delete screens | ✅ **Fixed.** Error messages are set but never shown |
| 3 | High | Practice / Revise | ✅ **Fixed.** Rotating the screen resets progress (and form input everywhere) |
| 4 | High | Several screens | ✅ **Fixed.** A failed load shows "Loading..." forever, or a blank form |
| 5 | Medium | Revise | Tapping one difficult word flips all of them |
| 6 | Medium | Practice / Revise | Finished screens don't scroll, so long lists get cut off |
| 7 | Medium | Navigation | Double-tapping a button opens the screen twice |
| 8 | Medium | Models | Kotlin types say "never null", but the API can send null |
| 9 | Medium | Edit Subject | An empty subject name can be saved |
| 10 | Medium | Practice | The answer check is too strict (accents, trailing spaces) |
| 11 | Medium | Practice / Revise | A subject with no vocabulary jumps straight to "completed" |
| 12 | Low | Forms | The keyboard "Next" key does nothing |
| 13 | Low | Edit screens | ✅ **Fixed** (with #4). Fields can be edited before the data has loaded, but typing is ignored |
| 14 | Low | Edit screens | "Edit" and "Delete" buttons look identical |
| 15 | Low | Config | `usesCleartextTraffic="true"` applies to release builds |
| 16 | Low | Config | `versionName` is still `1.0.2` |
| 17 | Low | Config | The server URL is hard-coded, and the local one is commented out |
| 18 | Low | Coroutines | `catch (e: Exception)` also catches coroutine cancellation |
| 19 | Low | Code quality | Dead code, debug prints, typos, misleading names |

---

## High

### 1. Requests time out while the Render server wakes up
**Where:** `data/remote/RetrofitInstance.kt`

Retrofit uses OkHttp's default timeouts of 10 seconds for connect, read and write. The backend is hosted on Render (`onrender.com`). If it's on Render's free tier, it goes to sleep when idle and takes about 30–60 seconds to wake up. So the first request after a quiet period (normally the Home screen's subject list) times out and shows "Unable to load subjects".

**Fix:** give Retrofit an `OkHttpClient` with longer timeouts. OkHttp is already on the classpath through Retrofit.
```kotlin
private val client = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .build()

Retrofit.Builder().baseUrl(BASE_URL).client(client)...
```
Adding a "Retry" button next to the Home screen error message would also help.

> ✅ **Fixed:** `RetrofitInstance` now uses an `OkHttpClient` with 60-second connect, read and write timeouts. The Home screen error now has a Retry button, using the shared `ui/utils/LoadError.kt`.

### 2. Error messages are set but never shown
**Where:**
- `ui/screens/vocab/ViewVocabEditDelete.kt`
- `ui/screens/vocab/ViewVocabDelete.kt`
- `ui/screens/lesson/EditLessonView.kt`
- `ui/screens/subject/ViewSubjectEditDelete.kt`

These screens set `errorMessage` (for example "Unable to edit vocab." or "Please complete both fields."), but there's no `Text(errorMessage)` anywhere in their layout. When a save or delete fails, the spinner stops and nothing else happens. Validation errors are invisible too, so pressing Edit with an empty field appears to do nothing.

**Fix:** copy the error block that `CreateVocabulary.kt` already uses into each of these screens, above the buttons:
```kotlin
if (errorMessage.isNotEmpty()) {
    Text(text = errorMessage, fontSize = 14.sp, color = CustomColors.errorColor)
    Spacer(modifier = Modifier.height(12.dp))
}
```

> ✅ **Fixed:** all four screens now show their error message above the buttons.

### 3. Rotating the screen resets progress and form input
**Where:** every screen. No file uses `rememberSaveable`, and `AndroidManifest.xml` doesn't handle configuration changes.

When the device rotates, or the theme or font size changes, Android recreates the activity and everything held in `remember { }` is lost:
- **Practice:** `indexNum`, `userAnswer` and `showNext` reset, so the user goes back to question 1.
- **Revise:** `indexNum` and `failedVocab` reset, losing the list of difficult words.
- **Create/edit forms:** text the user has typed is lost.

**Fix:** use `rememberSaveable` for user progress and typed input, for example:
```kotlin
var indexNum by rememberSaveable { mutableIntStateOf(0) }
var userAnswer by rememberSaveable { mutableStateOf("") }
```
- `failedVocab` is a `List<Vocab>`, which can't be saved directly. Save the failed vocab **ids** (`List<Long>`) instead.
- For the form data classes, either save each field separately, or make the classes `@Parcelize`.
- The longer-term fix is to move screen state into a `ViewModel` with `SavedStateHandle`.

> ✅ **Fixed** with `rememberSaveable`:
> - **Practice:** saves `indexNum`, `userAnswer`, `result` and `showNext`. `feedbackColor` is no longer stored; it's worked out from `result` and `showNext`, because a `Color` can't be saved.
> - **Revise:** saves `indexNum` and `answerVisible`. Difficult words are saved as `failedIds` (`List<Long>`), and the `failedVocab` list is rebuilt from the loaded vocab.
> - **Create screens:** each text field is saved on its own. The request object (`CreateSubject`, `CreateVocab`, `CreateLesson`) is built from the fields.
> - **Edit screens:** the data loaded from the API is kept separately from the user's edits, which are saveable and `null` until changed. After a rotation the data is reloaded, but the user's edits take priority.

### 4. A failed load shows "Loading..." forever, or a blank form
**Where:**
- `SubjectView.kt`, `Practice.kt`, `Revise.kt` show "Loading..." forever.
- `ViewVocabEditDelete.kt`, `EditLessonView.kt`, `ViewSubjectEditDelete.kt` show a blank form.

In each case the `LaunchedEffect` catches the exception and only prints the stack trace. The screen stays on "Loading..." (or "Loading..." as the subject title), or shows empty edit fields. If the user then presses Edit on a blank form, the screen sends a `null` body, which fails without any message (see #2).

**Fix:** add an `errorMessage` (or `loadFailed`) state that is set in the `catch` block. Show it in place of the content, with a Retry button that runs the load again. Home already does this, so it can be used as the pattern. On edit screens, also disable the Edit button until the data has loaded.

> ✅ **Fixed:** all six screens now set a `loadFailed` flag. They show `LoadError` with a Retry button that increments a `loadAttempt` counter, which reruns the `LaunchedEffect`. On the edit screens, fields and buttons stay disabled until the data has loaded, which also fixes **#13**. The leftover `println` calls in these screens have been removed.

---

## Medium

### 5. Tapping one difficult word flips all of them
**Where:** `ui/screens/revise/Revise.kt`, end-of-session list (around line 203)

Every card in the "Review your difficult words" list reads and toggles the same `answerVisible` flag. Tapping any one card flips every card between English and the target language.

**Fix:** track the flipped state for each card, for example a set of the flipped vocab ids:
```kotlin
var flipped by remember { mutableStateOf(setOf<Long>()) }
// onClick:
flipped = if (vocab.id in flipped) flipped - vocab.id else flipped + vocab.id
// text:
if (vocab.id in flipped) vocab.term else vocab.translation
```

### 6. Finished screens don't scroll, so long lists get cut off
**Where:** `Practice.kt` "Practice completed!" screen, and `Revise.kt` "Review your difficult words" screen

Both screens list every word inside a plain `Column` that is centred in the screen and doesn't scroll. With more than about 6–8 words, the list runs off the screen. The "Back to Subject" button underneath can't be reached, although the Back button in the top bar still works. The same applies to `CreateLessonView` and `EditLessonView` when the keyboard is open, because the 10-line body field pushes the button off screen.

**Fix:** add `.verticalScroll(rememberScrollState())` to the outer `Column` of these screens, or show the word list in a `LazyColumn` with `Modifier.weight(1f)`.

### 7. Double-tapping a button opens the screen twice
**Where:** `ui/navigation/AppNavHost.kt`

`goBack` and the language picker only act when the current screen is `RESUMED`, but the other `navController.navigate(...)` calls have no such check. A quick double-tap on a subject, or on Revise, Practice, Create, and so on, pushes the same screen twice. The user then has to press Back twice.

**Fix:** use the same check for every forward navigation. A small helper keeps it in one place:
```kotlin
val navigate: (Any) -> Unit = { route ->
    if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        navController.navigate(route)
    }
}
```
Alternatively, pass `launchSingleTop = true` on each `navigate`.

### 8. Kotlin types say "never null", but the API can send null
**Where:** every model in `data/model/`

Gson builds objects without calling Kotlin constructors, so it ignores both Kotlin's null checks and default values. For example, the default `lessonTitle = ""` in `EditLesson` is never applied. The API does no validation either: `API_ENDPOINTS.md` notes that `{}` creates a subject with `subjectName: null`. So a `String` field the code treats as "never null" can actually be `null`, if the data was created outside this app (curl, another client, or a future change).

The results vary:
- `subjectLesson.body.toString()` in `SubjectView.kt` shows the word **"null"**.
- Code like `vocab.term.isBlank()`, or passing a null value to `Text()`, can crash with a `NullPointerException`.
- Elsewhere the code already guards against this (`sub.subjectName ?: "Unnamed subject"`), even though the type says the guard isn't needed.

**Fix (pick one):**
- Make response fields nullable (`String?`) and handle `null` where they're displayed.
- Or switch the converter to **kotlinx.serialization**. The plugin is already applied in `build.gradle.kts`, and Retrofit has an official converter for it. It respects Kotlin null-safety and default values. With `coerceInputValues = true`, it uses a field's default value when the JSON contains `null`.

### 9. An empty subject name can be saved
**Where:** `ui/screens/subject/ViewSubjectEditDelete.kt`

Unlike the create screens, the Edit button doesn't check for a blank name, and the API doesn't validate either. A subject can be renamed to `""`, which then appears as an empty button on Home.

**Fix:** before launching the save, run the same check the create screen uses (shown via the fix from #2):
```kotlin
if (subject?.subjectName.isNullOrBlank()) {
    errorMessage = "Please enter a subject name."
} else { ... }
```

### 10. The answer check is too strict
**Where:** `ui/screens/practice/Practice.kt`, `checkAnswer` (line 90)

- Only the user's answer is trimmed. If the stored `term` has a stray leading or trailing space, no answer can ever match it.
- Accents must be exact. `cafe` is marked wrong for `café`, and the same goes for `nino`/`niño` and Catalan `ç`/`l·l`. Many phone keyboards make accents awkward to type.

**Fix:** trim and normalise both sides. If accent-insensitive matching is wanted, strip accents before comparing:
```kotlin
private fun normalise(s: String) =
    Normalizer.normalize(s.trim(), Normalizer.Form.NFD)
        .replace("\\p{Mn}+".toRegex(), "")
        .lowercase()

if (normalise(answer) == normalise(correctAnswer)) ...
```
Whether accents should count is a teaching decision. One option is to accept the answer but show "Correct, but note the accent: café".

### 11. A subject with no vocabulary jumps straight to "completed"
**Where:** `Practice.kt` and `Revise.kt`

With zero vocab, `indexNum < lenVocabs` is `0 < 0`, which is false, so the screens go straight to "Practice completed! You've answered all 0 questions." or "All finished!".

**Fix:** either:
- check for an empty list first and show "This subject has no vocabulary yet", or
- hide or disable the Revise and Practice buttons in `SubjectView.kt` when `vocabList.isNullOrEmpty()`.

---

## Low

### 12. The keyboard "Next" key does nothing
**Where:**
- `CreateVocabulary.kt`: the word field
- `CreateLessonView.kt` and `EditLessonView.kt`: the title field

These fields set `ImeAction.Next`, but `onNext` is an empty block with the comment "Move focus to the next field", so the key does nothing.

**Fix:**
```kotlin
val focusManager = LocalFocusManager.current
keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
```
The `onNext` attached to the lesson **body** field can be removed, because that field uses `ImeAction.Default`.

### 13. Fields can be edited before the data has loaded, but typing is ignored
**Where:** `ViewVocabEditDelete.kt`, `EditLessonView.kt`, `ViewSubjectEditDelete.kt`

The fields are enabled while the data is still `null`, and `vocab?.copy(...)` on `null` does nothing. Anything typed before the load finishes is silently lost.

**Fix:** use `enabled = !isLoading && vocab != null`, or set `isLoading = true` at the start of the load so the existing check covers it. Do the same for the lesson and subject screens.

### 14. "Edit" and "Delete" buttons look identical
**Where:** `ViewVocabEditDelete.kt`, `ViewSubjectEditDelete.kt`

Both buttons use `primaryButtonColors()`, so a destructive action looks the same as a save.

**Fix:** use `secondaryButtonColors()` for Delete. Alternatively, add a destructive button style to `ui/theme/Styles.kt` based on `CustomColors.errorColor`.

### 15. `usesCleartextTraffic="true"` applies to release builds
**Where:** `src/main/AndroidManifest.xml`

This was needed for the commented-out local `http://192.168.1.47:8080` server. But it applies to every build, so plain HTTP is allowed in production too, even though production uses HTTPS.

**Fix:** remove the attribute from the main manifest. Put it in a debug-only manifest (`src/debug/AndroidManifest.xml`), or use a `network_security_config.xml` that permits cleartext only to the local IP.

### 16. `versionName` is still `1.0.2`
**Where:** `build.gradle.kts`

The branch is `1.0.3` and the app now targets API v1.0.3, but the Home screen footer still shows 1.0.2.

**Fix:** set `versionName = "1.0.3"` and increase `versionCode`.

### 17. The server URL is hard-coded, and the local one is commented out
**Where:** `data/remote/RetrofitInstance.kt`

Switching between the local and production servers means editing code, which makes it easy to commit the wrong one.

**Fix:** define the URL per build type in `build.gradle.kts` and read `BuildConfig.BASE_URL` (`buildConfig = true` is already enabled):
```kotlin
buildTypes {
    debug   { buildConfigField("String", "BASE_URL", "\"http://192.168.1.47:8080/\"") }
    release { buildConfigField("String", "BASE_URL", "\"https://spring-boot-backend-srdb.onrender.com/\"") }
}
```

### 18. `catch (e: Exception)` also catches coroutine cancellation
**Where:** every `LaunchedEffect` and `handle*` function

When the user leaves a screen during a request, the coroutine is cancelled with a `CancellationException`. These blocks catch it like any other error: they print a misleading stack trace, and the coroutine keeps running briefly when it should stop.

**Fix:** rethrow cancellation before handling other errors:
```kotlin
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) { ... }
```
Alternatively, catch only `IOException` and `HttpException`.

### 19. Dead code, debug prints, typos, misleading names
These have no effect on users but make the code harder to follow.

- **Unused stub composables, each containing only a TODO.** Delete them:
  - `editSubject` and `deleteSubject` in `ui/screens/subject/CreateSubject.kt`
  - `editVocab` and `deleteVocab` in `ui/screens/vocab/CreateVocabulary.kt`

  The TODO comments in the vocab stubs also say "subject".
- **Typo:** `hendleSubjectEdit` should be `handleSubjectEdit` (`ViewSubjectEditDelete.kt`).
- **Debug output:** `println(...)` calls in `ViewSubjectEditDelete.kt:175`, `ViewVocabEditDelete.kt:60`, `EditLessonView.kt:61` and `EditLessonView.kt:179`. Remove them, or use `Log.d`.
- **Misleading name:** `ViewVocabDelete` (`ViewVocabDelete.kt`) also deletes subjects. Rename it to something like `ConfirmDeleteScreen` to match `ConfirmDeleteRoute`. `DeleteType` could move with it into a shared package.
- **Inconsistent screen names:** `CreateFunction`, `SubjectViewFunction` and `ViewVocabFunction` don't match the `...View` / `...Screen` names used elsewhere.
- **Unused parameter:** `subjectId` in `EditLessonView` is never used.
- **Repeated validation:** `handleSubjectSubmit`, `handleVocabSubmit` and `handleLessonSubmit` repeat the blank-field check that the button's `onClick` has already done.
- **Duplicated state:** in Practice and Revise, `vocabList` duplicates `subject.vocabs`, and the code uses both. Keep one.
- **Leftovers:**
  - a commented-out import in `ViewVocabEditDelete.kt:29`
  - commented-out `horizontalArrangement` lines
  - a stray `;` in `EditLessonView.kt:59`
  - `var editLessonRes: Lesson` declared on one line and assigned on the next
  - a blank first line in `Practice.kt` and `Revise.kt`
  - odd indentation in `EditLessonView.kt` and `CreateLessonView.kt`
- **Redundant call:** `subjectLesson.body.toString()` in `SubjectView.kt` calls `toString()` on a `String`. Remove it once #8 is fixed.
- **App name:** the theme and app are still called "SpanishFlashcards", and the launcher label most likely still says Spanish (`@string/app_name`). Consider a language-neutral name now that Catalan is supported.

---

## Suggested order of work
1. **#1 and #2:** small changes that make failures visible and stop the first-launch timeout.
2. **#4, #5, #6, #9:** visible bugs, each fixed within a single screen.
3. **#3:** rotation. Worth doing as part of a move to ViewModels.
4. **#8:** switch to kotlinx.serialization. Touches every model, so do it on its own branch.
5. **The rest**, as time allows.
