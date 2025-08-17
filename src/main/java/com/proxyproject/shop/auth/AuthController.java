package com.proxyproject.shop.auth;

import com.proxyproject.shop.security.JwtService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.authentication.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private final AuthenticationManager authManager;
  private final JwtService jwt;

  public AuthController(AuthenticationManager authManager, JwtService jwt) {
    this.authManager = authManager; this.jwt = jwt;
  }

  @PostMapping("/login")
  public Map<String,Object> login(@RequestBody LoginRequest req) {
    var auth = authManager.authenticate(
      new UsernamePasswordAuthenticationToken(req.username(), req.password())
    );
    List<String> roles = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
    return Map.of("accessToken", jwt.generate(auth.getName(), roles), "tokenType", "Bearer");
  }

  public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
}
