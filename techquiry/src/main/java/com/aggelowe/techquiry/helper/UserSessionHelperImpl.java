package com.aggelowe.techquiry.helper;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.aggelowe.techquiry.service.Authentication;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Scope("session")
@Component
@Setter
@Getter
public class UserSessionHelperImpl implements UserSessionHelper {

    Authentication authentication;
    
}
