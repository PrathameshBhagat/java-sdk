package com.infisical.sdk.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.amazonaws.auth.BasicAWSCredentials;
import com.infisical.sdk.models.AwsAuthLoginInput;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class AwsAuthProviderTest {

  @Test
  void testFromCredentials() throws URISyntaxException {
    final AwsAuthProvider provider = AwsAuthProvider.builder().build();
    final AwsAuthLoginInput loginInput =
        provider.fromCredentials(
            "us-west-2", new BasicAWSCredentials("MOCK_ACCESS_KEY", "MOCK_SECRET_KEY"));
    assertEquals("POST", loginInput.getIamHttpRequestMethod());
    assertEquals(
        provider.getParams(),
        Arrays.stream(loginInput.getIamRequestBody().split("&"))
            .map(
                item -> {
                  final String[] parts = URLDecoder.decode(item, StandardCharsets.UTF_8).split("=");
                  return Map.entry(parts[0], List.of(parts[1]));
                })
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    assertEquals(
        Map.ofEntries(
            Map.entry("Host", "sts.us-west-2.amazonaws.com"),
            Map.entry("X-Amz-Date", "20251002T230735Z"),
            Map.entry(
                "Authorization",
                "AWS4-HMAC-SHA256 Credential=MOCK_ACCESS_KEY/20251002/us-west-2/sts/aws4_request, SignedHeaders=host;x-amz-date, Signature=09bdfc811945a6696912eb468b046029a67c3418b58588c32d88a7989dc53ed3")),
        loginInput.getIamRequestHeaders());
  }
}
