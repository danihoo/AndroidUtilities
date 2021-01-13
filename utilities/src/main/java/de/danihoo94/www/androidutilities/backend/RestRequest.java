package de.danihoo94.www.androidutilities.backend;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static de.danihoo94.www.androidutilities.backend.DataException.STATUS_AUTH_FAILURE;
import static de.danihoo94.www.androidutilities.backend.DataException.STATUS_SERVER_ERROR;
import static de.danihoo94.www.androidutilities.backend.DataException.STATUS_TIMEOUT;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class RestRequest {

    public static final String REQUEST_METHOD_GET = "GET";
    public static final String REQUEST_METHOD_POST = "POST";
    public static final String REQUEST_METHOD_PUT = "PUT";
    public static final String REQUEST_METHOD_PATCH = "PATCH";
    public static final String REQUEST_METHOD_DELETE = "DELETE";

    public static final String AUTH_METHOD_BEARER = "Bearer";

    // static attributes
    private final String urlBase;
    private final String urlExtension;
    private final String authorizationMethod;
    private final String authorization;
    private final String contentType;
    private final List<Header> customHeaders;
    private final String requestMethod;
    private final int retryAttempts;
    private final int[] retryCodes;
    private final JsonObject body;

    private RestRequest(Builder builder) {
        urlBase = builder.urlBase;
        urlExtension = builder.urlExtension;
        authorizationMethod = builder.authorizationMethod;
        authorization = builder.authorization;
        contentType = builder.contentType;
        customHeaders = builder.customHeaders;
        requestMethod = builder.requestMethod;
        retryAttempts = builder.retryAttempts;
        retryCodes = builder.retryCodes;
        body = builder.body;
    }

    public JsonElement perform() throws DataException {
        DataException lastException = null;
        for (int i = 0; i <= retryAttempts; i++) {
            try {
                return performRequest();
            } catch (DataException e) {
                for (int code : retryCodes) {
                    boolean found = false;
                    if (e.getHtmlStatus() == code) {
                        found = true;
                    }
                    if (!found) {
                        throw e;
                    } else {
                        lastException = e;
                    }
                }
            }
        }
        // if this point of code is reached, all request attempts failed
        throw lastException;
    }

    private JsonElement performRequest() throws DataException {

        // Connect
        try {
            URL url = new URL(urlBase + urlExtension);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setUseCaches(false);
            con.setRequestProperty("Accept", "*/*");
            if (authorization != null) {
                con.setRequestProperty("Authorization", authorizationMethod + " " + authorization);
            }

            // Write request body
            if (requestMethod.equals(REQUEST_METHOD_GET)) {
                con.setRequestMethod(requestMethod);
            } else {
                con.setDoOutput(true);

                // PATCH and DELETE must be executed as POST with special header
                con.setRequestMethod(REQUEST_METHOD_POST);
                if (!requestMethod.equals(REQUEST_METHOD_POST)) {
                    con.setRequestProperty("X-HTTP-Method-Override", requestMethod);
                }

                if (contentType != null) {
                    con.setRequestProperty("Content-Type", contentType);
                }

                for (Header h : customHeaders) {
                    con.setRequestProperty(h.type, h.value);
                }

                OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());
                if (body != null) {
                    os.write(new Gson().toJson(body));
                }
                os.flush();
                os.close();
            }

            // execute
            int status = con.getResponseCode();

            if (status >= 400) {
                throw new DataException("Request failed", status);
            }

            // Create input stream
            InputStreamReader isr = new InputStreamReader(con.getInputStream());
            BufferedReader reader = new BufferedReader(isr);

            // Read as long as data is available
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            JsonElement responseJson = JsonParser.parseString(response.toString());
            if (responseJson != null) {
                return responseJson;
            } else {
                throw new IOException("Backend response does not contain any object.");
            }
        } catch (IOException e) {
            throw new DataException(e.getMessage());
        }
    }

    public static class Builder {
        private final List<Header> customHeaders = new ArrayList<>();

        private String urlBase = null;
        private String urlExtension = "";
        private String authorizationMethod = null;
        private String authorization = null;
        private String contentType = null;
        private String requestMethod = REQUEST_METHOD_GET;
        private int retryAttempts = 3;
        private int[] retryCodes = new int[]{STATUS_AUTH_FAILURE, STATUS_TIMEOUT, STATUS_SERVER_ERROR};
        private JsonObject body = null;

        public Builder setContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public RestRequest build() {
            return new RestRequest(this);
        }

        public Builder setUrlBase(String url) {
            this.urlBase = url;
            return this;
        }

        public Builder setUrlExtension(String url) {
            this.urlExtension = url;
            return this;
        }

        public Builder setAuthorizationMethod(String authorizationMethod) {
            this.authorizationMethod = authorizationMethod;
            return this;
        }

        public Builder setAuthorization(String authorization) {
            this.authorization = authorization;
            return this;
        }

        public Builder setRequestMethod(String requestMethod) {
            this.requestMethod = requestMethod;
            return this;
        }

        public Builder setRetryAttempts(int retryAttempts) {
            this.retryAttempts = retryAttempts;
            return this;
        }

        public Builder setRetryCodes(int[] retryCodes) {
            this.retryCodes = retryCodes;
            return this;
        }

        public Builder setBody(JsonObject body) {
            this.body = body;
            return this;
        }

        public Builder addCustomHeader(String type, String value) {
            this.customHeaders.add(new Header(type, value));
            return this;
        }
    }

    private static class Header {
        final String type;
        final String value;

        public Header(String type, String value) {
            this.type = type;
            this.value = value;
        }
    }
}