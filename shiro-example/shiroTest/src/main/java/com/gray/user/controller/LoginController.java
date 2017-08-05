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
			model.addAttribute("failMsg","用户不存在或密码错误!");
			return "/jsp/fail";
		}else{
			model.addAttribute("successMsg","登陆成功！");
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
		if(isRelogin(user))return "SUCC";//如果已经登录，无需重新登录
		return shiroLogin(user);//调用shiro的登陆验证
	}
	
	private String shiroLogin(User user){
		//组装token，包括客户公司名称】简称、客户编号、用户名称、密码
		UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(),user.getPassword().toCharArray(),null);
		token.setRememberMe(true);
		
		//shiro登录验证
		try{
			SecurityUtils.getSubject().login(token);
		}catch(UnknownAccountException ex){
			return "用户不存在或者密码错误!";
		}catch(IncorrectCredentialsException ex){
			return "用户不存在或者密码错误!";
		}catch(AuthenticationException ex){
			return ex.getMessage();
		}catch(Exception ex){
			ex.printStackTrace();
			return "内部错误，请重试!";
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