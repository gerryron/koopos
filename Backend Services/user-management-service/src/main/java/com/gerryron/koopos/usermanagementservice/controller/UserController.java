package com.gerryron.koopos.usermanagementservice.controller;

import com.gerryron.koopos.usermanagementservice.service.UserService;
import com.gerryron.koopos.usermanagementservice.shared.request.SignUpRequest;
import com.gerryron.koopos.usermanagementservice.shared.response.RestResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<RestResponse<Object>> signUp(
            @RequestBody @Valid SignUpRequest signUpRequest
            ) {
        return ResponseEntity.ok(userService.createUser(signUpRequest));
    }
}
