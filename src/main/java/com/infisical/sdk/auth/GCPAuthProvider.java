package com.infisical.sdk.auth;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.IdTokenCredentials;
import com.google.auth.oauth2.IdTokenProvider;
import com.google.auth.oauth2.IdTokenProvider.Option;

public class GCPAuthProvider {

    public static HashMap<String,String> getGCPAuthInput(String identityId){

        try{

            // This will fetch credentials from environment variable named GOOGLE_APPLICATION_CREDENTIALS or
            // or if it's running in a GCP instance it will get them from the instance itself (GCP service account attached) 
            GoogleCredentials googleCredentials = GoogleCredentials.getApplicationDefault();

            IdTokenCredentials idTokenCredentials =
                IdTokenCredentials.newBuilder()
                    .setIdTokenProvider((IdTokenProvider) googleCredentials)
                    .setTargetAudience(identityId)
                    .setOptions(Arrays.asList(Option.FORMAT_FULL, Option.LICENSES_TRUE))
                    .build();
    
            // Get the ID token.
            String idToken = idTokenCredentials.refreshAccessToken().getTokenValue();
    
            // Body cannot be a string so... HashMap can use bulider, POJO etc
            HashMap<String, String> body =  new HashMap<>();
              body.put("identityId", identityId);
              body.put("jwt", idToken);

            return body;

        } catch (IOException e){
            throw new RuntimeException(e);
        }

    }
}
