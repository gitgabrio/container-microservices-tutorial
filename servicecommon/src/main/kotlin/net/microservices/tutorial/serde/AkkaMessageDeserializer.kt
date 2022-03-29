package net.microservices.tutorial.serde

import com.fasterxml.jackson.databind.ObjectMapper
import net.microservices.tutorial.messages.AkkaMessage
import net.microservices.tutorial.serde.AkkaMessageDeserializer
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.LoggerFactory

class AkkaMessageDeserializer : Deserializer<AkkaMessage?> {
    private val objectMapper = ObjectMapper()
    override fun configure(configs: Map<String?, *>?, isKey: Boolean) {}
    override fun deserialize(topic: String, objectData: ByteArray): AkkaMessage? {
        return try {
            objectMapper.readValue(objectData, AkkaMessage::class.java)
        } catch (e: Exception) {
            log.error("Failed to deserialize topic $topic", e)
            null
        }
    }

    override fun close() {}

    companion object {
        private val log = LoggerFactory.getLogger(AkkaMessageDeserializer::class.java)
    }
}