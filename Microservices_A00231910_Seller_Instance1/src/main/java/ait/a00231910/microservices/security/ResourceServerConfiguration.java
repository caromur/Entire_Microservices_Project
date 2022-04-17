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
			.antMatchers(HttpMethod.DELETE, "/seller/username/**").hasAnyRole("ADMIN", "SELLER")
			.antMatchers(HttpMethod.DELETE, "/seller/**").hasRole("ADMIN")
			.antMatchers(HttpMethod.GET, "/sellers/**").hasRole("ADMIN")
			.antMatchers(HttpMethod.GET, "/seller/username/**").hasAnyRole("ADMIN", "SELLER")
			.antMatchers(HttpMethod.GET, "/seller/**").hasRole("ADMIN")
			.antMatchers(HttpMethod.PUT, "/seller/username/**").hasAnyRole("ADMIN", "SELLER")
			.antMatchers(HttpMethod.PUT, "/seller/**").hasRole("ADMIN")
			.antMatchers(HttpMethod.POST, "/sellers/**").permitAll()
			.antMatchers(HttpMethod.GET, "/").permitAll()
			.anyRequest().authenticated();

	}

}
