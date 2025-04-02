package com.bucket.thingstodobeforedie.enums;

public enum SocialProvider {
    GOOGLE("google"),
    FACEBOOK("facebook"),
    LOCAL("local");
    
    private final String providerType;
    
    SocialProvider(String providerType) {
        this.providerType = providerType;
    }
    
    public String getProviderType() {
        return providerType;
    }
    
    public static SocialProvider fromString(String providerType) {
        for (SocialProvider provider : SocialProvider.values()) {
            if (provider.getProviderType().equalsIgnoreCase(providerType)) {
                return provider;
            }
        }
        return LOCAL; // Default to local provider if not found
    }
} 