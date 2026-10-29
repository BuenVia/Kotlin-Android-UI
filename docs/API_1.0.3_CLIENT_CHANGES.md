# Client changes for API v1.0.3

These are the changes made to the Android app so it works with the v1.0.3 backend described in `API_ENDPOINTS.md`.

## Summary

| API change | What changed in the app |
|---|---|
| Every path now starts with `/{language}/` (`es` or `ca`) | Added a `Language` enum and a `CurrentLanguage` holder. Every API call now passes the language segment. |
| Vocab fields renamed: `esp` → `term`, `eng` → `translation` | Renamed the fields in all vocab models and everywhere the screens use them. |
| Full subject responses include `"language": "ES" \| "CA"` | Added a `language` field to `Subject`. |
| Subject update body should be `{ "subjectName": "..." }` only | Added an `EditSubject` model. The update now sends only the name. |
| Vocab create/update return `Vocab` (no `subjectId`) | Changed the return types of `createVocab` and `editVocab` from `CreateVocab` / `EditVocab` to `Vocab`. |

## New files

### `data/model/language/Language.kt`
- `enum class Language(pathSegment, displayName)` with two values:
  - `ES("es", "Spanish")`
  - `CA("ca", "Catalan")`
- The enum names match the uppercase values the API returns in subject JSON, so Gson maps `"language": "ES"` straight to `Language.ES`.
- `object CurrentLanguage` holds the language the app is working in. It defaults to `Language.ES`, and `CurrentLanguage.pathSegment` gives the URL segment. It is stored as Compose state so screens can react if a language switcher is added later.
- The languages are hard-coded here because the API has no endpoint that returns them.

### `data/model/subject/EditSubject.kt`
- `data class EditSubject(val subjectName: String)` is the request body for `PUT /{language}/subject/update/{id}`.

## Modified files

### `data/remote/FlashcardApi.kt`
- Every endpoint path is now prefixed with `/{language}`, and every function takes `@Path("language") language: String` as its first parameter.
- `updateSubject` now takes `EditSubject` instead of `Subject?`.
- `createVocab` now returns `Vocab` instead of `CreateVocab`.
- `editVocab` now returns `Vocab` instead of `EditVocab`.

### Models
- `data/model/vocab/Vocab.kt`, `CreateVocab.kt`, `EditVocab.kt`: `esp` → `term`, `eng` → `translation`.
- `data/model/subject/Subject.kt`: added `val language: Language?`.

### Screens (all API calls now pass `CurrentLanguage.pathSegment`)
- `ui/screens/home/HomeScreen.kt`: `getSubjectNames`.
- `ui/screens/subject/CreateSubject.kt`: `createSubject`.
- `ui/screens/subject/SubjectView.kt`: `getSubject`; vocab display uses `term`.
- `ui/screens/subject/ViewSubjectEditDelete.kt`: `getSubject`. `hendleSubjectEdit` now sends `EditSubject(subjectName)` instead of the whole `Subject`, and returns `false` if no subject is loaded.
- `ui/screens/vocab/CreateVocabulary.kt`: `createVocab`; form uses `term` / `translation`.
- `ui/screens/vocab/ViewVocabEditDelete.kt`: `getVocabById`, `editVocab`; form uses `term` / `translation`.
- `ui/screens/vocab/ViewVocabDelete.kt`: `deleteVocab`, `deleteSubject`.
- `ui/screens/lesson/CreateLessonView.kt`: `createLesson`.
- `ui/screens/lesson/EditLessonView.kt`: `getLesson`, `updateLesson`.
- `ui/screens/revise/Revise.kt`: `getSubject`; cards use `term` / `translation`.
- `ui/screens/practice/Practice.kt`: `getSubject`; questions and answers use `term` / `translation`.

## Already compatible (no change needed)
- **Subject delete cascade**: after a subject is deleted, the app goes back to Home, which reloads the subject list. The app keeps no other local cache, so the deleted vocab and lesson don't linger.
- **Subject create response** already used `subjectId` (`CreateSubjectResponse`).
- **Vocab/lesson create and update** already sent `subjectId` in the body.
- **Deletes** already use `Response<Unit>` and check `isSuccessful`, so the empty 204 body is handled.
- **`GET /lesson/getbysubject/{id}`** (which can return an empty body) isn't used by the app.

## Language selection

The user picks a language every time the app opens. The choice is not saved between launches.

### New: `ui/screens/language/LanguageSelectScreen.kt`
- Shows one button per `Language.entries` entry (Spanish, Catalan).
- Shows a Back button only when it was opened from Home. At startup there is nothing to go back to.

### `ui/navigation/Routes.kt`
- Added `LanguageSelectRoute`.

### `ui/navigation/AppNavHost.kt`
- `startDestination` is now `LanguageSelectRoute` instead of `HomeRoute`.
- When a language is picked, the app sets `CurrentLanguage.language`, then navigates to Home and clears the whole back stack. Home then loads fresh with that language's subjects, and pressing Back on Home exits the app rather than reopening the picker.
- Home's new `onChangeLanguage` callback opens the picker on top of Home. Pressing Back there returns to Home without changing anything.

### `MainActivity.kt`
- Saves `CurrentLanguage.language` in `onSaveInstanceState` and restores it in `onCreate` before any screen is drawn. Android can close a backgrounded app and later reopen it on the same screen, and in-memory state is lost when that happens. Without this step the language would quietly reset to Spanish while the user was looking at Catalan subjects.

### `ui/screens/home/HomeScreen.kt`
- New `onChangeLanguage` parameter and a "Change language" button under the subtitle.
- The heading is now "`<language>` Flashcards" instead of "Spanish Flashcards".
- The subject list's `LaunchedEffect` is keyed on the current language.

### Language-aware labels
- `ui/screens/vocab/CreateVocabulary.kt`: "Add a `<language>` word…" and the "`<language>` word" field label.
- `ui/screens/revise/Revise.kt`: "Tap a word to switch between English and `<language>`".

## Verification
- `./gradlew :app:compileDebugKotlin` builds with no errors.
- I haven't tested against the running backend.
