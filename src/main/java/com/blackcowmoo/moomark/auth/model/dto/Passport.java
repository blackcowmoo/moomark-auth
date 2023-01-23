package com.blackcowmoo.moomark.auth.model.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class Passport {
  public Timestamp exp; // expired timestamp
  public String key;
  public String hash;
}
