package com.infisical.sdk.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infisical.sdk.models.AwsAuthLoginInput;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;

class AwsAuthProviderTest {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void testFromCredentials() throws JsonProcessingException {
    final AwsAuthProvider provider =
        AwsAuthProvider.builder().overrideInstant(Instant.ofEpochSecond(1759446719)).build();
    final AwsAuthLoginInput loginInput =
        provider.fromCredentials(
            "us-west-2", AwsBasicCredentials.create("MOCK_ACCESS_KEY", "MOCK_SECRET_KEY"));
    assertEquals("POST", loginInput.getIamHttpRequestMethod());

    final String decodedBody =
        new String(
            Base64.getDecoder().decode(loginInput.getIamRequestBody()), StandardCharsets.UTF_8);
    final Map<String, List<String>> bodyParams =
        Arrays.stream(decodedBody.split("&"))
            .map(
                item -> {
                  final String[] parts = URLDecoder.decode(item, StandardCharsets.UTF_8).split("=");
                  return Map.entry(parts[0], List.of(parts[1]));
                })
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    assertEquals(provider.getParams(), bodyParams);

    final String decodedHeaders =
        new String(
            Base64.getDecoder().decode(loginInput.getIamRequestHeaders()), StandardCharsets.UTF_8);
    final Map<String, String> actualHeaders = objectMapper.readValue(decodedHeaders, Map.class);
    assertEquals(
        Map.ofEntries(
            Map.entry("Host", "sts.us-west-2.amazonaws.com"),
            Map.entry("Content-Type", "application/x-www-form-urlencoded; charset=utf-8"),
            Map.entry("Content-Length", "43"),
            Map.entry(
                "x-amz-content-sha256",
                "2e5272931159dc39306511e6dbae66f365e6748f021352ad514b568d66ebba7c"),
            Map.entry("X-Amz-Date", "20251002T231159Z"),
            Map.entry(
                "Authorization",
                "AWS4-HMAC-SHA256 Credential=MOCK_ACCESS_KEY/20251002/us-west-2/sts/aws4_request, SignedHeaders=content-type;host;x-amz-content-sha256;x-amz-date, Signature=0b47d8813cf09e9f5cc7a1edce21384e8d9c11ae8cd4a1599d6f307672d4c421")),
        actualHeaders);
  }
}
