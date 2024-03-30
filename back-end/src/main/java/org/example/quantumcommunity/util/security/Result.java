package org.example.quantumcommunity.util.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Result {
    public static final int NULL_REQUEST=100;
    public static final int UNAUTHORIZED = 101;
    public static final int BAD_REQUEST = 102;

    public static final int USERNAME_EMPTY=103;
    public static final int EMAIL_EMPTY=104;
    public static final int PHONE_EMPTY=105;
    public static final int PASSWORD_EMPTY=106;
    public static final int USERNAME_INVALID=107;
    public static final int EMAIL_INVALID = 108;
    public static final int PHONE_INVALID = 109;
    public static final int PASSWORD_INVALID = 110;
    public static final int USERNAME_EXISTS=111;
    public static final int EMAIL_EXISTS=112;
    public static final int PHONE_EXISTS=113;
    public static final int REGISTER_FAILED=114;
    public static final int NULL_TITLE=115;
    public static final int NULL_CONTENT=116;
    public static final int TOO_LONG_TITLE=117;
    public static final int WRONG_PAGE_NUM=118;
    public static final int WRONG_PAGE_SIZE=119;
    public static final int NULL_ARTICLE=120;
    public static final int NULL_AUTHOR=121;
    public static final int UPLOAD_FAILED=122;
    public static final int FORBIDDEN=123;
    public static final int TOO_MANY_REQUEST=124;
    public static final int FAILED=125;

    public static final int NOT_FOUND=404;
    public static final int OK = 200;
    public static final int ERROR=500;
    public static final int DELETE_FAILED = 501;
    public static final int NULL_CATEGORY = 502 ;


    private int statusCode;
    private String message;
    private Object data;

}