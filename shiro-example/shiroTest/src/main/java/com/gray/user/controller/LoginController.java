package com.gray.user.controller;
 
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;  
import org.apache.shiro.authc.AuthenticationException;  
import org.apache.shiro.authc.IncorrectCredentialsException;  
import org.apache.shiro.authc.UnknownAccountException;  
import org.apache.shiro.authc.UsernamePasswordToken;  
import org.apache.shiro.subject.Subject;

import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.stereotype.Controller;  
import org.springframework.ui.Model;  
import org.springframework.web.bind.annotation.RequestMapping;

import com.gray.user.entity.User;  
import com.gray.user.service.impl.UserServiceImpl;

@Controller
@RequestMapping("/test")
public class LoginController{
	@Autowired
	private UserServiceImpl userService;
	
	@RequestMapping("/dologin.do")
	public String dologin(User user,Model model){
		String info = loginUser(user);
		if(!"SUCC".equals(info)){
			model.addAttribute("failMsg","�û������ڻ��������!");
			return "/jsp/fail";
		}else{
			model.addAttribute("successMsg","��½�ɹ���");
			model.addAttribute("name",user.getUsername());
			return "/jsp/success";
		}
	}
	
	@RequestMapping("/logout.do")
	public void logout(HttpServletRequest request,HttpServletResponse response) throws IOException{
		Subject subject = SecurityUtils.getSubject();
		if(subject != null){
			try{
				subject.logout();
			}catch(Exception ex){
				
			}
		}
		response.sendRedirect("/index.jsp");
	}
	
	private String loginUser(User user){
		if(isRelogin(user))return "SUCC";//����Ѿ���¼���������µ�¼
		return shiroLogin(user);//����shiro�ĵ�½��֤
	}
	
	private String shiroLogin(User user){
		//��װtoken�������ͻ���˾���ơ���ơ��ͻ���š��û����ơ�����
		UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(),user.getPassword().toCharArray(),null);
		token.setRememberMe(true);
		
		//shiro��¼��֤
		try{
			SecurityUtils.getSubject().login(token);
		}catch(UnknownAccountException ex){
			return "�û������ڻ����������!";
		}catch(IncorrectCredentialsException ex){
			return "�û������ڻ����������!";
		}catch(AuthenticationException ex){
			return ex.getMessage();
		}catch(Exception ex){
			ex.printStackTrace();
			return "�ڲ�����������!";
		}
		return "SUCC";
	}
	
	private boolean isRelogin(User user){
		Subject us = SecurityUtils.getSubject();
		if(us.isAuthenticated()){
			return true;
		}
		return false;
	}
}