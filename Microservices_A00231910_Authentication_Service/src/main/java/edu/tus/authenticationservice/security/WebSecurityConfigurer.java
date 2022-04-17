package edu.tus.authenticationservice.security;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@RestController
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter{
	
	@Autowired
	AuthenticationManagerBuilder auth;
	
	@Autowired
	InMemoryUserDetailsManager userDetailsManager;
		
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception{
		return super.authenticationManagerBean();
	}
	
//	@Override
//	@Bean
//	public UserDetailsService userDetailsServiceBean() throws Exception{
//		return super.userDetailsServiceBean();
//	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception{
//		auth.inMemoryAuthentication()
//		.withUser("Adam Carolan").password("{noop}pa55word")
//		.roles("SELLER", "ADMIN")
//		.and()
//		.withUser("Jane Doe").password("{noop}password")
//		.roles("SELLER")
//		.and().withUser("John Doe").password("{noop}password")
//		.roles("SELLER");
		
		// Uses 
		auth.userDetailsService(inMemoryUserDetailsManager());
				
	}
	
	@Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        final Properties users = new Properties();
        users.put("Adam Carolan","{noop}pa55word,ROLE_ADMIN"); //add whatever other user you need
        users.put("Jane Doe","{noop}password,ROLE_SELLER"); //add whatever other user you need
        users.put("John Doe","{noop}password,ROLE_SELLER"); //add whatever other user you need
        return new InMemoryUserDetailsManager(users);
    }

}
