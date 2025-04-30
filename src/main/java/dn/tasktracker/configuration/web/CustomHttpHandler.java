package dn.tasktracker.configuration.web;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.springframework.stereotype.Component;

public class CustomHttpHandler implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {

    }
}
