package com.knowy.persistence;

import com.knowy.persistence.adapter.jpa.KnowyJpaRepositoryConfiguration;
import com.knowy.persistence.adapter.jpa.dao.KnowyJpaDaoConfiguration;
import com.knowy.persistence.adapter.jpa.mapper.KnowyJpaMapperConfiguration;
import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;

@AutoConfiguration
@ConditionalOnClass({EntityManager.class, JpaRepository.class})
@ConditionalOnMissingBean(JpaRepositoryFactoryBean.class)
@Import({KnowyJpaDaoConfiguration.class, KnowyJpaMapperConfiguration.class, KnowyJpaRepositoryConfiguration.class})
public class KnowyJpaAutoConfiguration {
}

