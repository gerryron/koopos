package com.gerryron.kooposservice.controller;

import com.gerryron.kooposservice.dto.RestResponse;
import com.gerryron.kooposservice.dto.request.SignInRequest;
import com.gerryron.kooposservice.dto.request.SignUpRequest;
import com.gerryron.kooposservice.dto.response.SignInResponse;
import com.gerryron.kooposservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<RestResponse<Object>> signUp(@RequestBody @Valid SignUpRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<RestResponse<SignInResponse>> login(@RequestBody @Valid SignInRequest request) {
        return ResponseEntity.ok(userService.signIn(request));
    }
}
