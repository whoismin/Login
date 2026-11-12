package br.com.fatec.fretec.controller;

import br.com.fatec.fretec.controller.adapter.UserControllerAdapter;
import br.com.fatec.fretec.controller.request.UserRequest;
import br.com.fatec.fretec.controller.response.AuthClaimsResponse;
import br.com.fatec.fretec.controller.response.UserResponse;
import br.com.fatec.fretec.entity.Token;
import br.com.fatec.fretec.entity.User;
import br.com.fatec.fretec.repository.UserRepository;
import br.com.fatec.fretec.security.JwtSecurity;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fatec/login")
public class UserController {
    private final UserRepository repository;
    private final UserDetailsService userDetailsService;
    private final JwtSecurity jwtSecurity;

    @org.springframework.beans.factory.annotation.Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    public UserController(
            UserRepository repository,
            UserDetailsService userDetailsService,
            JwtSecurity jwtSecurity) {
        this.repository = repository;
        this.userDetailsService = userDetailsService;
        this.jwtSecurity = jwtSecurity;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/v1/user/save")
    public UserResponse save(@RequestBody UserRequest request) {
        User save = repository.save(UserControllerAdapter.cast(request));
        return new UserResponse(
                save.id(),
                save.username(),
                save.email(),
                save.cep(),
                save.roles());
    }

    // Cria o usuário e já efetua o login: salva, gera o JWT, seta o cookie
    // HttpOnly e devolve os claims — mesmo formato de resposta do /v1/auth
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/v1/user/create")
    public AuthClaimsResponse create(@RequestBody UserRequest request, HttpServletResponse response) {
        User saved = repository.save(UserControllerAdapter.cast(request));

        UserDetails userDetails = userDetailsService.loadUserByUsername(saved.username());
        Token token = new Token(jwtSecurity.generateToken(userDetails));

        ResponseCookie cookie = ResponseCookie.from(AuthController.ACCESS_TOKEN_COOKIE, token.value())
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
                .path("/")
                .maxAge(jwtSecurity.getExpirationSeconds())
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return new AuthClaimsResponse(
                saved.id(),
                saved.username(),
                saved.roles().stream().map(Enum::name).toList());
    }
}