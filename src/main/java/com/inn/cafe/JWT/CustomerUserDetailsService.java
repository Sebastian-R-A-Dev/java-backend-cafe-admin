package com.inn.cafe.JWT;

import com.inn.cafe.dao.UserDao;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomerUserDetailsService implements UserDetailsService {
    @Autowired
    UserDao userDao;
    private com.inn.cafe.model.User userDetail;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Inside load user by username {}", email);
        userDetail = userDao.findByEmailId(email);
        if (userDetail == null) throw new UsernameNotFoundException("User not exists by email : " + email);

        Set<String> roles = new HashSet<>();
        roles.add(userDetail.getRole());

        Set<GrantedAuthority> authorities = roles.stream()
                .map((SimpleGrantedAuthority::new))
                .collect(Collectors.toSet());

        return new User(
                email,
                userDetail.getPassword(),
                authorities
        );
    }


    public com.inn.cafe.model.User getUserDetails() {
        return this.userDetail;
    }
}
