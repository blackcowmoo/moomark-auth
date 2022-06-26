package com.blackcowmoo.moomark.auth.model.dto;

import java.sql.Timestamp;

import com.blackcowmoo.moomark.auth.model.entity.User;

import lombok.Data;

@Data
public class PassportResponse {
  public Timestamp exp; // expired timestamp
  public User user;
}
