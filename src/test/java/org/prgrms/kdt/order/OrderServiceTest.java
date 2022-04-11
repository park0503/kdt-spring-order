package org.prgrms.kdt.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.prgrms.kdt.voucher.FixedAmountVoucher;
import org.prgrms.kdt.voucher.MemoryVoucherRepository;
import org.prgrms.kdt.voucher.Voucher;
import org.prgrms.kdt.voucher.VoucherService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    //이런식으로 Stub 객체 만들어서 사용 가능
    class OrderRepositoryStub implements OrderRepository {
        @Override
        public Order insert(Order order) {
            return null;
        }
    }

    @Test
    @DisplayName("오더가 생성되어야 한다. (stub version)")
    void createOrder() {
        // Given
        var voucherRepository = new MemoryVoucherRepository();
        Voucher fixedAmountVoucher = new FixedAmountVoucher(UUID.randomUUID(), 100);
        voucherRepository.insert(fixedAmountVoucher);
        var sut = new OrderService(new VoucherService(voucherRepository), new OrderRepositoryStub());


        // When
        var order = sut.createOrder(UUID.randomUUID(), List.of(new OrderItem((UUID.randomUUID()), 200L, 1)), fixedAmountVoucher.getVoucherid()); // - 만들어진 order의 상태에 집중

        // Then
        assertThat(order.totalAmount(), is(100L));
        assertThat(order.getVoucher().isEmpty(), is(false));
        assertThat(order.getVoucher().get().getVoucherid(), is(fixedAmountVoucher.getVoucherid()));
        assertThat(order.getOrderStatus(), is(OrderStatus.ACCEPTED));
    }

    @Test
    @DisplayName("오더가 생성되어야 한다. (mock version)")
    void createOrderByMock() {
        // Given
        var voucherServiceMock = mock(VoucherService.class);
        var orderRepositoryMock = mock(OrderRepository.class);
        var fixedAmountVoucher = new FixedAmountVoucher(UUID.randomUUID(), 100);
        when(voucherServiceMock.getVoucher(fixedAmountVoucher.getVoucherid())).thenReturn(fixedAmountVoucher); //when으로 기술한 부분만 동작, 이런 상황을 우리가 만든거임
        var sut = new OrderService(voucherServiceMock, orderRepositoryMock);


        // When
        var order = sut.createOrder(
                UUID.randomUUID(),
                List.of(new OrderItem((UUID.randomUUID()), 200L, 1)), fixedAmountVoucher.getVoucherid()
        ); //voucherService의 Mock객체와 orderReository의 Mock객체에 대해서 어떠한 method가 정상적으로 호출되는지를 verify를 해줘야 함.

        // Then
        assertThat(order.totalAmount(), is(100L));
        assertThat(order.getVoucher().isEmpty(), is(false));//이 두 줄 처럼 order의 상태도 확인 간으하고
        verify(voucherServiceMock).getVoucher(fixedAmountVoucher.getVoucherid()); //내부적으로 중간에 있는 getVoucher가 호출이 되었는지를 검증
        verify(orderRepositoryMock).insert(order); //실제로 insert가 호출 되었는지를 검증
        verify(voucherServiceMock).useVoucher(fixedAmountVoucher); // 해당 useVoucher를 사용한 지를 검증

        var inOrder = inOrder(voucherServiceMock, orderRepositoryMock); //inOrder를 쓰면 단순 함수 호출 여부를 검증한 위와 다르게 순서까지 검증 가능, 인자로 여러 개를 줄 수 있음.
        inOrder.verify(voucherServiceMock).getVoucher(fixedAmountVoucher.getVoucherid());
        inOrder.verify(orderRepositoryMock).insert(order);
        inOrder.verify(voucherServiceMock).useVoucher(fixedAmountVoucher);
        //실제로 createOrder에서는 getVoucher, insert, useVoucher 순으로 실행되므로 테스트 통과
    }
}