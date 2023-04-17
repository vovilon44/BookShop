package com.example.MyBookShopApp.security.jwt;

import com.example.MyBookShopApp.data.services.SessionService;
import com.example.MyBookShopApp.security.BlackListService;
import com.example.MyBookShopApp.security.BookstoreUserDetails;
import com.example.MyBookShopApp.security.BookstoreUserDetailsService;
import com.example.MyBookShopApp.security.BookstoreUserRegister;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    private final BookstoreUserDetailsService bookstoreUserDetailsService;
    private final JWTUtil jwtUtil;
    private final BlackListService blackListService;
    private final SessionService sessionService;


    private Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    public JWTRequestFilter(BookstoreUserDetailsService bookstoreUserDetailsService, JWTUtil jwtUtil, BlackListService blackListService, SessionService sessionService) {
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
        this.jwtUtil = jwtUtil;
        this.blackListService = blackListService;
        this.sessionService = sessionService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        String token = null;
        String username = null;
        Boolean isBlackList = false;
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    token = cookie.getValue();
                    isBlackList = blackListService.tokenIsBlackList(token);
                    if (isBlackList){
                        cookie.setMaxAge(0);
                        httpServletResponse.addCookie(cookie);
                        logger.warning("token in blacklist");
                    }
                    try {
                        username = jwtUtil.extractUsername(token);
                    } catch (ExpiredJwtException | SignatureException e) {
                        cookie.setMaxAge(0);
                        httpServletResponse.addCookie(cookie);
                        logger.warning(e.getLocalizedMessage());
                    }
                }
            }
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null && !isBlackList) {
                    BookstoreUserDetails userDetails;
                    try {
                        userDetails = bookstoreUserDetailsService.loadUserByUsername(username);
                        if (jwtUtil.validateToken(token, userDetails)) {
                            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                            sessionService.sessionsHandler(userDetails.getBookstoreUser(), token, httpServletRequest.getHeader("user-agent"));
                        }
                    } catch (UsernameNotFoundException e) {
                        logger.warning(e.getLocalizedMessage());
                    }
                }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
