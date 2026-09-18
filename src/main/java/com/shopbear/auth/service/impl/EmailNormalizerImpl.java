package com.shopbear.auth.service.impl;

import com.shopbear.auth.service.EmailNormalizer;
import org.springframework.stereotype.Service;

@Service
public class EmailNormalizerImpl implements EmailNormalizer {

    @Override
    public String normalize(String email) {
        return email.trim().toLowerCase();
    }
}
