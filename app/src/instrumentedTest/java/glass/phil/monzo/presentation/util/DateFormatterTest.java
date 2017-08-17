package glass.phil.monzo.presentation.util;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.Clock;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.Month;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(AndroidJUnit4.class)
public final class DateFormatterTest {
  private static final LocalDate TODAY = LocalDate.of(2015, Month.JULY, 15);
  private static final Clock CLOCK = Clock.fixed(TODAY.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneId.of("UTC"));

  @Test public void formatDate_today() {
    assertThat(formatDate(TODAY.atStartOfDay())).isEqualTo("Today");
    assertThat(formatDate(TODAY.atTime(12, 0))).isEqualTo("Today");
    assertThat(formatDate(TODAY.atTime(23, 59))).isEqualTo("Today");
  }

  @Test public void formatDate_yesterday() {
    assertThat(formatDate(TODAY.minusDays(1).atStartOfDay())).isEqualTo("Yesterday");
    assertThat(formatDate(TODAY.minusDays(1).atTime(12, 0))).isEqualTo("Yesterday");
    assertThat(formatDate(TODAY.minusDays(1).atTime(23, 59))).isEqualTo("Yesterday");
  }

  @Test public void formatDate_lessThanOneYearAgo() {
    assertThat(formatDate(TODAY.minusDays(2).atStartOfDay())).isEqualTo("Monday 13 July");
    assertThat(formatDate(TODAY.minusDays(2).atTime(12, 0))).isEqualTo("Monday 13 July");
    assertThat(formatDate(TODAY.minusDays(2).atTime(23, 59))).isEqualTo("Monday 13 July");
    assertThat(formatDate(TODAY.minusYears(1).plusDays(1).atStartOfDay())).isEqualTo("Wednesday 16 July");
    assertThat(formatDate(TODAY.minusYears(1).plusDays(1).atTime(12, 0))).isEqualTo("Wednesday 16 July");
    assertThat(formatDate(TODAY.minusYears(1).plusDays(1).atTime(23, 59))).isEqualTo("Wednesday 16 July");
  }

  @Test public void formatDate_atLeastOneYearAgo() {
    assertThat(formatDate(TODAY.minusYears(1).atStartOfDay())).isEqualTo("Tuesday 15 July 2014");
    assertThat(formatDate(TODAY.minusYears(1).atTime(12, 0))).isEqualTo("Tuesday 15 July 2014");
    assertThat(formatDate(TODAY.minusYears(1).atTime(23, 59))).isEqualTo("Tuesday 15 July 2014");
    assertThat(formatDate(TODAY.minusYears(2).atStartOfDay())).isEqualTo("Monday 15 July 2013");
    assertThat(formatDate(TODAY.minusYears(2).atTime(12, 0))).isEqualTo("Monday 15 July 2013");
    assertThat(formatDate(TODAY.minusYears(2).atTime(23, 59))).isEqualTo("Monday 15 July 2013");
  }

  @Test public void formatDateTime_today() {
    assertThat(formatDateTime(TODAY.atStartOfDay())).isEqualTo("Today, 00:00");
    assertThat(formatDateTime(TODAY.atTime(12, 0))).isEqualTo("Today, 12:00");
    assertThat(formatDateTime(TODAY.atTime(23, 59))).isEqualTo("Today, 23:59");
  }

  @Test public void formatDateTime_yesterday() {
    assertThat(formatDateTime(TODAY.minusDays(1).atStartOfDay())).isEqualTo("Yesterday, 00:00");
    assertThat(formatDateTime(TODAY.minusDays(1).atTime(12, 0))).isEqualTo("Yesterday, 12:00");
    assertThat(formatDateTime(TODAY.minusDays(1).atTime(23, 59))).isEqualTo("Yesterday, 23:59");
  }

  @Test public void formatDateTime_lessThanOneYearAgo() {
    assertThat(formatDateTime(TODAY.minusDays(2).atStartOfDay())).isEqualTo("Monday 13 July, 00:00");
    assertThat(formatDateTime(TODAY.minusDays(2).atTime(12, 0))).isEqualTo("Monday 13 July, 12:00");
    assertThat(formatDateTime(TODAY.minusDays(2).atTime(23, 59))).isEqualTo("Monday 13 July, 23:59");
    assertThat(formatDateTime(TODAY.minusYears(1).plusDays(1).atStartOfDay())).isEqualTo("Wednesday 16 July, 00:00");
    assertThat(formatDateTime(TODAY.minusYears(1).plusDays(1).atTime(12, 0))).isEqualTo("Wednesday 16 July, 12:00");
    assertThat(formatDateTime(TODAY.minusYears(1).plusDays(1).atTime(23, 59))).isEqualTo("Wednesday 16 July, 23:59");
  }

  @Test public void formatDateTime_atLeastOneYearAgo() {
    assertThat(formatDateTime(TODAY.minusYears(1).atStartOfDay())).isEqualTo("Tuesday 15 July 2014, 00:00");
    assertThat(formatDateTime(TODAY.minusYears(1).atTime(12, 0))).isEqualTo("Tuesday 15 July 2014, 12:00");
    assertThat(formatDateTime(TODAY.minusYears(1).atTime(23, 59))).isEqualTo("Tuesday 15 July 2014, 23:59");
    assertThat(formatDateTime(TODAY.minusYears(2).atStartOfDay())).isEqualTo("Monday 15 July 2013, 00:00");
    assertThat(formatDateTime(TODAY.minusYears(2).atTime(12, 0))).isEqualTo("Monday 15 July 2013, 12:00");
    assertThat(formatDateTime(TODAY.minusYears(2).atTime(23, 59))).isEqualTo("Monday 15 July 2013, 23:59");
  }

  private static CharSequence formatDate(LocalDateTime dateTime) {
    return DateFormatter.formatDate(ConfigurationContexts.uk(), CLOCK, dateTime.toInstant(ZoneOffset.UTC));
  }

  private static CharSequence formatDateTime(LocalDateTime dateTime) {
    return DateFormatter.formatDateTime(ConfigurationContexts.uk(), CLOCK, dateTime.toInstant(ZoneOffset.UTC));
  }
}
