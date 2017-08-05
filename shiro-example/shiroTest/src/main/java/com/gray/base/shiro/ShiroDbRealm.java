package com.gray.base.shiro;  
   
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.gray.user.entity.User;
import com.gray.user.service.impl.UserServiceImpl;

public class ShiroDbRealm extends AuthorizingRealm{
	@Autowired
	private UserServiceImpl userService;
	public static final String SESSION_USER_KEY = "gray";
	
	/**
	*��Ȩ��ѯ�ص����������м�Ȩ�����������û�����Ȩ��Ϣʱ���ã�������Ӧ�ó����о����û��ķ��ʿ��Ʒ���
	**/
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection arg0){
		User user = (User) SecurityUtils.getSubject().getSession().getAttribute(ShiroDbRealm.SESSION_USER_KEY);
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.addRole(user.getRole().trim());
		return info;
	}
	
	/**
	*��֤�ص���������¼��Ϣ���û���֤��Ϣ��֤
	**/
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
		//��tokenת����User����
		User userLogin = tokenToUser((UsernamePasswordToken) authcToken);
		//��֤�û��Ƿ���Ե�¼
		User ui = userService.doUserLogin(userLogin);
		if(ui == null){
			return null;
		}
		Session session = SecurityUtils.getSubject().getSession();
		session.setAttribute(ShiroDbRealm.SESSION_USER_KEY,ui);
		//��ǰRealm��name
		String realmName = this.getName();
		//��½����Ҫ��Ϣ��������һ��ʵ����Ķ��󣬵���ʵ����Ķ���һ���Ǹ���token��username��ѯ�õ��ġ�
		Object principal = authcToken.getPrincipal();
		return new SimpleAuthenticationInfo(principal,userLogin.getPassword(),realmName);
	}
	
	private User tokenToUser(UsernamePasswordToken authcToken){
		User user = new User();
		user.setUsername(authcToken.getUsername());
		user.setPassword(String.valueOf(authcToken.getPassword()));
		return user;
	}
	
	public UserServiceImpl getUserService(){
		return userService;
	}
	
	public void setUserService(UserServiceImpl userService){
		this.userService = userService;
	}
}