package com.example.java_mongo_fullstack_app.auth;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    private final CustomUserDetailsService customUserDetailsService;
    private final RoleBasedSuccessHandler roleBasedSuccessHandler;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService, RoleBasedSuccessHandler roleBasedSuccessHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.roleBasedSuccessHandler = roleBasedSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider());
        
        // Let Vaadin manage its own security rules first
        super.configure(http);

        // Customize the form login to use our success handler
        setLoginView(http, LoginView.class);
        
        http.formLogin(form -> form.successHandler(roleBasedSuccessHandler));
    }

    @Override
    protected void configure(WebSecurity web) throws Exception {
        super.configure(web);
        // Completely bypass Spring Security and Vaadin for API load testing
        web.ignoring().requestMatchers("/employees/**");
    }
}
