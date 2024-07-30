package com.scenic.rownezcoreservice.model;

import com.scenic.rownezcoreservice.order.state.OrderEvent;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderStatusDTO {
    private UUID orderId;
    private OrderEvent status;
}
