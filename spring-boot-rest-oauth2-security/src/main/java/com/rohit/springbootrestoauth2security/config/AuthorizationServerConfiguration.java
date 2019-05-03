package com.rohit.springbootrestoauth2security.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

	@Autowired
	@Qualifier("authenticationManagerBean")
	private AuthenticationManager authenticationManager;

	@Value("classpath:schema.sql")
	private Resource schemaScript;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	@Qualifier("bcryptEncoder")
	private PasswordEncoder encoder;

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()").passwordEncoder(encoder);
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.jdbc(dataSource).withClient("client")
				.secret("$2a$12$qsF6u44ukCaGR9q8E8j.0.SIMcdCATjpWFZQfcTq5urbac8WCCjge")// secret
				.authorizedGrantTypes("implicit", "password", "authorization_code", "refresh_token")
				.scopes("read", "write", "trust").autoApprove(true).accessTokenValiditySeconds(120)
				.refreshTokenValiditySeconds(600).resourceIds("my-api")
				.authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT", "USER").redirectUris();
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.tokenStore(tokenStore()).authenticationManager(authenticationManager)
				.userDetailsService(userDetailsService).approvalStore(approvalStore())
				.authorizationCodeServices(authorizationCodeServices());
	}

	@Bean("jdbcTokenStore")
	public TokenStore tokenStore() {
		return new JdbcTokenStore(dataSource);
	}
	
	@Bean
    public ApprovalStore approvalStore() {
        return new JdbcApprovalStore(dataSource);
    }
	
	@Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new JdbcAuthorizationCodeServices(dataSource);
    }

	@Bean
	public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
		DataSourceInitializer initializer = new DataSourceInitializer();
		initializer.setDataSource(dataSource);
		initializer.setDatabasePopulator(databasePopulator());
		return initializer;
	}

	private DatabasePopulator databasePopulator() {
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScript(schemaScript);
		return populator;
	}
	
	/*@Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }*/
}
