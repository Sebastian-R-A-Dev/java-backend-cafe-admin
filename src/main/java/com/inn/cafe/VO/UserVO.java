package com.inn.cafe.VO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserVO {
    private String email;
    private String password;
    private String oldPassword;
}
