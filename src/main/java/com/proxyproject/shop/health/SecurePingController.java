package com.proxyproject.shop.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/secure")
public class SecurePingController {

  @GetMapping("/ping")
  public Map<String, Object> ping() {
    return Map.of("ok", true, "message", "secure pong");
  }
}

