package com.infisical.sdk.models;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ListFoldersResponse {
  @SerializedName("folders")
  private List<Folder> folders;

  public List<Folder> getFolders() {
    return folders;
  }
}