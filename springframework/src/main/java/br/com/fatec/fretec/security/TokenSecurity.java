package br.com.fatec.fretec.security;

import br.com.fatec.fretec.entity.Login;
import br.com.fatec.fretec.entity.Token;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class TokenSecurity {
    private final JwtSecurity jwtSecurity;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    public TokenSecurity(
            JwtSecurity jwtSecurity,
            UserDetailsService userDetailsService,
            AuthenticationManager authenticationManager) {
        this.jwtSecurity = jwtSecurity;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
    }

    // Valida usuário/senha e devolve o UserDetails autenticado
    public UserDetails autenticar(Login login) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                login.username(), login.password());
        authenticationManager.authenticate(authToken);
        return userDetailsService.loadUserByUsername(login.username());
    }

    // Gera o token JWT a partir de um UserDetails já autenticado
    public Token gerarToken(UserDetails userDetails) {
        return new Token(jwtSecurity.generateToken(userDetails));
    }
}