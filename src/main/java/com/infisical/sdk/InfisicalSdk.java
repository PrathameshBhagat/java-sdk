package com.infisical.sdk;

import com.infisical.sdk.api.ApiClient;
import com.infisical.sdk.config.SdkConfig;
import com.infisical.sdk.resources.AuthClient;
import com.infisical.sdk.resources.SecretsClient;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;


public class InfisicalSdk {
    private SecretsClient secretsClient;
    private AuthClient authClient;

    private ApiClient apiClient;

  public InfisicalSdk(SdkConfig config) {

      this.apiClient = new ApiClient(config.getSiteUrl(), null);

    this.authClient = new AuthClient(apiClient, this::onAuthenticate);
    this.secretsClient = new SecretsClient(apiClient);
  }

  private void onAuthenticate(String accessToken) {
      this.apiClient = new ApiClient(apiClient.GetBaseUrl(), accessToken);

      this.secretsClient = new SecretsClient(apiClient);
      this.authClient = new AuthClient(apiClient, this::onAuthenticate);
  }

  public AuthClient Auth() {
      return this.authClient;
  }

  public SecretsClient Secrets() {
      return this.secretsClient;
  }
}