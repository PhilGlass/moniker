package glass.phil.monzo.test.util;

import android.Manifest;
import android.app.UiAutomation;
import android.os.Process;
import android.os.UserHandle;
import android.support.test.InstrumentationRegistry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

@SuppressWarnings("PrimitiveArrayArgumentToVarargsMethod")
public final class Animations {
  private static final float DISABLED_ANIMATION_SCALE = 0f;

  private static final Method GRANT_RUNTIME_PERMISSION;
  private static final Object WINDOW_MANAGER_PROXY;
  private static final Method GET_ANIMATION_SCALES;
  private static final Method SET_ANIMATION_SCALES;

  static {
    try {
      GRANT_RUNTIME_PERMISSION = UiAutomation.class.getDeclaredMethod("grantRuntimePermission", String.class,
          String.class, UserHandle.class);

      WINDOW_MANAGER_PROXY = Class.forName("android.view.WindowManagerGlobal")
          .getDeclaredMethod("getWindowManagerService")
          .invoke(null);

      final Class<?> windowManagerProxyClass = Class.forName("android.view.IWindowManager");
      GET_ANIMATION_SCALES = windowManagerProxyClass.getDeclaredMethod("getAnimationScales");
      SET_ANIMATION_SCALES = windowManagerProxyClass.getDeclaredMethod("setAnimationScales", float[].class);
    } catch (NoSuchMethodException | IllegalAccessException | ClassNotFoundException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  private static float[] originalScales;

  public static void disableAll() {
    try {
      GRANT_RUNTIME_PERMISSION.invoke(
          InstrumentationRegistry.getInstrumentation().getUiAutomation(),
          InstrumentationRegistry.getTargetContext().getPackageName(),
          Manifest.permission.SET_ANIMATION_SCALE,
          Process.myUserHandle()
      );

      originalScales = (float[]) GET_ANIMATION_SCALES.invoke(WINDOW_MANAGER_PROXY);
      final float[] disabledScales = new float[originalScales.length];
      Arrays.fill(disabledScales, DISABLED_ANIMATION_SCALE);
      SET_ANIMATION_SCALES.invoke(WINDOW_MANAGER_PROXY, disabledScales);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public static void enableAll() {
    try {
      SET_ANIMATION_SCALES.invoke(WINDOW_MANAGER_PROXY, originalScales);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  private Animations() {
    throw new AssertionError("No instances.");
  }
}
