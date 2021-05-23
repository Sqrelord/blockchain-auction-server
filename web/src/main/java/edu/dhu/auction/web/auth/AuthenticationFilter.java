package edu.dhu.auction.web.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (request.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE)) {
            ObjectMapper mapper = new ObjectMapper();
            UsernamePasswordAuthenticationToken authRequest;
            try (InputStream inputStream = request.getInputStream()) {
                JsonNode jsonNode = mapper.readTree(inputStream);
                authRequest = new UsernamePasswordAuthenticationToken(jsonNode.get(SPRING_SECURITY_FORM_USERNAME_KEY).asText(), jsonNode.get(SPRING_SECURITY_FORM_PASSWORD_KEY).asText());
            } catch (NullPointerException | IOException e) {
                authRequest = new UsernamePasswordAuthenticationToken("", "");
            }
            this.setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        } else {
            return super.attemptAuthentication(request, response);
        }
    }
}
