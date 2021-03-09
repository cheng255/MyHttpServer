package com.cheng.standard;

import com.cheng.standard.ServletRequest;

import java.io.IOException;

public interface Servlet {
    void init() throws ServletException;

    void service(ServletRequest req, ServletResponse resp) throws ServletException, IOException;

    void destroy();
}
