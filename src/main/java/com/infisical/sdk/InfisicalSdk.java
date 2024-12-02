package com.infisical.sdk;

import com.infisical.sdk.config.SdkConfig;
import com.infisical.sdk.resources.AuthClient;
import com.infisical.sdk.resources.SecretsClient;

public class InfisicalSdk {

    private AuthClient authClient;
    private SecretsClient secretsClient;

    private final ApiClient apiClient;

  public InfisicalSdk(SdkConfig config) {
    this.apiClient = new ApiClient();
    apiClient.setBasePath(config.getSiteUrl());

    this.authClient = new AuthClient(apiClient, this::onAuthenticate);
    this.secretsClient = new SecretsClient(apiClient);
  }

  private void onAuthenticate(String accessToken) {
      this.apiClient.addDefaultHeader("Authorization", "Bearer " + accessToken);
      this.authClient = new AuthClient(apiClient, this::onAuthenticate);
      this.secretsClient = new SecretsClient(apiClient);
  }

  public AuthClient Auth() {
      return this.authClient;
  }

  public SecretsClient Secrets() {
      return this.secretsClient;
  }
}