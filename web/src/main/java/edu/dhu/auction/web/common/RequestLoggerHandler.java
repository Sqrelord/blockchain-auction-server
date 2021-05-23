package edu.dhu.auction.web.common;

import edu.dhu.auction.web.bean.Account;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class RequestLoggerHandler extends OncePerRequestFilter {

    private static final Logger log = LogManager.getLogger(RequestLoggerHandler.class);

    private static final String ANONYMOUS_USER = "anonymousUser";

    private static final String SOCK_JS_NODE_URL = "sockjs-node";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (!request.getRequestURL().toString().contains(SOCK_JS_NODE_URL)) {
            String queryString = StringUtils.isEmpty(request.getQueryString()) ? "" : "?" + URLDecoder.decode(request.getQueryString(), StandardCharsets.UTF_8.name());
            log.info("请求接口 : {} -> {}", request.getMethod(), request.getRequestURL().toString().concat(queryString));
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = ObjectUtils.isEmpty(authentication) ? ANONYMOUS_USER : ((Account) authentication.getPrincipal()).getUsername();
            log.info("请求用户 : <{}> @ {}:{} ", username, request.getRemoteAddr(), request.getRemotePort());
        }
        filterChain.doFilter(request, response);
    }
}