package com.gerryron.koopos.usermanagementservice.controller;

import com.gerryron.koopos.usermanagementservice.service.UserService;
import com.gerryron.koopos.usermanagementservice.shared.request.SignInRequest;
import com.gerryron.koopos.usermanagementservice.shared.request.SignUpRequest;
import com.gerryron.koopos.usermanagementservice.shared.response.RestResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signUp")
    public ResponseEntity<RestResponse<Object>> signUp(
            @RequestBody @Valid SignUpRequest signUpRequest
    ) {
        return ResponseEntity.ok(userService.createUser(signUpRequest));
    }

    @PostMapping("/signIn")
    public ResponseEntity<RestResponse<String>> signIn(
            @RequestBody @Valid SignInRequest signInRequest
    ) {
        return ResponseEntity.ok(userService.login(signInRequest));
    }

    @GetMapping("/validate")
    public ResponseEntity<RestResponse<Object>> validateToken(
            @RequestParam("token") String token
    ) {
        return ResponseEntity.ok(userService.validateToken(token));
    }
}
