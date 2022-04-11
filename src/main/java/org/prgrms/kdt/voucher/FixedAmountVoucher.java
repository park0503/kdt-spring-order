package org.prgrms.kdt.voucher;

import java.util.UUID;

public class FixedAmountVoucher implements Voucher {
    private static final long MAX_VOUCHER_AMOUNT = 1000000;
    private final UUID voucherId;
    private final long amount;

    public FixedAmountVoucher(UUID voucherId, long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount should be positive");
        } else if (amount == 0) {
            throw new IllegalArgumentException("Amount shouldn't be zero");
        } else if (amount > MAX_VOUCHER_AMOUNT) {
            throw new IllegalArgumentException("Amount should be less than 1000001");
        }
        this.voucherId = voucherId;
        this.amount = amount;
    }

    @Override
    public UUID getVoucherid() {
        return voucherId;
    }

    public long discount(long beforeDiscount) {
        var discountedAmount = beforeDiscount - amount;
        return (discountedAmount < 0) ? 0 : discountedAmount;
    }
}
