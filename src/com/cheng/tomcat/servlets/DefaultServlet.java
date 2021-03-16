package com.cheng.tomcat.servlets;

import com.cheng.standard.ServletException;
import com.cheng.standard.http.HttpServlet;
import com.cheng.standard.http.HttpServletRequest;
import com.cheng.standard.http.HttpServletResponse;
import com.cheng.tomcat.HttpServer;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DefaultServlet extends HttpServlet {
    private final String welcomeFile = "/index.html";
    private final Map<String, String> mime = new HashMap<>();
    private final String defaultContentType = "text/plain";

    @Override
    public void init() throws ServletException {
        mime.put("htm", "text/html");
        mime.put("html", "text/html");
        mime.put("jpg", "image/jpeg");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("由我处理静态资源");
        String contextPath = req.getContextPath();
        String servletPath = req.getServletPath();

        if (servletPath.equals("/")) {
            servletPath = welcomeFile;
        }

        String filename = String.format("%s\\%s\\%s", HttpServer.WEBAPPS_BASE, contextPath, servletPath);
        File file = new File(filename);
        if (!file.exists()) {
            // 404 的方式处理
            // req.getDispatcher().forward("404");
            HttpServer.notFoundServlet.service(req, resp);//服务器进行转发
            return;
        }

        String contentType = getContentType(servletPath);
        resp.setContentType(contentType);

        OutputStream outputStream = resp.getOutputStream();
        try (InputStream inputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        }
    }

    private String getContentType(String servletPath) {
        String contentType = defaultContentType;
        int i = servletPath.lastIndexOf('.');
        if (i != -1) {
            String extension = servletPath.substring(i + 1);
            contentType = mime.getOrDefault(extension, defaultContentType);
        }

        return contentType;
    }
}
