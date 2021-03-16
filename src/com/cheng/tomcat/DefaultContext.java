package com.cheng.tomcat;


public class DefaultContext extends Context {
    public DefaultContext(ConfigReader reader) {
        super(reader, "/");
    }

    @Override
    public com.cheng.standard.Servlet get(String servletPath) {
        return HttpServer.notFoundServlet;
    }
}
