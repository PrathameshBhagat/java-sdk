package com.infisical.sdk.auth;

import com.amazonaws.DefaultRequest;
import com.amazonaws.Request;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.util.SdkHttpUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infisical.sdk.models.AwsAuthLoginInput;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AwsAuthProvider {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Builder.Default private final String serviceName = "sts";
  @Builder.Default private final HttpMethodName methodName = HttpMethodName.POST;
  @Builder.Default private final String endpointTemplate = "https://sts.%s.amazonaws.com";

  @Builder.Default
  private final Map<String, List<String>> params =
      Map.ofEntries(
          Map.entry("Action", List.of("GetCallerIdentity")),
          Map.entry("Version", List.of("2011-06-15")));

  private final Date overrideDate;

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
    if (overrideDate != null) {
      signer.setOverrideDate(overrideDate);
    }
    signer.sign(request, credentials);

    final Map<String, String> requestHeaders = request.getHeaders();
    // TODO: add content-type and content length?
    //       ref:
    // https://github.com/Infisical/go-sdk/blob/3f1b7f831a883ea30a9182f86bd0173f4cf82a22/auth.go#L293-L294

    try {
      return AwsAuthLoginInput.builder()
          .iamHttpRequestMethod(methodName.name())
          .iamRequestHeaders(objectMapper.writeValueAsString(requestHeaders))
          .iamRequestBody(iamRequestBody)
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
