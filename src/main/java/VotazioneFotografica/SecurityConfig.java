package VotazioneFotografica;

import jakarta.servlet.SessionCookieConfig;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.ForwardedHeaderFilter;

@Configuration
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/getclassifica","/admin/getngiudici").permitAll()
                        .requestMatchers("/admin/**","/visualizzaFoto").hasRole("ADMIN")
                        .requestMatchers("/giudice/**").hasAnyRole("GIUDICE", "ADMIN")
                        .requestMatchers("/squadra/**","/SalvaFoto").hasAnyRole("SQUADRA", "ADMIN")
                        .requestMatchers("/", "/login", "/css/**", "/photo/**","/uploads/**","/maintenance","/IfYouKnowThisURLShootYourself").permitAll()
                        .requestMatchers("/actuator/**","/admin/getclassifica","/admin/getngiudici").permitAll() // permetti accesso agli endpoint Prometheus
                        .anyRequest().authenticated() // tutte le altre pagine richiedono login
                )
                .formLogin(form -> form
                        .loginPage("/login")     // tua pagina custom
                        .loginProcessingUrl("/login") // gestito da Spring Security, NON devi scriverlo tu
                        .defaultSuccessUrl("/home", true) // redirect dopo login
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                        .permitAll()
                ).sessionManagement(session -> session
                        .maximumSessions(-1) // Permette sessioni illimitate
                        .sessionRegistry(sessionRegistry()) // Collega il registro!
                );

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
        FilterRegistrationBean<ForwardedHeaderFilter> filter = new FilterRegistrationBean<>();
        filter.setFilter(new ForwardedHeaderFilter());
        filter.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filter;
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
}
