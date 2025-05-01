package dn.tasktracker.configuration.web;

import io.undertow.Undertow;
import io.undertow.servlet.api.DeploymentManager;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServer;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.context.annotation.Configuration;

public class WebConfig extends UndertowServletWebServerFactory{

    @Override
    protected UndertowServletWebServer getUndertowWebServer(Undertow.Builder builder, DeploymentManager manager, int port) {
        return super.getUndertowWebServer(builder, manager, port);
    }


}
