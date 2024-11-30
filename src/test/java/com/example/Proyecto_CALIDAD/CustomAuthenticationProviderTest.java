package com.example.Proyecto_CALIDAD;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.Proyecto_CALIDAD.Servicios.UsuarioService;

class CustomAuthenticationProviderTest {

    @InjectMocks
    private CustomAuthenticationProvider authenticationProvider;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
    }

    @Test
    void testRetrieveUser_UserNotFound() {
        String username = "nonexistentUser";
        when(userDetailsService.loadUserByUsername(username)).thenThrow(new UsernameNotFoundException("User not found"));

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, "password");

        assertThrows(UsernameNotFoundException.class, () -> {
            authenticationProvider.retrieveUser(username, auth);
        });
    }

    @Test
    void testRetrieveUser_AccountLocked() {
        String username = "lockedUser";
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(userDetails.isAccountNonLocked()).thenReturn(false);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, "password");

        assertThrows(LockedException.class, () -> {
            authenticationProvider.retrieveUser(username, auth);
        });
    }

    @Test
    void testAdditionalAuthenticationChecks_BadCredentials() {
        String username = "testUser";
        String password = "wrongPassword";
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, password);

        when(userDetails.getUsername()).thenReturn(username);
        when(userDetails.getPassword()).thenReturn("encodedPassword");
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> {
            authenticationProvider.additionalAuthenticationChecks(userDetails, auth);
        });

        verify(usuarioService, times(1)).loginFailed(username);
    }

    @Test
    void testAdditionalAuthenticationChecks_SuccessfulLogin() {
        String username = "testUser";
        String password = "correctPassword";
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, password);

        when(userDetails.getUsername()).thenReturn(username);
        when(userDetails.getPassword()).thenReturn("encodedPassword");
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);

        authenticationProvider.additionalAuthenticationChecks(userDetails, auth);

        verify(usuarioService, never()).loginFailed(username);
    }
}
