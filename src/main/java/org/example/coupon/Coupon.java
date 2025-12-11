package org.example.coupon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.user.UserType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    private Long id;
    private Long menuId;
    private UserType userType;
    private Integer couponPrice;
}
