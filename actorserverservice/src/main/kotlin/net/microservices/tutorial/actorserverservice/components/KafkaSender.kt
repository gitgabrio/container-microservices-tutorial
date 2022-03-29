package net.microservices.tutorial.actorserverservice.components

import net.microservices.tutorial.messages.AkkaMessage
import net.microservices.tutorial.serde.AkkaMessageSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaSender @Autowired constructor(private val kafkaTemplate: KafkaTemplate<String, String>) {

    private val akkaMessageSerializer :AkkaMessageSerializer = AkkaMessageSerializer()

    fun sendMessage(akkaMessage: AkkaMessage, topicName: String) {
        val byteArray : ByteArray? = akkaMessageSerializer.serialize(topicName, akkaMessage)
        byteArray?.let {
            val message = String(it)
            kafkaTemplate.send(topicName, message)
        }
    }
}