package com.infisical.sdk; 

import com.infisical.sdk.InfisicalSdk;
import com.infisical.sdk.config.*;

public class m {

  public static void main(String[] args) throws Exception{
    InfisicalSdk sdk = new InfisicalSdk(
      new SdkConfig.Builder()
        // Optional, will default to https://app.infisical.com
        .withSiteUrl("https://app.infisical.com")
        .build()
    );
/*
    sdk.Auth().UniversalAuthLogin(
      "44860ed7-4232-44c9-93c5-69d1f2522280",
      "16025831b6eff1771871a63ab003c1cd3bbbd7ba6be49a7331b53c8916bde1c5"
    );
*/
    sdk.Auth().GCPAuthLogin("7a864564-2a96-4254-bbf5-c2211e237868");

    var secret = sdk.Secrets().ListSecrets(
      "96c6a555-a933-42b8-baa8-204bc9773ed3",
      "dev",
      "/",
      null, // Expand Secret References (boolean, optional)
      null, // Include Imports (boolean, optional)
      null  // Secret Type (shared/personal, defaults to shared, optional)
      );


    System.out.println(secret);
  }
}