package br.com.fatec.fretec.controller.response;

import java.util.List;

public record AuthClaimsResponse(
        String id,
        String username,
        List<String> roles
) {
}
