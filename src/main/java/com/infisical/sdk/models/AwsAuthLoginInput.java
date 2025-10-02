package com.infisical.sdk.models;

import com.infisical.sdk.util.Helper;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AwsAuthLoginInput {
  private final String identityId;
  private final String iamRequestBody;
  private final String iamHttpRequestMethod;
  private final String iamRequestHeaders;

  public String validate() {
    if (Helper.isNullOrEmpty(identityId)) {
      return "Identity ID is required";
    }

    if (Helper.isNullOrEmpty(iamRequestBody)) {
      return "IamRequestBody is required";
    }

    if (Helper.isNullOrEmpty(iamHttpRequestMethod)) {
      return "IamHttpRequestMethod is required";
    }

    if (Helper.isNullOrEmpty(iamRequestHeaders)) {
      return "IamRequestHeaders is required";
    }
    return null;
  }
}
