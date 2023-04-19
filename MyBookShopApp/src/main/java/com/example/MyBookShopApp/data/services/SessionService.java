package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.SessionEntity;
import com.example.MyBookShopApp.data.repositories.SessionRepository;
import com.example.MyBookShopApp.security.BlackListRepository;
import com.example.MyBookShopApp.security.BookstoreUser;
import com.example.MyBookShopApp.security.BookstoreUserDetails;
import com.example.MyBookShopApp.security.TokenEntity;
import com.example.MyBookShopApp.security.jwt.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final JWTUtil jwtUtil;

    private final BlackListRepository blackListRepository;

    @Autowired
    public SessionService(SessionRepository sessionRepository, JWTUtil jwtUtil, BlackListRepository blackListRepository) {
        this.sessionRepository = sessionRepository;
        this.jwtUtil = jwtUtil;
        this.blackListRepository = blackListRepository;
    }

    public void newSession(BookstoreUser user, String token) {
        sessionRepository.save(new SessionEntity(user, token));
    }

    public void sessionsHandler(BookstoreUser user, String token, String userAgentInHeader) throws UnknownHostException {
        SessionEntity session = sessionRepository.findFirstByToken(token);
        if (session != null && !session.getDateLastSession().equals(LocalDate.now())){
            saveCurrentSession(session);
        } else if (session == null){
            List<SessionEntity> sessions = sessionRepository.findAllByUserOrderByCountDay(user);
            if (sessions.size() > 2){
                removeExcessSessions(user, sessions);
            }
            saveNewSession(setPlatformAndModel(new SessionEntity(user, token), userAgentInHeader));
        }
    }
    private void saveCurrentSession(SessionEntity session){
        session.setCountDay(session.getCountDay() + 1);
        session.setDateLastSession(LocalDate.now());
        sessionRepository.save(session);
    }

    private void saveNewSession(SessionEntity session){
        session.setCountDay(session.getCountDay() + 1);
        session.setDateLastSession(LocalDate.now());
        sessionRepository.save(session);
    }

    private SessionEntity setPlatformAndModel(SessionEntity session, String userAgentInHeader){
        String model = "";
        String platform = "";
        String[] partsUserAgent = userAgentInHeader.split(";");
        if (partsUserAgent.length == 2){
            platform = partsUserAgent[1].substring(0, partsUserAgent[1].indexOf(")"));
        } else if (partsUserAgent.length > 2){
            platform = partsUserAgent[1];
            model = partsUserAgent[2].indexOf(")") > 0 ? partsUserAgent[2].substring(0, partsUserAgent[2].indexOf(")")) : "";
        }
        session.setPlatform(platform);
        session.setPhoneModel(model);
        return session;
    }

    private void removeExcessSessions(BookstoreUser user, List<SessionEntity> sessions){
        BookstoreUserDetails details = new BookstoreUserDetails(user);
        List<SessionEntity> sessionsWithoutExpiredToken = new ArrayList<>();
        for (SessionEntity sessionEntity : sessions){
            try {
                jwtUtil.validateToken(sessionEntity.getToken(), details);
                sessionsWithoutExpiredToken.add(sessionEntity);
            } catch (ExpiredJwtException | SignatureException e) {
                sessionRepository.delete(sessionEntity);
            }

        }
        if (sessionsWithoutExpiredToken.size() == 3){
            blackListRepository.save(new TokenEntity(sessions.get(0).getToken(), new Date()));
            sessionRepository.delete(sessions.get(0));
        }
    }

    public List<SessionEntity> getSessions(BookstoreUser user){
        return sessionRepository.findAllByUserOrderByCountDay(user);
    }

    public Boolean removeSession(Integer id) {
        SessionEntity session = sessionRepository.findSessionEntityById(id);
        if (session != null) {
            blackListRepository.save(new TokenEntity(session.getToken(), new Date()));
            sessionRepository.delete(session);
            return true;
        } else {
            return false;
        }
    }


    public Boolean removeAllSessions(Integer[] ids) {
        List<SessionEntity> sessions = sessionRepository.findAllByIdIn(ids);
            for (SessionEntity session : sessions){
                blackListRepository.save(new TokenEntity(session.getToken(), new Date()));
                    sessionRepository.delete(session);
            }
            return true;

    }


}
