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
			.antMatchers(HttpMethod.DELETE, "/api/seller/*/product/**").hasAnyRole("ADMIN", "SELLER")
			.antMatchers(HttpMethod.DELETE, "/api/seller/username/**").hasAnyRole("ADMIN", "SELLER")
			.antMatchers(HttpMethod.DELETE, "/api/seller/**").hasRole("ADMIN")
			.antMatchers(HttpMethod.GET, "/api/sellers/**").hasRole("ADMIN")
			.antMatchers(HttpMethod.GET, "/api/seller/username/**").hasAnyRole("ADMIN", "SELLER")
			.antMatchers(HttpMethod.GET, "/api/seller/**").hasRole("ADMIN")
			.antMatchers(HttpMethod.PUT, "/api/seller/username/**").hasAnyRole("ADMIN", "SELLER")
			.antMatchers(HttpMethod.PUT, "/api/seller/**").hasRole("ADMIN")
			.antMatchers(HttpMethod.PUT, "/api/seller-delayed/**").hasRole("ADMIN")
			.antMatchers(HttpMethod.POST, "/api/sellers/**").permitAll()
			.antMatchers(HttpMethod.GET, "/api/ping").permitAll()
			//.antMatchers(HttpMethod.GET, "/").permitAll()
			.antMatchers(HttpMethod.GET, "/v2/api-docs/**").permitAll()
			.antMatchers(HttpMethod.GET, "/v3/api-docs/**").permitAll()
			.antMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()
			.antMatchers(HttpMethod.GET, "/swagger-resources/**").permitAll()
			.anyRequest().authenticated();

	}

}
