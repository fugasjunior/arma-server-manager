package cz.forgottenempire.servermanager.security;

import com.auth0.jwt.JWT;
import com.google.gson.JsonObject;
import cz.forgottenempire.servermanager.security.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static cz.forgottenempire.servermanager.security.SecurityConstants.*;

class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final UserRepository userRepository;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super();
        this.setAuthenticationManager(authenticationManager);
        this.userRepository = userRepository;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain,
            Authentication auth
    ) throws IOException {
        User principal = (User) auth.getPrincipal();
        String username = principal.getUsername();

        String[] permCodes = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new);

        Long userId = userRepository.findByUsername(username)
                .map(cz.forgottenempire.servermanager.security.user.User::getId)
                .orElse(null);

        String token = JWT.create()
                .withSubject(username)
                .withArrayClaim("perms", permCodes)
                .withClaim("userId", userId)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(HMAC512(SecurityConstants.getSecret().getBytes()));

        res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        JsonObject response = new JsonObject();
        response.addProperty("token", TOKEN_PREFIX + token);
        response.addProperty("expiresIn", EXPIRATION_TIME);
        res.getWriter().write(response.toString());
    }
}