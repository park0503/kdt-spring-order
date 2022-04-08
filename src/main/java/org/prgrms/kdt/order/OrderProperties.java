package org.prgrms.kdt.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.text.MessageFormat;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "kdt")
public class OrderProperties implements InitializingBean {

    private final static Logger logger = LoggerFactory.getLogger(OrderProperties.class);

    private String version;

    private int minimumOrderAmount;

    private List<String> supportVendors;

    private String description;

    @Override
    public void afterPropertiesSet() {
        logger.debug(MessageFormat.format("version -> {0}", version));
        logger.debug(MessageFormat.format("minimumOrderAmount -> {0}", minimumOrderAmount));
        logger.debug(MessageFormat.format("supportVendors -> {0}", supportVendors));
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setMinimumOrderAmount(int minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public void setSupportVendors(List<String> supportVendors) {
        this.supportVendors = supportVendors;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public int getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    public List<String> getSupportVendors() {
        return supportVendors;
    }

    public String getDescription() {
        return description;
    }
}