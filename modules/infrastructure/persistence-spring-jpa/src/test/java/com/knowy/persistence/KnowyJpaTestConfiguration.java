package com.knowy.persistence;

import org.springframework.context.annotation.Import;

@Import({KnowyJpaMapperAutoConfiguration.class, KnowyJpaRepositoryAutoConfiguration.class})
public class KnowyJpaTestConfiguration {

}
