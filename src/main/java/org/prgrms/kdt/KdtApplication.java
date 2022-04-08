package org.prgrms.kdt;

import org.prgrms.kdt.order.OrderItem;
import org.prgrms.kdt.order.OrderProperties;
import org.prgrms.kdt.order.OrderService;
import org.prgrms.kdt.voucher.FixedAmountVoucher;
import org.prgrms.kdt.voucher.JdbcVoucherRepository;
import org.prgrms.kdt.voucher.VoucherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.Assert;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.UUID;

@SpringBootApplication
@ComponentScan(basePackages = {"org.prgrms.kdt.voucher", "org.prgrms.kdt.order"})
public class KdtApplication {
	private static final Logger logger = LoggerFactory.getLogger(KdtApplication.class);
	public static void main(String[] args) {
		var spirngApplication = new SpringApplication(KdtApplication.class);
		//spirngApplication.setAdditionalProfiles("local");
		var applicationContext = spirngApplication.run(args);

		var customerId = UUID.randomUUID();
		var voucherRepository = applicationContext.getBean(VoucherRepository.class);
		var voucher = voucherRepository.insert(new FixedAmountVoucher(UUID.randomUUID(), 10L));

		System.out.println(MessageFormat.format("is Jdbc Repo -> {0}", voucherRepository instanceof JdbcVoucherRepository));
		System.out.println(MessageFormat.format("is Jdbc Repo -> {0}", voucherRepository.getClass().getCanonicalName()));

		var orderProperties = applicationContext.getBean(OrderProperties.class);
		logger.info("logger name => {}", logger.getName());
		logger.warn("version -> {}", orderProperties.getVersion());
		logger.info("moa -> {}", orderProperties.getMinimumOrderAmount());
		logger.error("sv -> {}", orderProperties.getSupportVendors());
		logger.info("d -> {}", orderProperties.getDescription());

		var orderService = applicationContext.getBean(OrderService.class);
		var order = orderService.createOrder(customerId, new ArrayList<>() {{
			add(new OrderItem(UUID.randomUUID(), 100L, 1));
		}}, voucher.getVoucherid());

		Assert.isTrue(order.totalAmount() == 90, MessageFormat.format("totalAmount: {0} is not 90L", order.totalAmount()));
	}

}
