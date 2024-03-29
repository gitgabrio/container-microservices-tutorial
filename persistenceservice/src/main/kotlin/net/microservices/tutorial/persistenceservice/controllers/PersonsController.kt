package net.microservices.tutorial.persistenceservice.controllers


import net.microservices.tutorial.dto.UserDTO
import net.microservices.tutorial.persistenceservice.entities.UserEntity
import net.microservices.tutorial.persistenceservice.repositories.PersonRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.web.bind.annotation.*
import java.util.logging.Logger


/**
 * A RESTFul controller for accessing user information.
 *
 */
@RestController
class PersonsController


/**
 * Create an instance plugging in the repository of Users.

 * @param personRepository A repository implementation.
 */
@Autowired
constructor(private val personRepository: PersonRepository) {

    private var logger = Logger.getLogger(PersonsController::class.java.simpleName)

    @Autowired
    private val discoveryClient: DiscoveryClient? = null

    @RequestMapping("/service-instances/{applicationName}")
    fun serviceInstancesByApplicationName(
        @PathVariable applicationName: String?): List<ServiceInstance?>? {
        return discoveryClient?.getInstances(applicationName)
    }

    /**
     * Fetch a <code>UserDTO</code> with the specified id.

     * @param id A Integer
     * *
     * @return The user if found.
     * *
     * @throws EntityNotFoundException If the number is not recognised.
     */
    @RequestMapping("/persons/{id}")
    fun byId(@PathVariable("id") id: Int): UserDTO? {
        logger.info("persistence-service byId() invoked: $id")
        return if (personRepository.existsById(id)) {
            val entity = personRepository.findById(id).get()
            UserDTO(entity.id, entity.name, entity.surname)
        } else {
            null
        }
    }

    /**
     * Fetch a <code>UserDTO</code> with the specified surname and name
     * @param surname A String
     *
     *  @param name A String
     *
     * @return The list users found.
     *
     *
     */
    @RequestMapping("/persons/{surname}/{name}")
    fun findBySurnameAndName(@PathVariable("surname") surname: String, @PathVariable("name") name: String): UserDTO {
        logger.info("persistence-service findBySurnameAndName() invoked: $surname $name")
        val entity = personRepository.findBySurnameAndName(surname, name)
        logger.info("persistence-service findBySurnameAndName() found: $entity")
        return UserDTO(entity.id, entity.name, entity.surname)
    }

    /**
     * Fetch a <code>List&lt;UserDTO&gt;</code> with the specified surname and name
     * @param surname A String
     *
     *  @param name A String
     *
     * @return The list users found.
     *
     *
     */
    @RequestMapping("/persons/surname/{surname}")
    fun findBySurname(@PathVariable("surname") surname: String): List<UserDTO> {
        logger.info("persistence-service findBySurname() invoked: $surname ")
        val list: List<UserEntity> = personRepository.findBySurname(surname)
        logger.info("persistence-service findBySurnameAndName() found: $list")
        return list.map { UserDTO(it.id, it.name, it.surname) }
    }

    /**
     * Fetch all <code>List&lt;UserDTO&gt;</code> entities
     *
     * @return All the  `UserEntity` found.
     */
    @RequestMapping("/persons")
    fun findAll(): Iterable<UserDTO> {
        logger.info("persistence-service findAll() invoked")
        val iterable: Iterable<UserEntity> = personRepository.findAll()
        logger.info("persistence-service findAll() found: $iterable")
        return iterable.map { UserDTO(it.id, it.name, it.surname) }
    }

    /**
     * Save a `UserDTO` entity
     *
     * @param toSave `UserDTO` entity  to save
     * *
     * @return The saved `UserDTO;` entity
     */
    @RequestMapping(value = ["/persons/save"], method = [RequestMethod.POST])
    fun save(@RequestBody toSave: UserDTO): UserDTO {
        logger.info("persistence-service save() invoked: $toSave")
        val entity = UserEntity()
        if (toSave.id != null) {
            entity.id = toSave.id!!
        }
        entity.name = toSave.name
        entity.surname = toSave.surname
        val saved = personRepository.save(entity)
        return UserDTO(saved.id, saved.name, saved.surname)
    }
}
