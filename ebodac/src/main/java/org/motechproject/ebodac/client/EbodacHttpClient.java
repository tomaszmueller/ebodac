package org.motechproject.ebodac.client;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
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

    public HttpResponse sendJson(String url, String jsonString) {
        return sendJson(url, jsonString, null, null);
    }

    public HttpResponse sendJson(String url, String jsonString, String username, String password) {
        try {
            StringRequestEntity entity = new StringRequestEntity(jsonString, "application/json", "UTF-8");

            return sendRequestEntity(url, username, password, entity);
        } catch (Exception e) {
            LOGGER.error("Fatal exception occurred while sending request: " + e.getMessage(), e);
        }

        return null;
    }

    public HttpResponse sendCsvFile(String url, String username, String password, InputStream file) {
        try {
            ByteArrayRequestEntity entity = new ByteArrayRequestEntity(IOUtils.toByteArray(file));

            return sendRequestEntity(url, username, password, entity);
        } catch (Exception e) {
            LOGGER.error("Fatal exception occurred while sending request: " + e.getMessage(), e);
        }

        return null;
    }

    private HttpResponse sendRequestEntity(String url, String username, String password, RequestEntity entity) {
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(url);
        if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
            Credentials creds = new UsernamePasswordCredentials(username, password);
            client.getState().setCredentials(AuthScope.ANY, creds);
        }
        try {
            method.setRequestEntity(entity);

            HttpResponse httpResponse = new HttpResponse();
            int status = client.executeMethod(method);
            httpResponse.setStatus(status);
            Header contentType = method.getResponseHeader("Content-Type");
            if (contentType != null) {
                httpResponse.setContentType(contentType.getValue());
            }
            if (method.getResponseBodyAsStream() != null) {
                InputStream responseStream = method.getResponseBodyAsStream();
                httpResponse.setResponseBody(IOUtils.toString(responseStream));
                responseStream.close();
            }

            return httpResponse;
        } catch (HttpException e) {
            LOGGER.error("HttpException occurred while sending request: " + e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.error("IOException occurred while sending request: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("Fatal exception occurred while sending request: " + e.getMessage(), e);
        } finally {
            method.releaseConnection();
        }

        return null;
    }
}
