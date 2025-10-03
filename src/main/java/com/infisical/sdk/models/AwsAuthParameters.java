package com.infisical.sdk.models;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder(toBuilder = true)
public class AwsAuthParameters {
  @NonNull private final String iamHttpRequestMethod;
  @NonNull private final String iamRequestHeaders;
  @NonNull private final String iamRequestBody;

  public AwsAuthLoginInput toLoginInput(String identityId) {
    return AwsAuthLoginInput.builder()
        .identityId(identityId)
        .iamRequestHeaders(iamRequestHeaders)
        .iamHttpRequestMethod(iamHttpRequestMethod)
        .iamRequestBody(iamRequestBody)
        .build();
  }
}
