package com.infisical.sdk.models;

import com.infisical.sdk.util.Helper;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class AwsAuthLoginInput {
  @NonNull private final String identityId;
  @NonNull private final String iamHttpRequestMethod;
  @NonNull private final String iamRequestHeaders;
  @NonNull private final String iamRequestBody;

  public String validate() {
    if (Helper.isNullOrEmpty(identityId)) {
      return "Identity ID is required";
    }

    if (Helper.isNullOrEmpty(iamHttpRequestMethod)) {
      return "IamHttpRequestMethod is required";
    }

    if (Helper.isNullOrEmpty(iamRequestHeaders)) {
      return "IamRequestHeaders is required";
    }

    if (Helper.isNullOrEmpty(iamRequestBody)) {
      return "IamRequestBody is required";
    }
    return null;
  }
}
