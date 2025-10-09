package com.infisical.sdk.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.IdTokenCredentials;
import com.google.auth.oauth2.IdTokenProvider;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;

class GCPAuthProviderTest {

    @Test
    void testGCPAuthInput_withMockedIdToken() throws IOException {
        // GIVEN
        String identityId = "https://example.com";
        String mockJwt = "mocked.jwt.token";
        Instant expiry = Instant.now().plusSeconds(3600);

        // Mock credentials
        GoogleCredentials mockCredentials = mock(GoogleCredentials.class, withSettings().extraInterfaces(IdTokenProvider.class));
        IdTokenProvider mockIdTokenProvider = (IdTokenProvider) mockCredentials;

        IdTokenCredentials mockIdTokenCredentials = mock(IdTokenCredentials.class);
        AccessToken mockAccessToken = new AccessToken(mockJwt, Date.from(expiry));
        when(mockIdTokenCredentials.refreshAccessToken()).thenReturn(mockAccessToken);

        try (MockedStatic<GoogleCredentials> mockedGoogleCreds = mockStatic(GoogleCredentials.class)) {
            mockedGoogleCreds.when(GoogleCredentials::getApplicationDefault).thenReturn(mockCredentials);

            // WHEN
            GCPAuthProvider provider = new GCPAuthProvider().GCPAuthInput();

            // Patch the real IdTokenCredentials.newBuilder()
            try (MockedStatic<IdTokenCredentials> mockedIdTokenCreds = mockStatic(IdTokenCredentials.class)) {
                IdTokenCredentials.Builder builderMock = mock(IdTokenCredentials.Builder.class);

                when(IdTokenCredentials.newBuilder()).thenReturn(builderMock);
                when(builderMock.setIdTokenProvider(mockIdTokenProvider)).thenReturn(builderMock);
                when(builderMock.setTargetAudience(identityId)).thenReturn(builderMock);
                when(builderMock.setOptions(anyList())).thenReturn(builderMock);
                when(builderMock.build()).thenReturn(mockIdTokenCredentials);

                HashMap<String, String> result = provider.GCPAuthInput(identityId);

                // THEN
                assertEquals(identityId, result.get("identityId"));
                assertEquals(mockJwt, result.get("jwt"));
            }
        }
    }
}
