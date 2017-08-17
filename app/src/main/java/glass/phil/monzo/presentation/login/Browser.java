package glass.phil.monzo.presentation.login;

import okhttp3.HttpUrl;

interface Browser {
  void connect();

  void loadPage(HttpUrl url);

  /** Show the page most recently loaded through {@link #loadPage(HttpUrl)}. */
  void showPage();

  void disconnect();
}
