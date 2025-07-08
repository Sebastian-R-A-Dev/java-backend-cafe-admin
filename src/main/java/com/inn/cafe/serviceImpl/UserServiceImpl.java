package com.inn.cafe.serviceImpl;

import com.inn.cafe.JWT.CustomerUserDetailsService;
import com.inn.cafe.JWT.JwtAuthenticationFilter;
import com.inn.cafe.JWT.JwtUtil;
import com.inn.cafe.VO.UserVO;
import com.inn.cafe.constants.Constants;
import com.inn.cafe.dao.UserDao;
import com.inn.cafe.enums.UserRole;
import com.inn.cafe.enums.UserStatus;
import com.inn.cafe.exceptions.UserNotFoundByIdException;
import com.inn.cafe.exceptions.UserStatusNotCorrectException;
import com.inn.cafe.model.User;
import com.inn.cafe.service.UserService;
import com.inn.cafe.utils.DateUtils;
import com.inn.cafe.utils.EmailUtils;
import com.inn.cafe.utils.PasswordUtils;
import com.inn.cafe.utils.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    PasswordUtils passwordUtils;

    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    EmailUtils emailUtils;

    private static final Integer PASSWORD_LENGTH = 8;

    @Override
    public ResponseEntity<ResponseResult> signUp(Map<String, String> requestMap) {
        log.info("Inside signup {}", requestMap);
        if (this.validateSingUpMap(requestMap)) {
            User user = userDao.findByEmailId(requestMap.get(Constants.EMAIL_KEY));
            if (user != null) {
                return new ResponseEntity<>(
                        new ResponseResult("Email already exists.", false),
                        HttpStatus.BAD_REQUEST
                );
            }
            user = this.getUserFromMap(requestMap);
            userDao.save(user);
            return new ResponseEntity<>(
                    new ResponseResult("User successfully registered.", user, true),
                    HttpStatus.OK
            );
        }
        return new ResponseEntity<>(new ResponseResult("Invalid Data.", false), HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<ResponseResult> login(UserVO userVO) {
        log.info("Inside login");
        Authentication auth = this.initAuthentication(userVO.getEmail(), userVO.getPassword());
        User user = customerUserDetailsService.getUserDetails();

        if (
                auth.isAuthenticated() && user != null &&
                        user.getStatus().equalsIgnoreCase(UserStatus.ENABLE.name())
        ) {
            Map<String, String> tokenResponse = new HashMap<>();
            tokenResponse.put("token", jwtUtil.generateToken(user.getEmail(), user.getRole()));
            return new ResponseEntity<>(
                    new ResponseResult(
                            "User logged and token generated.",
                            tokenResponse,
                            true
                    ),
                    HttpStatus.OK
            );
        } else return new ResponseEntity<>(
                new ResponseResult("Cannot do login please contact with the administrator", false),
                HttpStatus.BAD_REQUEST
        );
    }

    @Override
    public ResponseEntity<ResponseResult> getAllUser() {
        if (!jwtAuthenticationFilter.isAdmin()) {
            return new ResponseEntity<>(
                    new ResponseResult(
                            "Cannot get all users because you don't have the permissions",
                            new ArrayList<>(),
                            false
                    ),
                    HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(
                new ResponseResult(
                        "User list",
                        userDao.getAllUser(),
                        true
                ),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<ResponseResult> update(Map<String, String> requestMap) throws UserNotFoundByIdException, UserStatusNotCorrectException {
        if (!jwtAuthenticationFilter.isAdmin()) {
            return new ResponseEntity<>(
                    new ResponseResult(
                            "Cannot update users because you don't have the permissions",
                            false
                    ),
                    HttpStatus.UNAUTHORIZED);
        }

        String userId = requestMap.get(Constants.ID_KEY);
        String userStatus = requestMap.get(Constants.STATUS_KEY);
        //verify is the user status is correct
        if (!this.userStatusIsCorrect(userStatus))
            throw new UserStatusNotCorrectException("The user status you are trying to assign is not correct");

        Optional<User> user = userDao.findById(Integer.parseInt(userId));
        //verify is the user is present
        if (user.isEmpty()) throw new UserNotFoundByIdException("Cannot find any user by Id : " + userId);
        //is the user is present lets modify the status
        user.get().setStatus(userStatus);
        userDao.save(user.get());
        //lest email to all admin users to notify that new user have been change the status
        sendEnableOrDisableMailToAllAdminUsers(userStatus, user.get().getEmail(), userDao.getAllAdminEmails());

        return new ResponseEntity<>(
                new ResponseResult(
                        "The new state of user " + user.get().getEmail() + " is " + userStatus,
                        false
                ),
                HttpStatus.ACCEPTED
        );
    }

    @Override
    public ResponseEntity<ResponseResult> checkToken() {
        return new ResponseEntity<>(new ResponseResult("Valid token", true, true), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseResult> changePassword(UserVO body) throws UserNotFoundByIdException {
        User user = userDao.findByEmailId(jwtAuthenticationFilter.getCurrentUser());
        if (user == null) throw new UserNotFoundByIdException("Cannot find any user by email id: " + body.getEmail());
        if (!passwordUtils.verifyPasswordAuthenticity(body.getOldPassword(), user.getPassword()))
            return new ResponseEntity<>(new ResponseResult("The old password isn't correct.", false), HttpStatus.BAD_REQUEST);
        if (passwordUtils.verifyPasswordAuthenticity(body.getPassword(), user.getPassword()))
            return new ResponseEntity<>(new ResponseResult("The new password are the same of the old password", false), HttpStatus.BAD_REQUEST);

        user.setPassword(passwordUtils.encryptPassword(body.getPassword()));
        userDao.save(user);
        return new ResponseEntity<>(new ResponseResult("Password updated successfully!", false), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseResult> forgotPassword(UserVO body) throws UserNotFoundByIdException {
        User user = userDao.findByEmailId(body.getEmail());
        if (user == null) throw new UserNotFoundByIdException("Cannot find any user by email id: " + body.getEmail());
        String temporalPassword = passwordUtils.generateRandomPassword(PASSWORD_LENGTH);
        user.setPassword(passwordUtils.encryptPassword(temporalPassword));
        userDao.save(user);
        this.sendForgotPasswordEmail(temporalPassword, user.getEmail(), user.getName());
        return new ResponseEntity<>(
                new ResponseResult("A new temporal password has been requested, please check your email or spam.", false),
                HttpStatus.OK
        );
    }

    private boolean userStatusIsCorrect(String status) {
        return status.equalsIgnoreCase(UserStatus.ENABLE.name()) ||
                status.equalsIgnoreCase(UserStatus.DISABLE.name());
    }

    private Authentication initAuthentication(String email, String password) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password
                )
        );
    }


    private boolean validateSingUpMap(Map<String, String> requestMap) {
        return requestMap.containsKey(Constants.NAME_KEY) &&
                requestMap.containsKey(Constants.CONTACT_NUMBER_KEY) &&
                requestMap.containsKey(Constants.EMAIL_KEY) &&
                requestMap.containsKey(Constants.PASSWORD_KEY);
    }

    private User getUserFromMap(Map<String, String> requestMap) {
        String encryptedPassword = passwordUtils.encryptPassword(requestMap.get(Constants.PASSWORD_KEY));
        return new User(
                requestMap.get(Constants.NAME_KEY),
                requestMap.get(Constants.CONTACT_NUMBER_KEY),
                requestMap.get(Constants.EMAIL_KEY),
                encryptedPassword,
                UserStatus.DISABLE.name(),
                UserRole.USER.name()
        );
    }

    private void sendForgotPasswordEmail(String temporalPassword, String email, String userName) {
        String htmlBody = emailUtils.forgotPasswordBody(userName, temporalPassword, DateUtils.formatDate());
        emailUtils.sendSimpleMessage(email, EmailUtils.FORGOT_PASSWORD, htmlBody, null);
    }

    private void sendEnableOrDisableMailToAllAdminUsers(String userStatus, String email, List<String> allAdminEmails) {
        //only admin can send emails, so I will remove the current user, because know about it.
        allAdminEmails.remove(jwtAuthenticationFilter.getCurrentUser());
        String currentAdminUser = jwtAuthenticationFilter.getCurrentUser();
        String currentStringDate = DateUtils.formatDate();
        String htmlBody;
        if (userStatus.equalsIgnoreCase(UserStatus.ENABLE.name())) {
            htmlBody = emailUtils.enableOrDisableUserBody(email, currentAdminUser, currentStringDate, false);
            emailUtils.sendSimpleMessage(currentAdminUser, EmailUtils.ACCOUNT_APPROVED, htmlBody, allAdminEmails);
        } else {
            htmlBody = emailUtils.enableOrDisableUserBody(email, currentAdminUser, currentStringDate, true);
            emailUtils.sendSimpleMessage(currentAdminUser, EmailUtils.ACCOUNT_DISABLED, htmlBody, allAdminEmails);
        }

    }
}
