package net.microservices.tutorial.persistenceservice.components

import net.microservices.tutorial.commands.Command
import net.microservices.tutorial.messages.AkkaMessage
import net.microservices.tutorial.persistenceservice.configurations.KafkaConfiguration
import net.microservices.tutorial.persistenceservice.configurations.KafkaConfiguration.Companion.groupId
import net.microservices.tutorial.persistenceservice.repositories.PersonRepository
import net.microservices.tutorial.serde.AkkaMessageDeserializer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
open class KafkaListener  {
    private val log = LoggerFactory.getLogger(KafkaListener::class.java)

    private val akkaMessageDeserializer: AkkaMessageDeserializer = AkkaMessageDeserializer()

    @Autowired
    private val personRepository: PersonRepository? = null


    @KafkaListener(topics = [KafkaConfiguration.topicName], groupId = groupId)
    fun listener(data: String?) {
        log.info(data)
        data?.let {
            val akkaMessage: AkkaMessage? =
                akkaMessageDeserializer.deserialize(KafkaConfiguration.topicName, data.toByteArray())
            log.info("akkamessage $akkaMessage")
            akkaMessage?.let { manageMessage(it)}
        }
    }

    private fun manageMessage(akkaMessage: AkkaMessage) {
        personRepository?.let {
            when (akkaMessage.command) {
//                Command.READ ->
            }
        }

    }
}