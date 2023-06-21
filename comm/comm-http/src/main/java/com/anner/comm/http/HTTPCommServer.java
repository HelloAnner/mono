package com.anner.comm.http;

import com.anner.comm.common.ProxiedCommServer;
import com.anner.comm.http.helper.HTTPArgsPayload;
import com.anner.comm.stream.CommStreamPipe;
import com.anner.common.json.JSONSerializeUtils;
import com.anner.common.log.Logger;
import com.anner.common.string.StringUtils;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created by anner on 2023/3/21
 */
public class HTTPCommServer extends ProxiedCommServer {
    @Override
    public String type() {
        return "http";
    }

    /**
     * 处理http请求
     */
    public void handleRequest(HttpServletRequest req, HttpServletResponse res) {
        // 暂时先简单实现一下
        try {
            String service = req.getParameter("service");
            String method = req.getParameter("method");
            if (StringUtils.isEmpty(service) || StringUtils.isEmpty(method)) {
                throw new IllegalArgumentException("service or method is invalid");
            }
            String contentType = req.getContentType();
            if (StringUtils.isNotEmpty(contentType)) {
                if (contentType.contains("application/json")) {
                    // json格式的普通参数
                    HTTPArgsPayload argsPayload = JSONSerializeUtils.deserialize(
                            IOUtils.toString(req.getInputStream(), "UTF-8"),
                            HTTPArgsPayload.class
                    );
                    handleResult(res, handle(service, method, argsPayload.getArgs()));
                    return;
                }
                if (contentType.contains("application/octet-stream")) {
                    // 流参数
                    handleResult(res, handle(service, method, new Object[]{CommStreamPipe.wrap(req.getInputStream())}));
                    return;
                }
            }
            throw new IllegalArgumentException("invalid Content-Type " + contentType);
        } catch (Throwable e) {
            Logger.error(e.getMessage(), e);
            try {
                handleThrowable(res, e);
            } catch (Exception e1) {
                Logger.error(e1.getMessage(), e1);
            }
        } finally {
            try {
                ServletOutputStream sos = res.getOutputStream();
                sos.flush();
                sos.close();
            } catch (IOException e) {
                Logger.error(e.getMessage(), e);
            }
        }
    }

    private void handleThrowable(HttpServletResponse res, Throwable e) throws Exception {
        res.setContentType("text/plain");
        if (e instanceof IllegalArgumentException) {
            res.setStatus(400);
        } else {
            res.setStatus(500);
        }
        res.getOutputStream().write(String.format("%s: %s", e.getClass(), e.getMessage()).getBytes(StandardCharsets.UTF_8));
    }

    private void handleResult(HttpServletResponse res, Object result) throws Exception {
        res.setStatus(200);
        if (result instanceof CommStreamPipe) {
            res.setContentType("application/octet-stream");
            ((CommStreamPipe) result).doWrite(res.getOutputStream());
            return;
        }
        res.setContentType("application/json");
        res.getOutputStream().write(JSONSerializeUtils.serialize(result).getBytes(StandardCharsets.UTF_8));
    }

}
