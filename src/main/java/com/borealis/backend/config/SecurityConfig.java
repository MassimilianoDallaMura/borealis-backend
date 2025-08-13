package com.borealis.backend.config;

import com.borealis.backend.security.jwt.AuthEntryPointJwt;
import com.borealis.backend.security.jwt.AuthTokenFilter;
import com.borealis.backend.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity // Abilita la sicurezza a livello di metodo con @PreAuthorize, @PostAuthorize, ecc.
public class SecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    // Definisce il bean per il filtro JWT
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    // Definisce il provider di autenticazione che userà il nostro UserDetailsService e PasswordEncoder
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Il nostro servizio per caricare i dettagli utente
        authProvider.setPasswordEncoder(passwordEncoder());     // Il nostro encoder per le password
        return authProvider;
    }

    // Bean per il gestore di autenticazione
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Bean per l'encoder delle password (BCrypt è consigliato)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configurazione della catena di filtri di sicurezza HTTP
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Disabilita CSRF per le API stateless (JWT)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)) // Gestisce le eccezioni di autenticazione (es. 401 Unauthorized)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Imposta la politica delle sessioni a stateless (non vengono create sessioni lato server)
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/**").permitAll() // Permetti l'accesso pubblico agli endpoint di autenticazione (es. /api/auth/login)
                                .requestMatchers("/api/test/**").permitAll() // Esempio: endpoint di test pubblici (se presenti)
                                // Proteggi gli endpoint delle risorse, richiedendo autenticazione
                                .requestMatchers("/api/products/**").authenticated()
                                .requestMatchers("/api/users/**").authenticated()
                                .requestMatchers("/api/categories/**").authenticated()
                                .anyRequest().authenticated() // Tutte le altre richieste richiedono autenticazione
                );

        // Aggiunge il nostro provider di autenticazione
        http.authenticationProvider(authenticationProvider());

        // Aggiunge il filtro JWT personalizzato prima del filtro di autenticazione standard di Spring Security
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        // Configurazione CORS globale (se non usi @CrossOrigin sui singoli controller)
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    // Bean per la configurazione CORS globale
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200", "https://bvm.kaninchenhaus.org")); // Specifica l'origine del tuo frontend Angular
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Metodi HTTP consentiti
        configuration.setAllowedHeaders(List.of("*")); // Consente tutti gli header nelle richieste
        configuration.setAllowCredentials(true); // Consente l'invio di credenziali (es. header Authorization)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Applica questa configurazione CORS a tutti i path
        return source;
    }
}