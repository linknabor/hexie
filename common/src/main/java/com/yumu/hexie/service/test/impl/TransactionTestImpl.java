package com.yumu.hexie.service.test.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yumu.hexie.model.user.Address;
import com.yumu.hexie.model.user.AddressRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.test.TransactionTest;

/**
 * Unit test for simple App.
 * 
 * @param <T>
 * @param <T>
 */
@Service
public class TransactionTestImpl implements TransactionTest {

	@Autowired
	ApplicationContext context;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	AddressRepository addressRepository;
	
	@Autowired
	TransactionTest transactionTest;
	
	public void test() {
		testAdd();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void testAdd() {
		
//		User user = testAddUser();
		User user = new User();
		user.setCity("shanghia");
		user.setName("test");
		testAddAddess(user);
		
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public User testAddUser() {
		
		User user = new User();
		user.setAge(20);
		user.setCity("shanghai");
		user.setName("test");
		user = userRepository.save(user);
		return user;
	}

	public void testAddAddess(User user) {
		
		for(int i = 0; i<2; i++) {
			
			Address addr = new Address();
			addr.setCity(user.getCity());
			addr.setCounty("普陀");
			addr.setUserId(user.getId());
			addr.setUserName(user.getName());
			addr.setDetailAddress("测试地址"+i);
			
			addressRepository.save(addr);
			
			if (i==1) {
				System.out.println(i/0);
			}
			
		}
		
		
	}




}
