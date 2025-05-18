package com.noralearn.goethehub.common.config;

import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserAgentConfig {

  @Bean
  public UserAgentAnalyzer userAgentAnalyzer() {
    return UserAgentAnalyzer
        .newBuilder()
        // TODO: will apply after all dev & prod env properly configured
        // .hideMatcherLoadStats()
        .withCache(10000)
        .withFields(
            UserAgent.DEVICE_CLASS,
            UserAgent.DEVICE_NAME,
            UserAgent.OPERATING_SYSTEM_NAME,
            UserAgent.OPERATING_SYSTEM_CLASS,
            UserAgent.OPERATING_SYSTEM_NAME_VERSION,
            UserAgent.LAYOUT_ENGINE_CLASS,
            UserAgent.LAYOUT_ENGINE_NAME_VERSION,
            UserAgent.AGENT_CLASS,
            UserAgent.AGENT_NAME,
            UserAgent.AGENT_NAME_VERSION
        )
        .build();
  }
}
