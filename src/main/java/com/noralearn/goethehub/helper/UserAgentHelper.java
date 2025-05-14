package com.noralearn.goethehub.helper;

import lombok.RequiredArgsConstructor;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAgentHelper {

  private final UserAgentAnalyzer userAgentAnalyzer;

  public  String buildDeviceTypeByUserAgent(String rawUserAgent) {
    UserAgent userAgent = userAgentAnalyzer.parse(rawUserAgent);

     return String.join(":",
         userAgent.getValue(UserAgent.DEVICE_CLASS),
         userAgent.getValue(UserAgent.OPERATING_SYSTEM_NAME_VERSION),
         userAgent.getValue(UserAgent.AGENT_NAME_VERSION)
     );
  }
}
