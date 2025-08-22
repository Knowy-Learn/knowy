package com.knowy.security;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan("com.knowy.security.adapter.jwt")
public class KnowyJwtAutoConfiguration {
}
