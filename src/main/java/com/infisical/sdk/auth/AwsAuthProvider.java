package com.infisical.sdk.auth;

import com.amazonaws.DefaultRequest;
import com.amazonaws.Request;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.util.SdkHttpUtils;
import com.infisical.sdk.models.AwsAuthLoginInput;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AwsAuthProvider {
  @Builder.Default private final String serviceName = "sts";
  @Builder.Default private final HttpMethodName methodName = HttpMethodName.POST;
  @Builder.Default private final String endpointTemplate = "https://sts.%s.amazonaws.com";

  @Builder.Default
  private final Map<String, List<String>> params =
      Map.of("Action", List.of("GetCallerIdentity"), "Version", List.of("2011-06-15"));

  public AwsAuthLoginInput fromCredentials(String region, AWSCredentials credentials)
      throws URISyntaxException {

    final Request<?> request = new DefaultRequest<>(serviceName);
    final String iamRequestURL = endpointTemplate.formatted(region);
    request.setHttpMethod(methodName);
    request.setEndpoint(new URI(iamRequestURL));
    request.setParameters(params);
    final String iamRequestBody = SdkHttpUtils.encodeParameters(request);

    final AWS4Signer signer = new AWS4Signer();
    signer.setServiceName(serviceName);
    signer.setRegionName(region);
    signer.sign(request, credentials);

    System.out.println("Signed Request Headers:");
    request.getHeaders().forEach((key, value) -> System.out.println(key + ": " + value));
    return AwsAuthLoginInput.builder()
        .iamHttpRequestMethod(methodName.name())
        .iamRequestHeaders("FIXME")
        .iamRequestBody(iamRequestBody)
        .build();
  }
}
