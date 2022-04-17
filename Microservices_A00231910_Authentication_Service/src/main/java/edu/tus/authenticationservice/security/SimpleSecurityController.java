package edu.tus.authenticationservice.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user/")
public class SimpleSecurityController {

	private final InMemoryUserDetailsManager inMemoryUserDetailsManager;

	@Autowired
	public SimpleSecurityController(InMemoryUserDetailsManager inMemoryUserDetailsManager) {
		this.inMemoryUserDetailsManager = inMemoryUserDetailsManager;
	}

	@RequestMapping("exists/{username}")
	public boolean userExists(@PathVariable("username") String username) {
		return inMemoryUserDetailsManager.userExists(username);
	}

	@RequestMapping("add/{username}/{password}")
	public String add(@PathVariable("username") String username, @PathVariable("password") String password) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
		//authorities.add("ROLE_SELLER");
		inMemoryUserDetailsManager.createUser(new User(username, password, authorities));
		return "added";
	}
}