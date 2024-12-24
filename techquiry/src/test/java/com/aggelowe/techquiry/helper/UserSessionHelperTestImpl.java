package com.aggelowe.techquiry.helper;

import com.aggelowe.techquiry.service.Authentication;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Data
@NoArgsConstructor
public class UserSessionHelperTestImpl implements UserSessionHelper {

    Authentication authentication;

}
