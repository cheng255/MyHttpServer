package com.cheng.standard.http;

import com.cheng.standard.Servlet;
import com.cheng.standard.ServletException;
import com.cheng.standard.ServletRequest;
import com.cheng.standard.ServletResponse;

import java.io.IOException;

public abstract class HttpServlet implements Servlet {
    @Override
    public void init() throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void service(ServletRequest req, ServletResponse resp) throws ServletException, IOException {
        if (req instanceof HttpServletRequest && resp instanceof HttpServletResponse) {
            HttpServletRequest httpReq = (HttpServletRequest)req;
            HttpServletResponse httpResp = (HttpServletResponse)resp;

            service(httpReq, httpResp);
        } else {
            throw new ServletException("不支持非 HTTP 协议");
        }
    }

    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equals("GET")) {
            doGet(req, resp);
        } else {
            resp.sendError(405);
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendError(405);
    }
}
