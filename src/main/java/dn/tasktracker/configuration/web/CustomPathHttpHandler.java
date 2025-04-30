package dn.tasktracker.configuration.web;

import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.embedded.undertow.HttpHandlerFactory;


@RequiredArgsConstructor
public class CustomPathHttpHandler implements HttpHandlerFactory {

    private final CustomHttpHandler customHttpHandler;
    @Override
    public HttpHandler getHandler(HttpHandler next) {
        PathHandler pathHandler = new PathHandler(next);
        pathHandler.addExactPath("/", customHttpHandler);
        return pathHandler;
    }
}
