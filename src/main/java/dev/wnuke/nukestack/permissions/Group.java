package dev.wnuke.nukestack.permissions;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class Group {
    @SerializedName("Name")
    private String name;
    @SerializedName("Chat Prefix")
    private String prefix = "";
    @SerializedName("Permissions")
    private HashMap<String, Boolean> permissions;
}
