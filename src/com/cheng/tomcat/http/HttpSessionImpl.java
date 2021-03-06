package com.cheng.tomcat.http;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpSessionImpl implements com.cheng.standard.http.HttpSession {
    public final String sessionId;
    private final Map<String, Object> sessionData;

    @Override
    public String toString() {
        return "HttpSessionImpl{" +
                "sessionId='" + sessionId + '\'' +
                ", sessionData=" + sessionData +
                '}';
    }

    // 没有从 cookie 中拿到 session-id 时构建对象使用
    public HttpSessionImpl() {
        sessionId = UUID.randomUUID().toString();
        sessionData = new HashMap<>();
    }
    
    // 从 cookie 中拿到 session-id 时构建对象使用
    public HttpSessionImpl(String sessionId) throws IOException, ClassNotFoundException {
        this.sessionId = sessionId;
        sessionData = loadSessionData(sessionId);
    }

    private static final String SESSION_BASE = "D:\\workspace_idea\\server_HTTP正式\\sessions";
    // 文件名：<session-id>.session
    private Map<String, Object> loadSessionData(String sessionId) throws IOException, ClassNotFoundException {
        String sessionFilename = String.format("%s\\%s.session", SESSION_BASE, sessionId);
        File sessionFile = new File(sessionFilename);
        if (!sessionFile.exists()) {
            return new HashMap<>();
        }

        try (InputStream is = new FileInputStream(sessionFile)) {
            // ObjectInputStream 进行对象读取
            try (ObjectInputStream objectInputStream = new ObjectInputStream(is)) {
                return (Map<String, Object>) objectInputStream.readObject();
            }
        }
    }

    public void saveSessionData() throws IOException {
        if (sessionData.isEmpty()) {
            return;
        }
        String sessionFilename = String.format("%s\\%s.session", SESSION_BASE, sessionId);
        try (OutputStream os = new FileOutputStream(sessionFilename)) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(os)) {
                objectOutputStream.writeObject(sessionData);
                objectOutputStream.flush();
            }
        }
    }

    @Override
    public Object getAttribute(String name) {
        return sessionData.get(name);
    }

    @Override
    public void removeAttribute(String name) {
        sessionData.remove(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        sessionData.put(name, value);
    }
}
