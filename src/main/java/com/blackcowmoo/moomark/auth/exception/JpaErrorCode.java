package com.blackcowmoo.moomark.auth.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JpaErrorCode {
  DONT_KNOW_ERROR("DOESN'T KNOW ERROR", 100000),
  CANNOT_FIND_MEMBER("CANNOT_FIND_MEMBER", 100001),
  CANNOT_FIND_MEMBER_BY_EMAIL("CANNOT_FIND_MEMBER_BY_EMAIL",100002),
  CANNOT_FIND_MEMBER_BY_NICKNAME("CANNOT_FIND_MEMBER_BY_NICKNAME",100003),
  ALREADY_EXIST_EMAIL("ALREADY_EXIST_EMAIL", 100004);
  
  
  protected final String msg;
  protected final int code;
}
