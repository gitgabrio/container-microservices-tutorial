@file:JvmName("UsersService")

package net.microservices.tutorial.actorserverservice.services

import net.microservices.tutorial.dto.UserDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.AsyncRestTemplate
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.lang.Exception
import java.util.logging.Level
import java.util.logging.Logger
import javax.annotation.PostConstruct


/**
 * Hide the access to the microservice inside this local service.

l
 */
@Service
open class UsersService(persistenceServiceUrl: String) {

    @Autowired
    @LoadBalanced
    private var restTemplate: RestTemplate? = null

    @Autowired
    @LoadBalanced
    private var asyncRestTemplate: AsyncRestTemplate? = null

    private var persistenceServiceUrl: String

    private var logger = Logger.getLogger(UsersService::class.java.name)

    init {
        this.persistenceServiceUrl = if (persistenceServiceUrl.startsWith("http"))
            persistenceServiceUrl
        else
            "http://$persistenceServiceUrl"
    }

    /**
     * The RestTemplate works because it uses a custom request-factory that uses
     * Ribbon to look-up the service to use. This method simply exists to show
     * this.
     */
    @PostConstruct
    fun demoOnly() {
        // Can't do this in the constructor because the RestTemplate injection
        // happens afterwards.
        logger.warning("The RestTemplate request factory is " + restTemplate!!.requestFactory.javaClass)
    }

    @Throws(Exception::class)
    fun findAll(): List<UserDTO>? {
        logger.info("findAll() invoked")
        var users: Array<UserDTO>? = null
        try {
            users = restTemplate!!.getForObject("$persistenceServiceUrl/persons/", Array<UserDTO>::class.java)
        } catch (e: HttpClientErrorException) { // 404
            // Nothing found
            return null
        }
        return if (users == null || users.isEmpty())
            null
        else
            listOf(*users)
    }

    @Throws(Exception::class)
    fun findByNumber(userNumber: Int): UserDTO? {
        logger.info("findByNumber() invoked: for $userNumber")
        return try {
            restTemplate!!.getForObject("$persistenceServiceUrl/persons/{id}", UserDTO::class.java, userNumber)
        } catch (e: HttpClientErrorException) { // 404
            // Nothing found
            null
        }
    }

    @Throws(Exception::class)
    fun saveUser(newUser: UserDTO): UserDTO {
        logger.info("saveUser() invoked: for $newUser")
        try {
            return restTemplate!!.postForObject("$persistenceServiceUrl/persons/save", newUser, UserDTO::class.java)
                ?: throw Exception("Failed to save user")
        } catch (e: HttpClientErrorException) { // 404
            // Nothing found
            throw e
        }
    }

    @Throws(Exception::class)
    fun updateUser(updatedUser: UserDTO): UserDTO {
        logger.info("updateUser() invoked: for $updatedUser")
        return updatedUser
//        try {
//            return restTemplate!!.postForObject("$persistenceServiceUrl/persons/save", newUser, UserDTO::class.java) ?: throw Exception("Failed to save user")
//        } catch (e: HttpClientErrorException) { // 404
//            // Nothing found
//            throw e
//        }
    }

    @Throws(Exception::class)
    fun deleteUser(deletedUser: UserDTO): UserDTO {
        logger.info("deleteUser() invoked: for $deletedUser")
        return deletedUser
//        try {
//            return restTemplate!!.postForObject("$persistenceServiceUrl/persons/save", newUser, UserDTO::class.java) ?: throw Exception("Failed to save user")
//        } catch (e: HttpClientErrorException) { // 404
//            // Nothing found
//            throw e
//        }
    }


    private inline fun <reified T : Any> genericClass(): Class<T> = T::class.java

}