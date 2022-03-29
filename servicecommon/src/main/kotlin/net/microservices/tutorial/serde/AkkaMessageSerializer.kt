package net.microservices.tutorial.serde

import com.fasterxml.jackson.databind.ObjectMapper
import net.microservices.tutorial.messages.AkkaMessage
import org.apache.kafka.common.serialization.Serializer
import org.slf4j.LoggerFactory

class AkkaMessageSerializer : Serializer<AkkaMessage?> {

    private val objectMapper = ObjectMapper()

    override fun configure(configs: Map<String?, *>?, isKey: Boolean) {}
    override fun serialize(s: String, akkaMessage: AkkaMessage?): ByteArray? {
        try {
            return objectMapper.writeValueAsBytes(akkaMessage)
        } catch (e: Exception) {
            log.error("Failed to serialize akkaMessage $akkaMessage", e)

        }
        return null
    }

    override fun close() {}

    companion object {
        private val log = LoggerFactory.getLogger(AkkaMessageDeserializer::class.java)
    }
}