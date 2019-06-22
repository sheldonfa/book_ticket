package com.mypro.order.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.ZonedDateTime;


/**
 * POJO:Order
 *
 * @author fangxin
 * @date 2019-4-10
 */
@Entity(name = "customer_order")
@Setter
@Getter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uuid;
    private Long customerId;
    private String title;
    private String detail;
    private int amount;
    private Long ticketNum;
    private String status;
    private String reason;
    private ZonedDateTime createDate;
}
