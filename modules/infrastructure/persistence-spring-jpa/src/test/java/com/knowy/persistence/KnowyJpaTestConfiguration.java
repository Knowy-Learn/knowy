package com.knowy.persistence;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration
@Import({KnowyJpaMapperAutoConfiguration.class, KnowyJpaRepositoryAutoConfiguration.class})
public class KnowyJpaTestConfiguration {

}
