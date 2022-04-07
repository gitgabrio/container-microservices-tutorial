@file:JvmName("GraphqlServer")

package net.microservices.tutorial.graphqlservice

import net.microservices.tutorial.graphqlservice.configurations.GraphQLConfiguration
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import java.util.logging.Logger

/**
 * Run as a micro-service, registering with the Discovery Server (Eureka).
 *
 *
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(useDefaultFilters = false) // Disable component scanner
@Import(GraphQLConfiguration::class)
open class GraphqlServer {

    protected var logger = Logger.getLogger(GraphqlServer::class.java.name)

    companion object {

        /**
         * Run the application using Spring Boot and an embedded servlet engine.

         * @param args
         * *            Program arguments - ignored.
         */
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(GraphqlServer::class.java, *args)
        }
    }
}
