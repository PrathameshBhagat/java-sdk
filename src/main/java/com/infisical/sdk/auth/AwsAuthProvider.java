package com.infisical.sdk.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infisical.sdk.models.AwsAuthLoginInput;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4FamilyHttpSigner;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4HttpSigner;
import software.amazon.awssdk.http.auth.spi.signer.HttpSigner;

@Data
@Builder
public class AwsAuthProvider {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  @NonNull @Builder.Default private final String serviceName = "sts";
  @NonNull @Builder.Default private final SdkHttpMethod httpMethod = SdkHttpMethod.POST;
  @NonNull @Builder.Default private final String endpointTemplate = "https://sts.%s.amazonaws.com";

  @NonNull @Builder.Default
  private final String contentType = "application/x-www-form-urlencoded; charset=utf-8";

  @NonNull @Builder.Default
  private final Map<String, List<String>> params =
      Map.ofEntries(
          Map.entry("Action", List.of("GetCallerIdentity")),
          Map.entry("Version", List.of("2011-06-15")));

  private final Instant overrideInstant;

  public AwsAuthLoginInput fromCredentials(String region, AwsCredentials credentials) {
    final AwsV4HttpSigner signer = AwsV4HttpSigner.create();
    final String iamRequestURL = endpointTemplate.formatted(region);
    final String iamRequestBody = encodeParameters(params);
    final SdkHttpFullRequest request =
        SdkHttpFullRequest.builder()
            .uri(URI.create(iamRequestURL))
            .method(httpMethod)
            .appendHeader("Content-Type", contentType)
            .build();
    final SdkHttpRequest signedRequest =
        signer
            .sign(
                signingRequest -> {
                  var req =
                      signingRequest
                          .request(request)
                          .identity(credentials)
                          .payload(
                              ContentStreamProvider.fromByteArray(
                                  iamRequestBody.getBytes(StandardCharsets.UTF_8)))
                          .putProperty(AwsV4FamilyHttpSigner.SERVICE_SIGNING_NAME, serviceName)
                          .putProperty(AwsV4HttpSigner.REGION_NAME, region);
                  if (overrideInstant != null) {
                    req.putProperty(
                        HttpSigner.SIGNING_CLOCK, Clock.fixed(overrideInstant, ZoneOffset.UTC));
                  }
                })
            .request();
    final Map<String, String> requestHeaders =
        signedRequest.headers().entrySet().stream()
            .map(entry -> Map.entry(entry.getKey(), entry.getValue().getFirst()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    requestHeaders.put("Content-Length", String.valueOf(iamRequestBody.length()));
    final String encodedHeader;
    try {
      encodedHeader =
          Base64.getEncoder()
              .encodeToString(
                  objectMapper.writeValueAsString(requestHeaders).getBytes(StandardCharsets.UTF_8));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    final String encodedBody =
        Base64.getEncoder().encodeToString(iamRequestBody.getBytes(StandardCharsets.UTF_8));
    return AwsAuthLoginInput.builder()
        .iamHttpRequestMethod(httpMethod.name())
        .iamRequestHeaders(encodedHeader)
        .iamRequestBody(encodedBody)
        .build();
  }

  public static String encodeParameters(Map<String, List<String>> params) {
    return params.entrySet().stream()
        .flatMap(entry -> entry.getValue().stream().map(item -> Map.entry(entry.getKey(), item)))
        .map(
            entry ->
                String.format(
                    "%s=%s",
                    URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8),
                    URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8)))
        .collect(Collectors.joining("&"));
  }
}
