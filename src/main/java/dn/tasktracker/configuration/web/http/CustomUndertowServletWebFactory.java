package dn.tasktracker.configuration.web.http;

import dn.tasktracker.configuration.web.CustomHttpHandler;
import io.undertow.Undertow;
import io.undertow.servlet.api.DeploymentManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.undertow.UndertowBuilderCustomizer;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServer;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.stereotype.Component;


public abstract class CustomUndertowServletWebFactory extends UndertowServletWebServerFactory {


    private CustomHttpHandler customHttpHandler;

    public CustomUndertowServletWebFactory(
            ObjectProvider<UndertowDeploymentInfoCustomizer> deploymentInfoCustomizers,
            ObjectProvider<UndertowBuilderCustomizer> builderCustomizers,
            CustomHttpHandler customHttpHandler) {
        this.customHttpHandler = customHttpHandler;
        this.getDeploymentInfoCustomizers().addAll(deploymentInfoCustomizers.orderedStream().toList());
        this.getBuilderCustomizers().addAll(builderCustomizers.orderedStream().toList());

    }

    @Override
    protected UndertowServletWebServer getUndertowWebServer(Undertow.Builder builder, DeploymentManager manager, int port) {
        return super.getUndertowWebServer(builder, manager, port);
    }
}
