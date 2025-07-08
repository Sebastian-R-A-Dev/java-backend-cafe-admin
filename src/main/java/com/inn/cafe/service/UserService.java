package com.inn.cafe.service;

import com.inn.cafe.VO.UserVO;
import com.inn.cafe.exceptions.OldPasswordNotCorrectException;
import com.inn.cafe.exceptions.UserNotFoundByIdException;
import com.inn.cafe.exceptions.UserStatusNotCorrectException;
import com.inn.cafe.utils.ResponseResult;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface UserService {
    ResponseEntity<ResponseResult> signUp(Map<String, String> requestMap);

    ResponseEntity<ResponseResult> login(UserVO body);

    ResponseEntity<ResponseResult> getAllUser();

    ResponseEntity<ResponseResult> update(Map<String, String> requestMap) throws UserNotFoundByIdException, UserStatusNotCorrectException;

    ResponseEntity<ResponseResult> checkToken();

    ResponseEntity<ResponseResult> changePassword(UserVO body) throws UserNotFoundByIdException;

    ResponseEntity<ResponseResult> forgotPassword(UserVO body) throws UserNotFoundByIdException;
}
