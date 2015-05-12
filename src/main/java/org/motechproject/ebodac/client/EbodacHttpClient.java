package org.motechproject.ebodac.client;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class EbodacHttpClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(EbodacHttpClient.class);

    public JsonResponse sendJson(String url, String jsonString) {
        return sendJson(url, jsonString, null, null);
    }

    public JsonResponse sendJson(String url, String jsonString, String username, String password) {
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(url);

        if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
            Credentials creds = new UsernamePasswordCredentials(username, password);
            client.getState().setCredentials(AuthScope.ANY, creds);
        }
        try {
            StringRequestEntity input = new StringRequestEntity(jsonString, "application/json", "UTF-8");
            method.setRequestEntity(input);

            JsonResponse jsonResponse = new JsonResponse();
            int status = client.executeMethod(method);
            jsonResponse.setStatus(status);

            InputStream responseStream = method.getResponseBodyAsStream();
            jsonResponse.setJson(
                    IOUtils.toString(responseStream));
            responseStream.close();
            return jsonResponse;
        } catch (HttpException e) {
            LOGGER.error("HttpException occurred while sending request: " + e.getMessage());
        } catch (IOException e) {
            LOGGER.error("IOException occurred while sending request: " + e.getMessage());
        } finally {
            method.releaseConnection();
        }

        return null;
    }

}
