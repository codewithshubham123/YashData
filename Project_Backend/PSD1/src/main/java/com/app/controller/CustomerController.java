package com.app.controller;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.validation.Valid;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.pojos.Employee;
import com.app.pojos.Order;
import com.app.pojos.OrderStatus;
import com.app.pojos.User;
import com.app.service.IAdminService;
import com.app.service.IUserService;
import com.razorpay.RazorpayClient;

import dto.BookService;
import dto.GiveFeedback;
import dto.UpdatePassword;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins="http://localhost:3000")
@RestController
@RequestMapping("/api/psd/customer")
@Slf4j
public class CustomerController {

	Logger log = LoggerFactory.getLogger(CustomerController.class);

	
	@Autowired
	IUserService cust_service;
	
	@Autowired
	IAdminService adminService;
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getCustomerdetails(@PathVariable int id)
	{
		log.info("customer ID::"+id);
		User cust=cust_service.getCustomerById(id);
			return ResponseEntity.status(HttpStatus.OK).body(cust);
	}
	
	
	@PostMapping("/newaccount")
	public ResponseEntity<?> createNewCustomerAccount(@RequestBody @Valid User cust)
	{
		
		User customer=cust_service.insertNewCustomer(cust);
		log.info("Customer details :: "+customer);
		//c.f. implements future and completion stage
		//two methods in c.f. runAsync and supplyAsync
		//runAsync takes runnable object
		//runAsyn-->Returns a new CompletableFuture that is asynchronously completed by a task running in the ForkJoinPool.commonPool() after it runs the given action.
		
		return ResponseEntity.status(HttpStatus.OK).body(customer);
	}
	
	
	
	@PostMapping("/updatepassword")
	public ResponseEntity<?> updatePassword(@RequestBody UpdatePassword updatepassword)
	{
		
		User cust=cust_service.updatePassword(updatepassword);
		
		return ResponseEntity.status(HttpStatus.OK).body(cust);
	}
	
	@PostMapping("/bookservice")
	public ResponseEntity<?> bookService(@RequestBody BookService bookService)
	{
		
		Employee employee=adminService.assignEmployee(bookService);
		User cust=cust_service.getCustomerByEmail(bookService.getUserEmail());
		
		return ResponseEntity.status(HttpStatus.OK).body(employee);
			
	}
	
	@GetMapping("/completedorders/{id}")
	public ResponseEntity<?> getAllCompletedOrders(@PathVariable(name="id") int customerId)
	{
		return ResponseEntity.status(HttpStatus.OK).body(cust_service.getOrders(OrderStatus.COMPLETED,customerId));
	}
	
	@GetMapping("/uncompletedorders/{id}")
	public ResponseEntity<?> getAllPendingOrders(@PathVariable(name="id") int customerId)
	{
		return ResponseEntity.status(HttpStatus.OK).body(cust_service.getOrders(OrderStatus.PENDING,customerId));
	}
	
	@GetMapping("/orders/{id}")
	public ResponseEntity<?> getAllOrders(@PathVariable(name="id") int customerId)
	{
		return ResponseEntity.status(HttpStatus.OK).body(cust_service.getAllOrders(customerId));
	}
	
	@GetMapping("/feedbackorders/{id}")
	public ResponseEntity<?> getAllFeedbackNotGiven(@PathVariable(name="id") int customerId)
	{
		return ResponseEntity.status(HttpStatus.OK).body(cust_service.getFeedbackNotGivenOrders(OrderStatus.COMPLETED,false,customerId));
		
	}
	
	@GetMapping("/cancelservice/{id}")
	public ResponseEntity<?> cancelService(@PathVariable(name="id") int orderId)
	{
		
		Order order=cust_service.cancelService(orderId);
		
		return ResponseEntity.status(HttpStatus.OK).body("Request sucessfully cancelled");
	}
	
	
	@PostMapping("/feedback")
	public ResponseEntity<?> giveFeedback(@RequestBody GiveFeedback feedback)
	{
		return ResponseEntity.status(HttpStatus.OK).body(cust_service.giveFeedback(feedback));
	}
	
}
