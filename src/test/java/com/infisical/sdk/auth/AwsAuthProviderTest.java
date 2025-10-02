package com.infisical.sdk.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.amazonaws.auth.BasicAWSCredentials;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infisical.sdk.models.AwsAuthLoginInput;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class AwsAuthProviderTest {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void testFromCredentials() throws URISyntaxException, JsonProcessingException {
    final AwsAuthProvider provider =
        AwsAuthProvider.builder().overrideDate(new Date(1759446719)).build();
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
            Map.entry("X-Amz-Date", "19700121T084406Z"),
            Map.entry(
                "Authorization",
                "AWS4-HMAC-SHA256 Credential=MOCK_ACCESS_KEY/19700121/us-west-2/sts/aws4_request, SignedHeaders=host;x-amz-date, Signature=f5736d3f614856326236dc15ebcfa47b4b615bc19a9da8fc24b2b767e9c50efa")),
        objectMapper.readValue(loginInput.getIamRequestHeaders(), Map.class));
  }
}
