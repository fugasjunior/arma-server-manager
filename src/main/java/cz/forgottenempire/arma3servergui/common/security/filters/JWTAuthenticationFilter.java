package cz.forgottenempire.arma3servergui.common.security.filters;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static cz.forgottenempire.arma3servergui.common.security.SecurityConstants.EXPIRATION_TIME;
import static cz.forgottenempire.arma3servergui.common.security.SecurityConstants.HEADER_STRING;
import static cz.forgottenempire.arma3servergui.common.security.SecurityConstants.TOKEN_PREFIX;

import com.auth0.jwt.JWT;
import cz.forgottenempire.arma3servergui.common.security.SecurityConstants;
import java.io.IOException;
import java.util.Date;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        super();
        this.setAuthenticationManager(authenticationManager);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain,
            Authentication auth
    ) throws IOException, ServletException {
        String token = JWT.create()
                .withSubject(((User) auth.getPrincipal()).getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(HMAC512(SecurityConstants.getSecret().getBytes()));
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write("{\"" + HEADER_STRING + "\":\"" + TOKEN_PREFIX + token + "\"}");
    }
}