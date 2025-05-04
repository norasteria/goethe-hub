package com.noralearn.usermanagment.enums;

public enum AuthActivityStatus {
  LOGIN_SUCCESS,
  LOGIN_FAILED,
  LOGIN_SUSPENDED, // after 3x failed in a day
  LOGOUT
}
