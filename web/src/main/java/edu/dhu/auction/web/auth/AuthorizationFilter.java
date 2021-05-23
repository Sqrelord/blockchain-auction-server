package edu.dhu.auction.web.auth;

import edu.dhu.auction.web.bean.Account;
import edu.dhu.auction.web.util.JwtUtils;
import edu.dhu.auction.web.util.ResultEntity;
import edu.dhu.auction.web.util.ResultStatus;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class AuthorizationFilter extends OncePerRequestFilter {

    private static final Logger log = LogManager.getLogger(AuthorizationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response,
                                    @NotNull FilterChain chain) throws IOException, ServletException {
        try {
            String token = request.getHeader(JwtUtils.TOKEN_HEADER);
            String url = request.getRequestURI();
            String method = request.getMethod();
            if (token == null || !AuthPath.isAuthPath(url, method)) {
                chain.doFilter(request, response);
                return;
            }

            UsernamePasswordAuthenticationToken authenticationToken = null;
            Account account = JwtUtils.getAccount(token);
            String username = account.getUsername();
            if (!StringUtils.isEmpty(username)) {
                authenticationToken = new UsernamePasswordAuthenticationToken(username, null, account.getAuthorities());
            }
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            doFilter(request, response, chain);
        } catch (ExpiredJwtException | MalformedJwtException | IllegalArgumentException | SignatureException e) {
            log.error("Jwt校验错误 : {}", e.getMessage());
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            PrintWriter printWriter = response.getWriter();
            printWriter.write(ResultEntity.build(ResultStatus.JWT_ERROR).toJSONString());
            printWriter.close();
            printWriter.flush();
        }
    }
}
