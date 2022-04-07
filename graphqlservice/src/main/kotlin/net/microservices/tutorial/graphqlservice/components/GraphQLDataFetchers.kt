package net.microservices.tutorial.graphqlservice.components

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import net.microservices.tutorial.graphqlservice.components.GraphQLDataFetchers
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableMap.of
import org.springframework.stereotype.Component
import java.util.*

@Component
open class GraphQLDataFetchers {

    val bookByIdDataFetcher: DataFetcher<*>
        get() = DataFetcher { dataFetchingEnvironment: DataFetchingEnvironment ->
            val bookId = dataFetchingEnvironment.getArgument<String>("id")
            books
                .stream()
                .filter { book: Map<String, String>? -> book!!["id"] == bookId }
                .findFirst()
                .orElse(null)
        }

    val authorDataFetcher: DataFetcher<*>
        get() = DataFetcher { dataFetchingEnvironment: DataFetchingEnvironment ->
            val book = dataFetchingEnvironment.getSource<Map<String, String>>()
            val authorId = book["authorId"]
            authors
                .stream()
                .filter { author: Map<String, String>? -> author!!["id"] == authorId }
                .findFirst()
                .orElse(null)
        }

    companion object {
        private val books = listOf<Map<String, String>?>(
            mapOf(
                "id" to "book-1",
                "name" to "Harry Potter and the Philosopher's Stone",
                "pageCount" to "223",
                "authorId" to "author-1"
            ),
            mapOf(
                "id" to "book-2",
                "name" to "Moby Dick",
                "pageCount" to "635",
                "authorId" to "author-2"
            ),
            mapOf(
                "id" to "book-3",
                "name" to "Interview with the vampire",
                "pageCount" to "371",
                "authorId" to "author-3"
            )
        )
        private val authors = listOf<Map<String, String>?>(
            mapOf(
                "id" to "author-1",
                "firstName" to "Joanne",
                "lastName" to "Rowling"
            ),
            mapOf(
                "id" to "author-2",
                "firstName" to "Herman",
                "lastName" to "Melville"
            ),
            mapOf(
                "id" to "author-3",
                "firstName" to "Anne",
                "lastName" to "Rice"
            )
        )
    }
}