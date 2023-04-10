package com.gerryron.koopos.usermanagementservice.config;

import com.gerryron.koopos.usermanagementservice.entity.UserEntity;
import com.gerryron.koopos.usermanagementservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> credential = repository.findByUsername(username);
        return credential.map(CustomUserDetails::new).orElseThrow(
                () -> new UsernameNotFoundException("not found username: " + username));
    }

}
