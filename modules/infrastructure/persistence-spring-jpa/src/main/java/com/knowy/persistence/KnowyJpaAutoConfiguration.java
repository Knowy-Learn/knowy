package com.knowy.persistence;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan("com.knowy.persistence.adapter.jpa")
public class KnowyJpaAutoConfiguration {
}

