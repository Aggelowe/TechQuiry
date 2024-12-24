package com.aggelowe.techquiry.service;

import io.micrometer.common.lang.NonNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class Authentication {
   
    @NonNull
    private final int id;

}
