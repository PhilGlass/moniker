package glass.phil.monzo.core;

import android.support.annotation.Nullable;

public final class Enums {
  @Nullable public static <T extends Enum<T>> T enumConstant(Class<T> enumClass, String constantName) {
    for (T constant : enumClass.getEnumConstants()) {
      if (constant.name().equalsIgnoreCase(constantName)) {
        return constant;
      }
    }
    return null;
  }

  public static <T extends Enum<T>> T enumConstantOrThrow(Class<T> enumClass, String constantName) {
    final T constant = enumConstant(enumClass, constantName);
    if (constant == null) {
      throw new IllegalArgumentException(constantName + " is not a member of " + enumClass.getSimpleName());
    }
    return constant;
  }

  private Enums() {
    throw new AssertionError("No instances");
  }
}
