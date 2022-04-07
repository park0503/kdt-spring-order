package org.prgrms.kdt;

import org.prgrms.kdt.order.OrderItem;
import org.prgrms.kdt.order.OrderService;
import org.prgrms.kdt.voucher.FixedAmountVoucher;
import org.prgrms.kdt.voucher.JdbcVoucherRepository;
import org.prgrms.kdt.voucher.VoucherRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.Assert;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.UUID;

@SpringBootApplication
@ComponentScan(basePackages = {"org.prgrms.kdt.voucher", "org.prgrms.kdt.order", "org.prgrms.kdt.configuration"})
public class KdtApplication {

	public static void main(String[] args) {
		var spirngApplication = new SpringApplication(KdtApplication.class);
		//spirngApplication.setAdditionalProfiles("local");
		var applicationContext = spirngApplication.run(args);

		var customerId = UUID.randomUUID();
		var voucherRepository = applicationContext.getBean(VoucherRepository.class);
		var voucher = voucherRepository.insert(new FixedAmountVoucher(UUID.randomUUID(), 10L));

		System.out.println(MessageFormat.format("is Jdbc Repo -> {0}", voucherRepository instanceof JdbcVoucherRepository));
		System.out.println(MessageFormat.format("is Jdbc Repo -> {0}", voucherRepository.getClass().getCanonicalName()));

		var orderService = applicationContext.getBean(OrderService.class);
		var order = orderService.createOrder(customerId, new ArrayList<OrderItem>() {{
			add(new OrderItem(UUID.randomUUID(), 100L, 1));
		}}, voucher.getVoucherid());

		Assert.isTrue(order.totalAmount() == 90, MessageFormat.format("totalAmount: {0} is not 90L", order.totalAmount()));
	}

}
