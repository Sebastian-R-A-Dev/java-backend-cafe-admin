package com.inn.cafe.restImpl;

import com.inn.cafe.VO.UserVO;
import com.inn.cafe.rest.UserRest;
import com.inn.cafe.service.UserService;
import com.inn.cafe.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserRestImpl implements UserRest {
    @Autowired
    UserService userService;


    @Override
    public ResponseEntity<ResponseResult> signUp(Map<String, String> requestMap) {
        try {
            return userService.signUp(requestMap);
        } catch (Exception e) {
           return this.getInternalServerError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseResult> login(UserVO userVO) {
        try {
            return userService.login(userVO);
        } catch (Exception e) {
           return this.getInternalServerError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseResult> getAllUser() {
        try {
            return userService.getAllUser();
        } catch (Exception e) {
           return this.getInternalServerError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseResult> update(Map<String, String> requestMap) {
        try {
            return userService.update(requestMap);
        } catch (Exception e) {
            e.printStackTrace();
           return this.getInternalServerError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseResult> checkToken() {
        try {
            return userService.checkToken();
        } catch (Exception e) {
           return this.getInternalServerError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseResult> changePassword(UserVO body) {
       try {
           return userService.changePassword(body);
       } catch (Exception e) {
           return this.getInternalServerError(e.getMessage());
       }
    }

    @Override
    public ResponseEntity<ResponseResult> forgotPassword(UserVO body) {
       try {
           return userService.forgotPassword(body);
       } catch (Exception e) {
           return this.getInternalServerError(e.getMessage());
       }
    }

    private ResponseEntity<ResponseResult> getInternalServerError(String msj) {
        return new ResponseEntity<>(
                new ResponseResult(msj, false),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
