package com.reefe.mqths;

import com.reefe.mqths.service.OrderSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@SpringBootApplication
public class UseMyStarterApplication {
	@Autowired
	private OrderSerivce orderSerivce;

	public static void main(String[] args) {
		SpringApplication.run(UseMyStarterApplication.class, args);
	}
	@GetMapping("/test")
	public void testUseMqth(){
		orderSerivce.orderPay(1,new BigDecimal(100));
	}
}
