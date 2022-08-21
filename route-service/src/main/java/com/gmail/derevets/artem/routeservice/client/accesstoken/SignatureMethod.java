package com.gmail.derevets.artem.routeservice.client.accesstoken;

public enum SignatureMethod {
    HMACSHA256("HmacSHA256", "HMAC-SHA256");

    private final String algorithm;
    private final String oauth1SignatureMethod;

    SignatureMethod(String alg, String method) {
        this.algorithm = alg;
        this.oauth1SignatureMethod = method;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getOauth1SignatureMethod() {
        return oauth1SignatureMethod;
    }
}
