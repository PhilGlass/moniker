package glass.phil.monzo.presentation.login;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsSession;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

import java.util.List;

import glass.phil.monzo.BuildConfig;
import okhttp3.HttpUrl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(
    application = Application.class,
    constants = BuildConfig.class,
    shadows = CustomTabsBrowserTest.CustomTabsClientShadow.class
)
public final class CustomTabsBrowserTest {
  @Rule public final MockitoRule mockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

  @Mock private Activity activity;
  @Mock private CustomTabsClient client;
  @Mock private CustomTabsSession session;

  private CustomTabsBrowser browser;

  @Before public void setUp() {
    browser = new CustomTabsBrowser(activity);
  }

  @SuppressWarnings("WrongConstant")
  @Test public void connectionLifecycle() {
    browser.connect();

    verify(activity).bindService(any(), eq(browser), anyInt());
    verify(activity, never()).unbindService(browser);

    browser.disconnect();

    verify(activity).unbindService(browser);
  }

  @Test public void load() {
    when(client.newSession(any())).thenReturn(session);

    browser.onCustomTabsServiceConnected(null, client);
    browser.loadPage(HttpUrl.parse("https://x.com/"));

    verify(client).warmup(anyLong());
    verify(session).mayLaunchUrl(eq(Uri.parse("https://x.com/")), any(), any());

    browser.loadPage(HttpUrl.parse("https://y.com/"));

    verify(session).mayLaunchUrl(eq(Uri.parse("https://y.com/")), any(), any());
  }

  @Test public void errorCreatingSession() {
    when(client.newSession(any())).thenReturn(null);

    browser.onCustomTabsServiceConnected(null, client);
    browser.loadPage(HttpUrl.parse("https://x.com/"));

    verify(client).warmup(anyLong());
  }

  @Test public void serviceConnected_noLoadedUrl() {
    when(client.newSession(any())).thenReturn(session);

    browser.onCustomTabsServiceConnected(null, client);

    verify(client).warmup(anyLong());
    verify(session, never()).mayLaunchUrl(any(), any(), any());
  }

  @Test public void serviceConnected_withLoadedUrl() {
    when(client.newSession(any())).thenReturn(session);

    browser.loadPage(HttpUrl.parse("https://x.com/"));

    verify(client, never()).warmup(anyLong());
    verify(session, never()).mayLaunchUrl(any(), any(), any());

    browser.onCustomTabsServiceConnected(null, client);

    verify(client).warmup(anyLong());
    verify(session).mayLaunchUrl(eq(Uri.parse("https://x.com/")), any(), any());
  }

  @Test public void serviceReconnected() {
    when(client.newSession(any())).thenReturn(session);

    browser.onCustomTabsServiceConnected(null, client);
    browser.loadPage(HttpUrl.parse("https://x.com/"));

    verify(client).warmup(anyLong());
    verify(session).mayLaunchUrl(eq(Uri.parse("https://x.com/")), any(), any());

    final CustomTabsClient nextClient = mock(CustomTabsClient.class);
    final CustomTabsSession nextSession = mock(CustomTabsSession.class);
    when(nextClient.newSession(any())).thenReturn(nextSession);

    browser.onServiceDisconnected(null);
    browser.onCustomTabsServiceConnected(null, nextClient);

    verifyNoMoreInteractions(client, session);
    verify(nextClient).warmup(anyLong());
    verify(nextSession).mayLaunchUrl(eq(Uri.parse("https://x.com/")), any(), any());
  }

  @Implements(CustomTabsClient.class)
  public static final class CustomTabsClientShadow {
    @SuppressWarnings("unused")
    @Implementation public static String getPackageName(Context context, @Nullable List<String> packages) {
      return "x.y.z";
    }
  }
}
