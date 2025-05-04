package com.noralearn.goethehub.helper;

import org.apache.commons.codec.digest.DigestUtils;

public class TokenHasherHelper {

  public static String hashToken(String token) {
    return DigestUtils.sha256Hex(token);
  }
}
