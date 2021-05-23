package edu.dhu.auction.web.auth;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import edu.dhu.auction.web.bean.Account;
import edu.dhu.auction.web.bean.vo.AccountVo;
import edu.dhu.auction.web.common.RequestLoggerHandler;
import edu.dhu.auction.web.repository.AccountRepository;
import edu.dhu.auction.web.service.impl.AccountServiceImpl;
import edu.dhu.auction.web.util.BeanUtils;
import edu.dhu.auction.web.util.JwtUtils;
import edu.dhu.auction.web.util.ResultEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Map;

import static edu.dhu.auction.web.util.ResultStatus.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger log = LogManager.getLogger(WebSecurityConfig.class);

    @Resource
    private AccountRepository accountRepository;

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        return new AccountServiceImpl();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OncePerRequestFilter requestLoggerFilter() {
        return new RequestLoggerHandler();
    }

    @Bean
    public AuthenticationFilter authenticationFilter() throws Exception {
        AuthenticationFilter filter = new AuthenticationFilter();
        filter.setFilterProcessesUrl("/user/login");
        filter.setAuthenticationManager(authenticationManagerBean());
        filter.setAuthenticationSuccessHandler(this::onAuthenticationSuccess);
        filter.setAuthenticationFailureHandler(this::onAuthenticationFailure);
        return filter;
    }

    @Bean
    public AuthorizationFilter authorizationFilter() {
        return new AuthorizationFilter();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(ImmutableList.of("http://sqrelord.xyz", "https://sqrelord.xyz"));
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return this::onAuthenticationSuccess;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(bCryptPasswordEncoder());
    }

    // @formatter:off
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                    .disable()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .sessionFixation().none()
                    .and()
                .cors()
                    .configurationSource(corsConfigurationSource())
                    .and()
                .authorizeRequests()
                    .anyRequest().permitAll()
                    .antMatchers(GET, AuthPath.getPath(GET)).authenticated()
                    .antMatchers(POST, AuthPath.getPath(POST)).authenticated()
                    .antMatchers(DELETE, AuthPath.getPath(DELETE)).authenticated()
                    .and()
                .exceptionHandling()
                    .authenticationEntryPoint(this::onEntryPoint)
                    .accessDeniedHandler(this::onAccessDenied)
                    .and()
                .formLogin()
                    .permitAll()
                    .successHandler(this::onAuthenticationSuccess)
                    .failureHandler(this::onAuthenticationFailure)
                    .and()
                .addFilterBefore(requestLoggerFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(authorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
    // @formatter:on

    private void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication auth) throws IOException {
        Account account = (Account) auth.getPrincipal();
        AccountVo accountVo = BeanUtils.copy(account, AccountVo.class);
        String token = JwtUtils.generateToken(accountVo);
        accountRepository.updateLoginTime(account.getId(), LocalDateTime.now());
        log.info("用户 <{}> 登录", account.getUsername());
        response.setContentType(APPLICATION_JSON_UTF8_VALUE);
        PrintWriter writer = response.getWriter();
        Map<String, Object> data = ImmutableMap.of("token", token, "info", accountVo);
        writer.write(ResultEntity.build(LOGIN_SUCCESS, data).toJSONString());
        writer.close();
    }

    private void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                         AuthenticationException ex) throws IOException {
        response.setContentType(APPLICATION_JSON_UTF8_VALUE);
        PrintWriter writer = response.getWriter();
        if (ex instanceof InternalAuthenticationServiceException || ex instanceof UsernameNotFoundException) {
            writer.write(ResultEntity.build(ACCOUNT_NOT_EXIST).toJSONString());
        } else if (ex instanceof BadCredentialsException) {
            writer.write(ResultEntity.build(PASSWORD_ERROR).toJSONString());
        } else {
            writer.write(ResultEntity.build(LOGIN_FAILURE).toJSONString());
        }
        writer.close();
    }

    private void onEntryPoint(HttpServletRequest request, HttpServletResponse response,
                              AuthenticationException ex) throws IOException {
        response.setContentType(APPLICATION_JSON_UTF8_VALUE);
        PrintWriter writer = response.getWriter();
        if (ex instanceof InsufficientAuthenticationException) {
            writer.write(ResultEntity.build(NOT_LOGIN).toJSONString());
        } else {
            writer.write(ResultEntity.build(SYSTEM_ERROR).toJSONString());
        }
        writer.close();
    }

    private void onAccessDenied(HttpServletRequest request, HttpServletResponse response,
                                AccessDeniedException ex) throws IOException {
        response.setContentType(APPLICATION_JSON_UTF8_VALUE);
        PrintWriter writer = response.getWriter();
        writer.write(ResultEntity.build(ACCESS_DENIED).toJSONString());
        writer.close();
    }
}
