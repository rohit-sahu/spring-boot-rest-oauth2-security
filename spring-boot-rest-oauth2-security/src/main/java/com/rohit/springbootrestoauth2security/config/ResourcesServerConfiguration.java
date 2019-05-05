package com.rohit.springbootrestoauth2security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableResourceServer
public class ResourcesServerConfiguration extends ResourceServerConfigurerAdapter {

	public static final String RESOURCE_ID = "my-api";
	private static final String SECURED_READ_SCOPE = "#oauth2.hasScope('read')";
    private static final String SECURED_WRITE_SCOPE = "#oauth2.hasScope('write')";
    private static final String SECURED_PATTERN = "/api/**";
    private static final String ADMIN_PATTERN = "/api/admin/**";

	@Autowired
	@Qualifier("jdbcTokenStore")
	private TokenStore tokenStore;

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.resourceId(RESOURCE_ID).tokenStore(tokenStore).tokenServices(tokenServices());
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
		http.csrf().disable().cors().and().requestMatchers().antMatchers(SECURED_PATTERN).and().authorizeRequests()
				.antMatchers(SECURED_PATTERN).fullyAuthenticated().and().requestMatchers().antMatchers(ADMIN_PATTERN)
				.and().authorizeRequests().antMatchers(ADMIN_PATTERN).access("hasRole('ADMIN')")
				.and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
		
		http.requestMatchers().antMatchers("/**").and().authorizeRequests().antMatchers("/**").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/**").access(SECURED_READ_SCOPE)
		.antMatchers(HttpMethod.POST, "/**").access(SECURED_WRITE_SCOPE).antMatchers(HttpMethod.PATCH, "/**")
		.access(SECURED_WRITE_SCOPE).antMatchers(HttpMethod.PUT, "/**").access(SECURED_WRITE_SCOPE)
		.antMatchers(HttpMethod.DELETE, "/**").access(SECURED_WRITE_SCOPE).and().headers()
		.addHeaderWriter((request, response) -> {
			response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
			response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
			response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, PUT, OPTIONS, DELETE");
			response.setHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
			response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
					"X-Requested-With, Content-Type, Authorization, Origin, Accept, Access-Control-Request-Method, Access-Control-Request-Headers");
			response.setHeader(HttpHeaders.ACCEPT_LANGUAGE, request.getHeader(HttpHeaders.ACCEPT_LANGUAGE));
			response.setHeader(HttpHeaders.USER_AGENT, request.getHeader(HttpHeaders.USER_AGENT));
			//response.setHeader(HttpHeaders.WWW_AUTHENTICATE, request.getHeader(HttpHeaders.WWW_AUTHENTICATE));
			response.setHeader(HttpHeaders.AUTHORIZATION, request.getHeader(HttpHeaders.AUTHORIZATION));
			if (request.getMethod().equals("OPTIONS")) {
				response.setHeader("Access-Control-Allow-Methods",
						request.getHeader("Access-Control-Request-Method"));
				response.setHeader("Access-Control-Allow-Headers",
						request.getHeader("Access-Control-Request-Headers"));
			}
		});
	}
	
	@Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore);
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }

	/*@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/oauth/token", "/oauth/authorize**", "/publishes").permitAll();
		// .anyRequest (). authenticated ();
		http.requestMatchers().antMatchers("/private") // Deny access to "/ private"
				.and().authorizeRequests().antMatchers("/private").access("hasRole('USER')").and().requestMatchers()
				.antMatchers("/admin") // Deny access to "/ admin"
				.and().authorizeRequests().antMatchers("/admin").access("hasRole('ADMIN')");
	}*/
}
