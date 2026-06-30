# Issue #39 — Default-value time conversions

Issue: https://github.com/SpineEventEngine/time/issues/39

## Decision (confirmed by maintainer)

- Do **not** force consistency. Defaults that convert meaningfully stay as-is
  (`LocalTime`→midnight, `Duration`→zero, `Timestamp`→epoch, `ZoneOffset`→UTC,
  `OffsetTime`→00:00 UTC).
- Conversions that are **impossible** for a default (no meaningful zero month /
  zero day / missing date / empty zone id) must explicitly reject the default
  and throw a clear exception.
- Document the impossible-conversion cases (KDoc/Javadoc).

## Exception-type convention

- Static utility `Xs.toJavaTime(value)` validates an **argument** →
  `checkNotDefault` → `IllegalArgumentException`.
- Instance mixin `value.toJavaTime()` validates the **receiver state** →
  `checkNotDefaultState` → `IllegalStateException` (matches the pre-existing
  `LocalDateTemporal` / `LocalDateTest`).
- Kotlin extensions use `checkNotDefaultArg` → `IllegalArgumentException`
  (`Month` keeps its existing `error(...)` → `IllegalStateException`).

## Changes

- `DtPreconditions`: added `checkNotDefaultState(Message)` next to
  `checkNotDefault(Message)`.
- Static converters guarded with `checkNotDefault`: `LocalDates`, `YearMonths`,
  `OffsetDateTimes` (`ZonedDateTimes` already guarded). `Months`/`DaysOfWeek`
  (enums) keep `checkMonth`/`checkDay`; documented only.
- Instance mixins guarded with `checkNotDefaultState`: `LocalDateTemporal`,
  `LocalDateTimeTemporal`, `ZoneIdMixin`.
- Kotlin extensions guarded with `checkNotDefaultArg`: `toKotlinLocalDate`,
  `toKotlinYearMonth`, `toKotlinLocalDateTime`, `toKotlinTimeZone`
  (`toKotlinMonth` already rejects `MONTH_UNDEFINED`).
- New regression specs `DefaultValueConversionSpec` in `time` and `time-kotlin`;
  the `time` spec exercises the mixin instance methods where available.
- Incidental: fixed pre-existing broken Dokka link `Durations2.ZERO` →
  `Durations.ZERO` in `DurationExts.kt` (surfaced by the synced `failOnWarning`).

## Verification

`./gradlew :time:test :time-kotlin:test` — green (19 new tests pass).
`./gradlew :time:dokkaGenerate :time-kotlin:dokkaGenerate` — green (0 warnings).
Reviewed by `spine-code-review` (APPROVE) and `review-docs` (APPROVE w/ nits,
all addressed).

## Status: done — pending PR
