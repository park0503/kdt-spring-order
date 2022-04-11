package org.prgrms.kdt;

import org.prgrms.kdt.AppConfiguration;
import org.prgrms.kdt.order.OrderItem;
import org.prgrms.kdt.order.OrderProperties;
import org.prgrms.kdt.order.OrderService;
import org.prgrms.kdt.voucher.JdbcVoucherRepository;
import org.prgrms.kdt.voucher.VoucherRepository;
import org.prgrms.kdt.voucher.FixedAmountVoucher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.Assert;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.UUID;

public class OrderTester {
    private static final Logger logger = LoggerFactory.getLogger(OrderTester.class);

    public static void main(String[] args) throws IOException {
        AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);
        var applicationContext = new AnnotationConfigApplicationContext(AppConfiguration.class);


//        var environment = applicationContext.getEnvironment();
//        applicationContext.register(AppConfiguration.class);
//
//        environment.setActiveProfiles("local");
//        applicationContext.refresh();

        var orderProperties = applicationContext.getBean(OrderProperties.class);
        logger.info("logger name => {}", logger.getName());
        logger.info("version -> {}", orderProperties.getVersion());
        logger.info("moa -> {}", orderProperties.getMinimumOrderAmount());
        logger.info("sv -> {}", orderProperties.getSupportVendors());
        logger.info("d -> {}", orderProperties.getDescription());


//        var version = environment.getProperty("kdt.version");
//        var minimumOrderAmount = environment.getProperty("kdt.minimum-order-amount", Integer.class);
//        var supportVendors = environment.getProperty("kdt.support-vendors", List.class);


//        System.out.println(version);
//        System.out.println(minimumOrderAmount);
//        System.out.println(MessageFormat.format("supportVendors -> {0}", supportVendors));

//        var resource = applicationContext.getResource("classpath:application.yaml");
//        var resource2 = applicationContext.getResource("file:sample.txt");
//        var resource3 = applicationContext.getResource(("https://stackoverflow.com/"));
//        System.out.println(MessageFormat.format("Resource -> {0}", resource3.getClass().getCanonicalName()));
//
//        var readableByteChannel = Channels.newChannel(resource3.getURL().openStream());
//        var bufferReader = new BufferedReader(Channels.newReader(readableByteChannel, StandardCharsets.UTF_8));
//        var contents = bufferReader.lines().collect(Collectors.joining("\n"));
//        System.out.println(contents);
//
//
//        var yaml = resource.getFile();
//        var file = resource2.getFile();
//        var strings = Files.readAllLines(file.toPath());
//        System.out.println(strings.stream().reduce("", (a, b) -> a + "\n" + b));

        var customerId = UUID.randomUUID();
        //var voucherRepository = BeanFactoryAnnotationUtils.qualifiedBeanOfType(applicationContext.getBeanFactory(), VoucherRepository.class, "memory");
        var voucherRepository = applicationContext.getBean(VoucherRepository.class);
        var voucher = voucherRepository.insert(new FixedAmountVoucher(UUID.randomUUID(), 10L));

        System.out.println(MessageFormat.format("is Jdbc Repo -> {0}", voucherRepository instanceof JdbcVoucherRepository));
        System.out.println(MessageFormat.format("is Jdbc Repo -> {0}", voucherRepository.getClass().getCanonicalName()));

        var orderService = applicationContext.getBean(OrderService.class);
        var order = orderService.createOrder(customerId, new ArrayList<>() {{
            add(new OrderItem(UUID.randomUUID(), 100L, 1));
        }}, voucher.getVoucherid());

        Assert.isTrue(order.totalAmount() == 90, MessageFormat.format("totalAmount: {0} is not 90L", order.totalAmount()));
    }
}
