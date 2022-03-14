@file:JvmName("PersistenceConfiguration")
package net.microservices.tutorial.persistenceservice.configurations

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import java.util.logging.Logger
import javax.sql.DataSource

/**
 * The users Spring configuration.
 *
 */
@Configuration
@ComponentScan("net.microservices.tutorial.persistenceservice")
@EntityScan("net.microservices.tutorial.persistenceservice.entities")
@EnableJpaRepositories("net.microservices.tutorial.persistenceservice.repositories")
open class PersistenceConfiguration {

    private var logger: Logger = Logger.getLogger(javaClass.name)

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "test-datasource")
    open fun dataSource(): DataSource {
        logger.info("dataSource() invoked")
        return DataSourceBuilder.create().build()
    }



}
