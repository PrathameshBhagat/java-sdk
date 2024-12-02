package com.infisical.sdk.resources;



import com.infisical.sdk.ApiException;
import com.infisical.sdk.models.*;
import com.infisical.sdk.util.Helper;


import com.infisical.sdk.api.DefaultApi;
import com.infisical.sdk.ApiClient;
import com.infisical.sdk.util.InfisicalException;

import java.math.BigDecimal;

public class SecretsClient {
    private final DefaultApi request;

    public SecretsClient(ApiClient apiClient) {
        this.request = new DefaultApi(apiClient);
    }

    public InlineResponse200 ListSecrets(String projectId, String environmentSlug, String secretPath, Boolean expandSecretReferences, Boolean recursive, Boolean includeImports) throws InfisicalException {
        try {

        return this.request.apiV3SecretsRawGet(
                projectId,
                null,
                environmentSlug,
                secretPath,
                Helper.booleanToString(expandSecretReferences),
                Helper.booleanToString(recursive),
                Helper.booleanToString(includeImports),
                null
        );

        } catch (ApiException e) {
            throw new InfisicalException(e);
        }
    }

    public InlineResponse2001 GetSecret(String secretName, String projectId, String environmentSlug, String secretPath, Boolean expandSecretReferences, Boolean includeImports, String secretType) throws InfisicalException {
        try {
            return this.request.apiV3SecretsRawSecretNameGet(
                secretName,
                projectId,
                null,
                environmentSlug,
                secretPath,
                null,
                secretType,
                Helper.booleanToString(expandSecretReferences),
                Helper.booleanToString(includeImports)
            );
        } catch (ApiException e) {
            throw new InfisicalException(e);
        }
    }

    public InlineResponse2002 UpdateSecret(String secretName, String projectId, String environmentSlug, String secretPath, String newSecretValue, String newSecretName) throws InfisicalException {

        try {
            var params = new RawSecretNameBody2();

            params.setEnvironment(environmentSlug);
            params.setSecretPath(secretPath);
            params.setWorkspaceId(projectId);




            if (newSecretName != null) params.setNewSecretName(newSecretName);
            if (newSecretName != null) params.setSecretValue(newSecretValue);


            return this.request.apiV3SecretsRawSecretNamePatch(params, secretName);
        } catch (ApiException e) {
            throw new InfisicalException(e);
        }
    }

    public InlineResponse2002 CreateSecret(String secretName, String secretValue, String projectId, String environmentSlug, String secretPath) throws InfisicalException {
        try {
            var params = new RawSecretNameBody();

            params.setWorkspaceId(projectId);
            params.setEnvironment(environmentSlug);
            params.setSecretPath(secretPath);

            params.setSecretValue(!secretValue.isEmpty() ? secretValue : "");

            return this.request.apiV3SecretsRawSecretNamePost(params, secretName);

        } catch (ApiException e) {
            throw new InfisicalException(e);
        }
    }

    public InlineResponse2002 DeleteSecret(String secretName, String projectId, String environmentSlug, String secretPath) throws InfisicalException {
        try {

            var params = new RawSecretNameBody1();

            params.setWorkspaceId(projectId);
            params.setEnvironment(environmentSlug);
            params.setSecretPath(secretPath);

            return this.request.apiV3SecretsRawSecretNameDelete(params, secretName);

        } catch (ApiException e) {
            throw new InfisicalException(e);
        }
    }


}