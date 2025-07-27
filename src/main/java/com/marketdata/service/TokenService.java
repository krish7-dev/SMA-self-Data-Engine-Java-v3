package com.marketdata.service;

import com.marketdata.repository.TokenQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Autowired
    private TokenQuery tokenQuery;

    public void storeTokens(String accessToken, String publicToken) {
        tokenQuery.saveTokens(accessToken, publicToken);
        System.out.println("Tokens saved successfully!");
    }
}
