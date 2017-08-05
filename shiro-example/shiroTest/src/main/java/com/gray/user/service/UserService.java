package com.gray.user.service;

import org.springframework.stereotype.Service;

import com.gray.user.entity.User;

@Service
public interface UserService{
	public User doUserLogin(User userLogin);
}