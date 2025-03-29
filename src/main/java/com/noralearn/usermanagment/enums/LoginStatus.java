package com.noralearn.usermanagment.enums;

public enum LoginStatus {
  SUCCESS,
  FAILED,
  SUSPENDED, // after 3x failed in a day
  LOGOUT
}
