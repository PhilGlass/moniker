package glass.phil.monzo.presentation.util;

import android.content.Context;

import org.threeten.bp.Clock;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import glass.phil.monzo.R;
import glass.phil.monzo.model.Clocks.Local;

@SuppressWarnings("SpellCheckingInspection")
public final class DateFormatter {
  private static final DateTimeFormatter TRUNCATED = DateTimeFormatter.ofPattern("EEEE d MMMM");
  private static final DateTimeFormatter FULL = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy");
  private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

  public static CharSequence formatDate(Context context, @Local Clock clock, Instant instant) {
    final LocalDate date = instant.atZone(clock.getZone()).toLocalDate();
    final LocalDate today = LocalDate.now(clock);
    if (today.equals(date)) {
      return context.getString(R.string.today);
    } else if (today.minusDays(1).equals(date)) {
      return context.getString(R.string.yesterday);
    } else if (today.minusYears(1).isBefore(date)) {
      return TRUNCATED.format(date);
    } else {
      return FULL.format(date);
    }
  }

  public static CharSequence formatDateTime(Context context, @Local Clock clock, Instant instant) {
    final LocalDateTime dateTime = instant.atZone(clock.getZone()).toLocalDateTime();
    return context.getString(R.string.date_time, formatDate(context, clock, instant), TIME.format(dateTime));
  }

  private DateFormatter() {
    throw new AssertionError("No instances");
  }
}
