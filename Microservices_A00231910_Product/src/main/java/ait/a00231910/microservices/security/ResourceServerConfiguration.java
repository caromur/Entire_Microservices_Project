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
			.antMatchers(HttpMethod.GET, "/api/products").permitAll()
			.antMatchers(HttpMethod.GET, "/api/products/**").permitAll()
			.antMatchers(HttpMethod.GET, "/api/product/**").permitAll()
			.antMatchers(HttpMethod.POST, "/api/products").hasAnyRole("ADMIN", "SELLER")
			.antMatchers(HttpMethod.PUT, "/api/product/**").hasAnyRole("ADMIN", "SELLER")
			.antMatchers(HttpMethod.DELETE, "/api/product/**").hasAnyRole("ADMIN", "SELLER")
			.antMatchers(HttpMethod.GET, "/api/ping").permitAll()
			.antMatchers(HttpMethod.GET, "/v2/api-docs/**").permitAll()
			.antMatchers(HttpMethod.GET, "/v3/api-docs/**").permitAll()
			.antMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()
			.antMatchers(HttpMethod.GET, "/swagger-resources/**").permitAll()
			.anyRequest().authenticated();

	}

}
