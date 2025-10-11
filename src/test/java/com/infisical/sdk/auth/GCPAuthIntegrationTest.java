package com.infisical.sdk.auth;

import com.infisical.sdk.InfisicalSdk;
import com.infisical.sdk.config.SdkConfig;
import com.infisical.sdk.util.EnvironmentVariables;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

public class GCPAuthIntegrationTest{

    private static final Logger logger = LoggerFactory.getLogger(GCPAuthIntegrationTest.class);
    @Test
    public void testGCPAuthAndFetchSecrets() throws Exception {

        // Load env variables
        var envVars = new EnvironmentVariables();

        // Check if env vars are set
        assertNotNull(envVars.getSiteUrl(), "INFISICAL_SITE_URL env variable must be set");
        assertNotNull(envVars.getMachineIdentityId(), "INFISICAL_MACHINE_IDENTITY_ID env variable must be set");


        // Create SDK instance
        var sdk = new InfisicalSdk(new SdkConfig.Builder()
                .withSiteUrl(envVars.getSiteUrl())
                .build()
        );

        // Authenticate using GCP
        assertDoesNotThrow(() -> {
            sdk.Auth().GCPAuthLogin(envVars.getMachineIdentityId());
        });



        try {
            var secrets = sdk.Secrets().ListSecrets(
                                    envVars.getProjectId(), 
                                    "dev", 
                                    "/",
                                    null,
                                    null,
                                    null);
                
            assertNotNull(secrets, "Secrets list should not be null");
            assertFalse(secrets.isEmpty(), "Secrets list should not be empty");

            logger.info("TestGCPAuthListSecret: Secret {}", secrets);

        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}
