package org.prgrms.kdt.voucher;

import java.util.UUID;

public interface Voucher {
    UUID getVoucherid();

    long discount(long beforeDiscount);
}
