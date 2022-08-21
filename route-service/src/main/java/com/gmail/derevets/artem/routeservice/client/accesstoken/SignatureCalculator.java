package com.gmail.derevets.artem.routeservice.client.accesstoken;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SignatureCalculator {


    private final String consumerKey;
    private final String consumerSecret;


    public SignatureCalculator(String consumerKey, String consumerSecret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    /**
     * Calculate the OAuth 1.0 signature base string based on the given parameters
     *
     * @param consumerKey the consumer key
     * @param method the HTTP method
     * @param baseURL The base url including the protocol, host, port, and path. The query portion of the request URL
     * must be assembled in the 'queryParams' input.
     * @param oauthTimestamp the time stamp
     * @param nonce nonce
     * @param signatureMethod signature method to be used - supported are HMAC-SHA1, HMAC-SHA256, ES512
     * @param oauthVersion the oauth_version value; OPTIONAL.  If present, MUST be set to "1.0".  Provides the version
     * of the authentication process as defined in RFC5849.
     * @param formParams the list of form parameters
     * @param queryParams list of query parameters
     * @return computed OAuth 1.0 signature base string.
     */
    private static String computeSignatureBaseString(String consumerKey, String method, String baseURL,
            long oauthTimestamp,
            String nonce, SignatureMethod signatureMethod,
            String oauthVersion,
            Map<String, List<String>> formParams,
            Map<String, List<String>> queryParams) {
        //Create signature base with the http method and base url
        StringBuilder signatureBaseString = new StringBuilder(100);
        signatureBaseString.append(method.toUpperCase());
        signatureBaseString.append('&');
        signatureBaseString.append(urlEncode(normalizeBaseURL(baseURL)));

        //create parameter set with OAuth parameters
        // 3.4.1.3.1.  Parameter Sources
        OAuthParameterSet parameterSet = new OAuthParameterSet();

        // The OAuth HTTP "Authorization" header field (Section 3.5.1) if
        // present.  The header's content is parsed into a list of name/value
        // pairs excluding the "realm" parameter if present.  The parameter
        // values are decoded as defined by Section 3.5.1.
        parameterSet.add("oauth_consumer_key", consumerKey); // decoded consumerKey
        parameterSet.add("oauth_nonce", nonce);
        parameterSet.add("oauth_signature_method", signatureMethod.getOauth1SignatureMethod());
        parameterSet.add("oauth_timestamp", String.valueOf(oauthTimestamp));
        if (null != oauthVersion) {
            parameterSet.add("oauth_version", oauthVersion);
        }

        //add form parameters
        addRequestParameters(formParams, parameterSet);

        //add query parameters
        addRequestParameters(queryParams, parameterSet);

        //sort the parameters by the key and format them into key=value concatenated with &
        String parameterString = parameterSet.sortAndConcat();
        //combine the signature base and parameters
        signatureBaseString.append('&');
        signatureBaseString.append(urlEncode(parameterString));

        return signatureBaseString.toString();
    }

    private static void addRequestParameters(Map<String, List<String>> formParams, OAuthParameterSet parameterSet) {
        if (formParams != null && !formParams.isEmpty()) {
            for (String key : formParams.keySet()) {
                List<String> values = formParams.get(key);
                for (String value : values) {
                    parameterSet.add(key, value);
                }
            }
        }
    }

    /**
     * Sign the cipher text using the given key and the specified algorithm
     *
     * @param signatureBaseString the cipher text to be signed
     * @param key the signing key
     * @param signatureMethod signature method
     * @return signed cipher text
     */
    private static String generateSignature(String signatureBaseString, String key, SignatureMethod signatureMethod) {
        //get the bytes from the signature base string
        byte[] bytesToSign = signatureBaseString.getBytes(StandardCharsets.UTF_8);

        try {
            return computeHMACSignature(bytesToSign, key, signatureMethod.getAlgorithm());
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Utility method to URL encode a given string. If there are any spaces the URLEncodes encodes it to "+" but we
     * require it to be "%20". Also the RFC5849 requires that the character '~' must not be encoded and character '*'
     * has to be encoded since it's not one.
     */
    static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8.name())
                    .replace("+", "%20")
                    .replace("*", "%2A")
                    .replace("%7E", "~");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Compute HMAC digital signature
     *
     * @param bytesToSign bytes to be signed
     * @param algorithm HMAC algorithm to be used.
     * @return signed cipher text
     */
    private static String computeHMACSignature(byte[] bytesToSign, String key, String algorithm) {
        try {
            byte[] keyBytes = (urlEncode(key) + "&").getBytes(StandardCharsets.UTF_8.name());
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, algorithm);

            //generate signature based on the requested signature method
            Mac mac = Mac.getInstance(algorithm);
            mac.init(signingKey);
            byte[] signedBytes = mac.doFinal(bytesToSign);
            return Base64.getEncoder().encodeToString(signedBytes);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Remove the default port from the baseURL
     */
    private static String normalizeBaseURL(String baseURL) {
        int index;
        if (baseURL.startsWith("http:")) {
            index = baseURL.indexOf(":80/", 4);
            if (index > 0) {
                baseURL = baseURL.substring(0, index) + baseURL.substring(index + 3);
            }
        } else if (baseURL.startsWith("https:")) {
            index = baseURL.indexOf(":443/", 5);
            if (index > 0) {
                baseURL = baseURL.substring(0, index) + baseURL.substring(index + 4);
            }
        }

        return baseURL;
    }

    /**
     * Calculate the OAuth 1.0 signature based on the given parameters
     *
     * @param method the HTTP method
     * @param baseURL The base url including the protocol, host, port, and path. The query portion of the request URL
     * must be assembled in the 'queryParams' input.
     * @param oauthTimestamp the time stamp
     * @param nonce nonce
     * @param signatureMethod signature method to be used - supported are HMAC-SHA1, HMAC-SHA256, ES512
     * @param oauthVersion the oauth_version value; OPTIONAL.  If present, MUST be set to "1.0".  Provides the version
     * of the authentication process as defined in RFC5849.
     * @param formParams the list of form parameters
     * @param queryParams list of query parameters
     * @return computed signature using the requested signature method.
     */
    public String calculateSignature(String method, String baseURL, long oauthTimestamp,
            String nonce, SignatureMethod signatureMethod,
            String oauthVersion,
            Map<String, List<String>> formParams,
            Map<String, List<String>> queryParams) {
        String signatureBaseString = computeSignatureBaseString(this.consumerKey, method, baseURL, oauthTimestamp,
                nonce, signatureMethod,
                oauthVersion,
                formParams,
                queryParams);
        return generateSignature(signatureBaseString.toString(), this.consumerSecret, signatureMethod);
    }

    /**
     * Construct the OAuth 1.0 authorization header with the given parameters. The oauth_version is set to "1.0"
     *
     * @param signature the computed signature
     * @param nonce nonce parameter
     * @param oauthTimestamp timestamp parameter
     * @param signatureMethod signature method used to compute this header.
     * @return the Authorization header for OAuth 1.0 calls.
     */
    public String constructAuthHeader(String signature, String nonce, long oauthTimestamp,
            SignatureMethod signatureMethod) {
        return "OAuth "
                + "oauth_consumer_key" + "=\"" + urlEncode(consumerKey)
                + "\", " + "oauth_signature_method" + "=\""
                + signatureMethod.getOauth1SignatureMethod()
                + "\", " + "oauth_signature" + "=\"" + urlEncode(signature)
                + "\", " + "oauth_timestamp" + "=\"" + oauthTimestamp
                + "\", " + "oauth_nonce" + "=\"" + urlEncode(nonce)
                + "\", " + "oauth_version" + "=\"" + "1.0" + "\"";
    }

    /**
     * Container class for Parameters.
     */
    private static final class OAuthParameterSet {

        private final List<Parameter> allParameters = new ArrayList<>();

        /**
         * Add the given URL encoded key-value to the parameter list
         *
         * @param key the parameter key
         * @param value the parameter value
         */
        private void add(String key, String value) {
            allParameters.add(new Parameter(urlEncode(key), urlEncode(value)));
        }

        /**
         * Sort the parameters by their key and concat into key=value format with '&'
         *
         * @return the concatinated parameters in the sorted order.
         */
        private String sortAndConcat() {
            Parameter[] params = new Parameter[allParameters.size()];
            allParameters.toArray(params);
            Arrays.sort(params);
            StringBuilder encodedParams = new StringBuilder(100);

            for (Parameter param : params) {
                if (encodedParams.length() > 0) {
                    encodedParams.append('&');
                }
                encodedParams.append(param.getKey()).append('=').append(param.getValue());
            }
            return encodedParams.toString();
        }
    }

    /**
     * Holds a tuple key-value pair. Implements <code>Comparable</code> for sorting by the key.
     */
    private static final class Parameter implements Comparable<Parameter> {

        private final String key;
        private final String value;

        private Parameter(String key, String value) {
            this.key = key;
            this.value = value;
        }

        private String getKey() {
            return key;
        }

        private String getValue() {
            return value;
        }

        /**
         * Compare the key, if the key is the same, compare by the value.
         */
        @Override
        public int compareTo(Parameter other) {
            int diff = this.key.compareTo(other.key);
            if (diff == 0) {
                diff = this.value.compareTo(other.value);
            }

            return diff;
        }
    }
}
