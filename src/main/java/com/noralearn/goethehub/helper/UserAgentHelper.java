package com.noralearn.goethehub.helper;

import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

public class UserAgentHelper {

  public static String buildDeviceTypeByUserAgent(String rawUserAgent) {
    return UserAgentAnalyzer
        .newBuilder()
        .withFields(
            UserAgent.DEVICE_CLASS,
            UserAgent.OPERATING_SYSTEM_NAME_VERSION,
            UserAgent.AGENT_NAME_VERSION
        )
        .build()
        .toString();
  }
}
