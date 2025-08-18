package com.knowy.adapter.persistence.jpa;

import com.knowy.core.port.CourseRepository;
import com.knowy.core.user.port.UserRepository;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ConditionalOnMissingBean({UserRepository.class, CourseRepository.class})
@ComponentScan
public class KnowyJpaAutoConfiguration {

}

