package com.anner.comm.http;

import com.anner.comm.common.CommClientFactory;
import com.anner.comm.common.ProxiedCommClient;
import com.anner.comm.http.helper.DelayCloseResponseStream;
import com.anner.comm.http.helper.HTTPArgsPayload;
import com.anner.comm.info.CommProperties;
import com.anner.comm.stream.CommStreamPipe;
import com.anner.common.json.JSONSerializeUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.InputStream;
import java.net.URI;

/**
 * Created by anner on 2023/3/21
 */
public class HTTPCommClient extends ProxiedCommClient {
    private static final String SELF_TYPE = "http";
    private static final String SCHEMA = "schema";
    private static final String PATH = "path";

    private final String url;


    static {
        // 类加载时将自身注册到工厂中
        CommClientFactory.registerClient(SELF_TYPE, HTTPCommClient::new);
    }

    private final CloseableHttpClient httpClient = HttpClients.createDefault();


    public HTTPCommClient(CommProperties properties) {
        super(properties);
        url = String.format(
                "%s://%s:%d%s",
                properties.getAsString(SCHEMA, "http"),
                properties.getAsString(CommProperties.HOST, "127.0.0.1"),
                properties.getAsInt(CommProperties.PORT, 8080),
                properties.getAsString(PATH, "/")
        );
    }

    public HTTPCommClient(String url) {
        this(parseProperties(url));
    }

    @Override
    protected Object invoke(String service, String method, Object[] args, Class<?> returnType) throws Exception {
        HTTPArgsPayload payload = new HTTPArgsPayload(args);
        HttpPost req = new HttpPost(
                new URIBuilder(url)
                        .addParameter("service", service)
                        .addParameter("method", method)
                        .build()
        );
        req.setEntity(buildEntity(payload));
        CloseableHttpResponse res = null;
        boolean delayClose = false;
        try {
            res = httpClient.execute(req);
            HttpEntity entity = res.getEntity();
            if (res.getStatusLine().getStatusCode() >= 400) {
                throw new Exception(EntityUtils.toString(entity));
            }
            if (returnType == Void.class) {
                EntityUtils.consume(entity);
                return null;
            }
            if (returnType == CommStreamPipe.class) {
                delayClose = true;
                return CommStreamPipe.wrap(new DelayCloseResponseStream(entity.getContent(), res));
            }
            return JSONSerializeUtils.deserialize(EntityUtils.toString(entity), returnType);
        } finally {
            if (res != null && !delayClose) {
                res.close();
            }
        }
    }

    private HttpEntity buildEntity(HTTPArgsPayload payload) throws JsonProcessingException {
        Object[] args = payload.getArgs();
        if (args.length == 1 && args[0] instanceof CommStreamPipe) {
            return new InputStreamEntity((InputStream) args[0], ContentType.APPLICATION_OCTET_STREAM);
        }
        return new StringEntity(JSONSerializeUtils.serialize(payload), ContentType.APPLICATION_JSON);
    }

    private static CommProperties parseProperties(String url) {
        URI uri = URI.create(url);
        CommProperties p = new CommProperties();
        p.put(CommProperties.TYPE, SELF_TYPE);
        p.put(CommProperties.HOST, uri.getHost());
        p.put(CommProperties.PORT, uri.getPort());
        p.put(CommProperties.WEIGHT, 1);
        p.put(SCHEMA, uri.getScheme());
        p.put(PATH, uri.getRawPath());
        return p;
    }
}
