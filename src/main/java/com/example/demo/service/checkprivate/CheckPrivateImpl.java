package com.example.demo.service.checkprivate;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class CheckPrivateImpl implements CheckPrivate {

    private Environment environment;
    private String host;
    private String port;
    private String link;

    public CheckPrivateImpl(Environment environment) {
        this.environment = environment;
        this.host = environment.getProperty("private.host");
        this.port = environment.getProperty("private.port");
    }

    @Override
    public Boolean checkPrivate(String path) {
        return null;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return Integer.parseInt(this.port);
    }

//    public String getUser(String path) {
//
//    }

}
