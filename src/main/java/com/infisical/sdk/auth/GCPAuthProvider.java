package com.infisical.sdk.auth;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.IdTokenCredentials;
import com.google.auth.oauth2.IdTokenProvider;
import com.google.auth.oauth2.IdTokenProvider.Option;
import com.google.auto.value.AutoValue.Builder;

import lombok.Data;

@Data
@Builder
public class GCPAuthProvider {

    public HashMap<String,String> GCPAuthInput(String identityId){

        try{

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
