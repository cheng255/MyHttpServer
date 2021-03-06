package com.cheng.tomcat;

import com.cheng.tomcat.http.Request;
import com.cheng.tomcat.http.Response;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class RequestResponseTask implements Runnable {
    private static final com.cheng.tomcat.http.HttpRequestParser parser = new com.cheng.tomcat.http.HttpRequestParser();

    private final Socket socket;

    public RequestResponseTask(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            // 1. 解析并得到请求对象
            Request request = parser.parse(socket.getInputStream());
            System.out.println(request);

            // 2. 实例化一个响应对象
            Response response = new Response();
            // 3. 根据 request.getContextPath() 找到哪个 Context 进行处理
            Context handleContext = com.cheng.tomcat.HttpServer.defaultContext;
            for (Context context : com.cheng.tomcat.HttpServer.contextList) {
                if (context.getName().equals(request.getContextPath())) {
                    handleContext = context;
                    break;
                }
            }
            // 4. 根据 request.getServletPath() 找到 Context 中的哪个 HttpServlet 进行处理
            com.cheng.standard.Servlet servlet = handleContext.get(request.getServletPath());
            if (servlet == null) {
                servlet = com.cheng.tomcat.HttpServer.defaultServlet;
            }
            // 5. 调用 servlet.service(request, response)，交给业务处理
            servlet.service(request, response);
            //System.out.println(response);
            // 6. 根据 response 对象中的数据，发送 HTTP 响应
            sendResponse(socket.getOutputStream(), request, response);

            socket.close();
        } catch (Exception exc) {
            exc.printStackTrace(System.out);
        }
    }

    private void sendResponse(OutputStream outputStream, Request request, Response response) throws IOException {
        // 保存 session
        // 1. 种 cookie
        // 2. 保存成文件
        if (request.session != null) {
            com.cheng.standard.http.Cookie cookie = new com.cheng.standard.http.Cookie("session-id", request.session.sessionId);
            response.addCookie(cookie);
            request.session.saveSessionData();
        }

        Writer writer = new OutputStreamWriter(outputStream, "UTF-8");
        PrintWriter printWriter = new PrintWriter(writer);
        for (com.cheng.standard.http.Cookie cookie : response.cookieList) {
            response.setHeader("Set-Cookie", String.format("%s=%s", cookie.getName(), cookie.getValue()));
        }

        printWriter.printf("HTTP/1.0 %d\r\n", response.status);
        for (Map.Entry<String, String> entry : response.headers.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();

            printWriter.printf("%s: %s\r\n", name, value);
        }
        printWriter.printf("\r\n");
        response.bodyPrintWriter.flush();
        response.bodyOutputStream.flush();
        printWriter.flush();

        byte[] bytes = response.bodyOutputStream.toByteArray();
        outputStream.write(bytes);
        outputStream.flush();
    }
}
