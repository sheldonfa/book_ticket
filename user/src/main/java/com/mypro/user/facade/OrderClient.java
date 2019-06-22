package com.mypro.user.facade;

import com.mypro.service.IOrderService;
import com.mypro.service.dto.OrderDTO;
import com.mypro.user.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(value = "order",path = "/api/order")
public interface OrderClient extends IOrderService {

    @GetMapping("/list")
    List<OrderDTO> list();
}
