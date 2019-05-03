package com.rohit.springbootrestoauth2security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableResourceServer
public class ResourcesServerConfiguration extends ResourceServerConfigurerAdapter {

	public static final String RESOURCE_ID = "my-api";

	@Autowired
	@Qualifier("jdbcTokenStore")
	private TokenStore tokenStore;

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.resourceId(RESOURCE_ID).tokenStore(tokenStore);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/oauth/token", "/oauth/authorize**", "/publishes").permitAll();
		// .anyRequest (). authenticated ();
		http.requestMatchers().antMatchers("/private") // Deny access to "/ private"
				.and().authorizeRequests().antMatchers("/private").access("hasRole('USER')").and().requestMatchers()
				.antMatchers("/admin") // Deny access to "/ admin"
				.and().authorizeRequests().antMatchers("/admin").access("hasRole('ADMIN')");

		http.authorizeRequests().antMatchers(HttpMethod.GET, "/**").access("#oauth2.hasScope('read')")
				.antMatchers(HttpMethod.POST, "/**").access("#oauth2.hasScope('write')")
				.antMatchers(HttpMethod.PATCH, "/**").access("#oauth2.hasScope('write')")
				.antMatchers(HttpMethod.PUT, "/**").access("#oauth2.hasScope('write')")
				.antMatchers(HttpMethod.DELETE, "/**").access("#oauth2.hasScope('write')").and()

				.headers().addHeaderWriter((request, response) -> {
					response.addHeader("Access-Control-Allow-Origin", "*");
					
					response.setHeader("Access-Control-Allow-Credentials", "true");
					response.setHeader("Access-Control-Allow-Origin", "*");
					response.setHeader("Access-Control-Allow-Credentials", "true");
					response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
					response.setHeader("Access-Control-Max-Age", "3600");
					response.setHeader("Access-Control-Allow-Headers",
							"X-Requested-With, Content-Type, Authorization, Origin, Accept, Access-Control-Request-Method, Access-Control-Request-Headers");
					
					if (request.getMethod().equals("OPTIONS")) {
						response.setHeader("Access-Control-Allow-Methods",
								request.getHeader("Access-Control-Request-Method"));
						response.setHeader("Access-Control-Allow-Headers",
								request.getHeader("Access-Control-Request-Headers"));
					}
				});
		http.cors();
	}
}
