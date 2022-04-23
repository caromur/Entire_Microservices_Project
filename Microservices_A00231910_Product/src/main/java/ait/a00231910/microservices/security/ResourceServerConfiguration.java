package ait.a00231910.microservices.security;


import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter{
	
	@Override
	public void configure(HttpSecurity http) throws Exception
	{
		http.authorizeRequests()
			.antMatchers(HttpMethod.GET, "/products").permitAll()
			.antMatchers(HttpMethod.GET, "/products/**").permitAll()
			.antMatchers(HttpMethod.POST, "/products").hasAnyRole("ADMIN", "SELLER")
			.antMatchers(HttpMethod.PUT, "/product/**").hasAnyRole("ADMIN", "SELLER")
			.antMatchers(HttpMethod.DELETE, "/product/**").hasAnyRole("ADMIN", "SELLER")
			.antMatchers(HttpMethod.GET, "/").permitAll()
			.anyRequest().authenticated();

	}

}
