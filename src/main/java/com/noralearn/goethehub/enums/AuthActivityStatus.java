package com.noralearn.goethehub.enums;

public enum AuthActivityStatus {
  LOGIN_SUCCESS,
  LOGIN_FAILED,
  LOGIN_SUSPENDED, // after 3x failed in a day
  LOGOUT,
  RESET_PASSWORD
}
