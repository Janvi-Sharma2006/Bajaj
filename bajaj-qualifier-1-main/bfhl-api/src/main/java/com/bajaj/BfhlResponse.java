package com.bajaj;

import com.fasterxml.jackson.annotation.JsonInclude;

// This ensures null fields (like 'data' in health check) aren't sent in the JSON
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BfhlResponse {
    private boolean is_success;
    private String official_email;
    private Object data;

    public BfhlResponse(boolean is_success, String official_email) {
        this.is_success = is_success;
        this.official_email = official_email;
    }

    public BfhlResponse(boolean is_success, String official_email, Object data) {
        this.is_success = is_success;
        this.official_email = official_email;
        this.data = data;
    }

    // Getters (required for Spring to convert this to JSON)
    public boolean getIs_success() { return is_success; }
    public String getOfficial_email() { return official_email; }
    public Object getData() { return data; }
}