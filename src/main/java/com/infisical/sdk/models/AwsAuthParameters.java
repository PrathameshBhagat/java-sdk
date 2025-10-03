package com.infisical.sdk.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AwsAuthParameters {
  private final String iamHttpRequestMethod;
  private final String iamRequestHeaders;
  private final String iamRequestBody;

  public AwsAuthLoginInput toLoginInput(String identityId) {
    return AwsAuthLoginInput.builder()
        .identityId(identityId)
        .iamRequestHeaders(iamRequestHeaders)
        .iamHttpRequestMethod(iamHttpRequestMethod)
        .iamRequestBody(iamRequestBody)
        .build();
  }
}
