package com.mypro.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class OrderDTO implements Serializable{

    private Long id;
    private String uuid;
    private Long customerId;
    private String title;
    private Long ticketNum;
    private int amount;
    private String status;

    public OrderDTO() {
    }

    public OrderDTO(Long id, String uuid, Long customerId, String title, Long ticketNum, int amount, String status) {
        this.id = id;
        this.uuid = uuid;
        this.customerId = customerId;
        this.title = title;
        this.ticketNum = ticketNum;
        this.amount = amount;
        this.status = status;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", customerId=" + customerId +
                ", title='" + title + '\'' +
                ", ticketNum=" + ticketNum +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                '}';
    }
}
