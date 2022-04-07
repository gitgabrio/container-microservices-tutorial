@file:JvmName("GraphQLConfiguration")

package net.microservices.tutorial.graphqlservice.configurations

import graphql.GraphQL
import graphql.schema.GraphQLSchema
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import graphql.schema.idl.TypeRuntimeWiring.newTypeWiring
import net.microservices.tutorial.graphqlservice.components.GraphQLDataFetchers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import java.io.File
import java.nio.charset.Charset
import java.util.logging.Logger


@Configuration
@ComponentScan("net.microservices.tutorial.graphqlservice")
open class GraphQLConfiguration {

    protected var logger = Logger.getLogger(GraphQLConfiguration::class.java.name)

    @Autowired
    var graphQLDataFetchers: GraphQLDataFetchers? = null

    @Bean
    @Throws(Exception::class)
    open fun graphQL(): GraphQL {
        val lines = File("src/main/resources/schema.graphql").readLines(Charsets.UTF_8)
        return if (lines != null) {
            val sdl = lines.joinToString()
            logger.info("sdl $sdl")
            val graphQLSchema = buildSchema(sdl)
            logger.info("graphQLSchema $graphQLSchema")
            GraphQL.newGraphQL(graphQLSchema).build()
        } else {
            logger.severe("Failed to read schema.graphql")
            throw Exception("Failed to instantiate GraphQL")
        }
    }

    private fun buildSchema(sdl: String): GraphQLSchema? {
        val typeRegistry: TypeDefinitionRegistry = SchemaParser().parse(sdl)
        val runtimeWiring: RuntimeWiring = buildWiring()
        val schemaGenerator = SchemaGenerator()
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring)
    }

    private fun buildWiring(): RuntimeWiring {
        return RuntimeWiring.newRuntimeWiring()
            .type(
                newTypeWiring("Query")
                    .dataFetcher("bookById", graphQLDataFetchers?.bookByIdDataFetcher)
            )
            .type(
                newTypeWiring("Book")
                    .dataFetcher("author", graphQLDataFetchers?.authorDataFetcher)
            )
            .build()
    }


}