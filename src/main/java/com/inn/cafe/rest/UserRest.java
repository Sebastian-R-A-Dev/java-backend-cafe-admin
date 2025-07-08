package com.inn.cafe.rest;

import com.inn.cafe.VO.UserVO;
import com.inn.cafe.utils.ResponseResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping(path = "/user")
public interface UserRest {

    @PostMapping(path = "/signup")
    public ResponseEntity<ResponseResult> signUp(@RequestBody Map<String, String> requestMap);

    @PostMapping(path = "/login")
    public ResponseEntity<ResponseResult> login(@RequestBody UserVO body);

    @GetMapping(path = "/get")
    public ResponseEntity<ResponseResult> getAllUser();

    @PostMapping(path = "/update")
    public ResponseEntity<ResponseResult> update(@RequestBody Map<String, String> requestMap);

    @GetMapping(path = "/check-token")
    public ResponseEntity<ResponseResult> checkToken();

    @PostMapping(path = "/change-password")
    public ResponseEntity<ResponseResult> changePassword(@RequestBody UserVO body);

    @PostMapping(path = "forgot-password")
    public ResponseEntity<ResponseResult> forgotPassword(@RequestBody UserVO body);
}
