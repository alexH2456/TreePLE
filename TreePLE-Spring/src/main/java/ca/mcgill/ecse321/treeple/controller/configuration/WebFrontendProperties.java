package ca.mcgill.ecse321.treeple.controller.configuration;

import org.springframework.stereotype.Component;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Component
@ConfigurationProperties(prefix = "client.web")
public class WebFrontendProperties {

    // The IP address of the web frontend client
    private String ip = "192.168.56.50";

    // The port on which the web frontend listens
    private int port = 8087;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
