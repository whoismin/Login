package br.com.fatec.fretec.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// Rota de exemplo só acessível por quem tem ROLE_ADMIN (ver SecurityConfig:
// .requestMatchers("/fatec/login/v1/admin/**").hasRole("ADMIN")).
// O Spring Security já bloqueia (403) antes de chegar aqui quem não for admin,
// então só entrar neste método já prova que a role foi validada no servidor.
@RestController
@RequestMapping("/fatec/login/v1/admin")
public class AdminController {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/ping")
    public String ping(Authentication authentication) {
        return "Acesso liberado! Bem-vindo(a), " + authentication.getName() + ". Você é ADMIN.";
    }
}
