package com.example.Proyecto_CALIDAD;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.Proyecto_CALIDAD.Servicios.UsuarioService;

public class CustomAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    @Autowired
    private UsuarioService usuarioService;
    private UserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
            UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (!passwordEncoder.matches(authentication.getCredentials().toString(), userDetails.getPassword())) {
            logger.info("Intento de inicio de sesión fallido para el usuario: {}", userDetails.getUsername());
            usuarioService.loginFailed(userDetails.getUsername());
            throw new BadCredentialsException("La contraseña que ingresaste es incorrecta. Por favor, inténtalo de nuevo.");
        }
    }
    
    @Override
    public UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        UserDetails loadedUser = userDetailsService.loadUserByUsername(username);
        if (loadedUser == null) {
            throw new UsernameNotFoundException("No pudimos encontrar un usuario con ese nombre. Por favor, verifica tu entrada.");
        }
    
        if (!loadedUser.isAccountNonLocked()) {
            logger.info("La cuenta del usuario {} está bloqueada", username);
            throw new LockedException("Tu cuenta ha sido bloqueada. Por favor, contacta al soporte para más información.");
        }
    
        return loadedUser;
    }
    
}
