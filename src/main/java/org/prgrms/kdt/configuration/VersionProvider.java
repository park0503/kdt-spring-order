package org.prgrms.kdt.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:version.properties")
public class VersionProvider {
    private final String version;

    public VersionProvider(@Value("${version:v0.0.0}") String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}
