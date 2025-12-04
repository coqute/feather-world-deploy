package com.featherworld.project.member.model.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Profile("local")
public class LocalEmailServiceImpl implements EmailService {

    @Override
    public String sendEmail(String string, String email) {
        return "";
    }

    @Override
    public int checkAuthKey(Map<String, String> map) {
        return 1;
    }
}
