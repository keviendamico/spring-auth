package com.kevien.damico.auth.controller;

import com.kevien.damico.auth.security.JwtTokenUtil;
import com.kevien.damico.auth.service.BlackListService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final BlackListService blackListService;
    private final UserDetailsService userDetailsService;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil,
                          BlackListService blackListService, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.blackListService = blackListService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtObject> login(@RequestBody UserLogin userLogin) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLogin.username(), userLogin.password()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(new JwtObject(
                jwtTokenUtil.generateToken(userDetails),
                jwtTokenUtil.generateRefreshToken(userDetails)
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtObject> refresh(@RequestBody RefreshRequest request) {
        String refreshToken = request.refreshToken();
        if (blackListService.isBlacklisted(refreshToken) || !jwtTokenUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = jwtTokenUtil.getUsernameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        blackListService.blackListRefreshToken(refreshToken);
        return ResponseEntity.ok(new JwtObject(
                jwtTokenUtil.generateToken(userDetails),
                jwtTokenUtil.generateRefreshToken(userDetails)
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody JwtObject jwtObject) {
        blackListService.blackListAccessToken(jwtObject.jwt());
        blackListService.blackListRefreshToken(jwtObject.refreshToken());
        return ResponseEntity.noContent().build();
    }

    public record JwtObject(String jwt, String refreshToken) {}

    public record UserLogin(String username, String password) {}

    public record RefreshRequest(String refreshToken) {}
}
