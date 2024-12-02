package com.infisical.sdk.resources;

import com.infisical.sdk.ApiException;
import com.infisical.sdk.models.UniversalauthLoginBody;
import com.infisical.sdk.util.InfisicalException;

import com.infisical.sdk.api.DefaultApi;

import com.infisical.sdk.ApiClient;
import java.util.function.Consumer;

public class AuthClient {
  private final DefaultApi request;
  private final Consumer<String> onAuthenticate;

  public AuthClient(ApiClient apiClient, Consumer<String> onAuthenticate) {
    this.request = new DefaultApi(apiClient);
    this.onAuthenticate = onAuthenticate;
  }

  public void UniversalAuthLogin(String clientId, String clientSecret) throws InfisicalException {
    try {
      var params = new UniversalauthLoginBody();


      params.setClientId(clientId);
      params.setClientSecret(clientSecret);

      this.request.apiV1AuthUniversalAuthLoginPost(params);

      var result = this.request.apiV1AuthUniversalAuthLoginPost(params);

      this.onAuthenticate.accept(result.getAccessToken());
    } catch (ApiException e) {
      throw new InfisicalException(e);
    }
  }
}