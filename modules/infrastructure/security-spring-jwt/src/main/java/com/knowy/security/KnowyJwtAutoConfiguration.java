package com.knowy.security;

import com.knowy.core.user.port.KnowyPasswordEncoder;
import com.knowy.core.user.port.KnowyTokenTools;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ConditionalOnMissingBean({KnowyPasswordEncoder.class, KnowyTokenTools.class})
@ComponentScan
public class KnowyJwtAutoConfiguration {
}
