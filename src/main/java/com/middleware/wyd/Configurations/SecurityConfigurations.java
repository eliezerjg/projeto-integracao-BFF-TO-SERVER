package com.middleware.wyd.Configurations;


import com.middleware.wyd.Services.CloudflareIPRangeFetcherServiceSingleton;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import java.util.List;

@EnableWebSecurity
@Configuration
public class SecurityConfigurations {

    private final List<String> allowedIps = CloudflareIPRangeFetcherServiceSingleton.getInstance().getIPRanges();

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http.authorizeRequests();
        for (String ipRange : allowedIps) {
            IpAddressMatcher ipAddressMatcher = new IpAddressMatcher(ipRange);
            registry.requestMatchers(ipAddressMatcher).permitAll();
        }
        registry.anyRequest().denyAll();
        return registry
                .and()
                .csrf()
                .disable()
                .build();
    }

}
