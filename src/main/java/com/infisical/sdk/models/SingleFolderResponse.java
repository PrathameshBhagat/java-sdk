package com.infisical.sdk.models;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public class SingleFolderResponse {
  @SerializedName("folder")
  private Folder folder;

  public Folder getFolder() {
    return folder;
  }
}