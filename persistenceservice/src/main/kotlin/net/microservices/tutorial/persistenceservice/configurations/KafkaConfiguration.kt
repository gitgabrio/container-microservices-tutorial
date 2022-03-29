@file:JvmName("PersistenceConfiguration")

package net.microservices.tutorial.persistenceservice.configurations

import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.KafkaListenerContainerFactory
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import java.util.logging.Logger


/**
 * The users Spring configuration.
 *
 */
@Configuration
@ComponentScan("net.microservices.tutorial.persistenceservice")
open class KafkaConfiguration {

    private var logger: Logger = Logger.getLogger(javaClass.name)

    companion object {
        const val topicName: String = "persistence-input"
        const val groupId = "myGroup"
    }

    @Value("\${spring.kafka.bootstrap-servers}")
    private val bootstrapServers: String? = null

    @Bean
    open fun consumerConfigs(): Map<String, Any>? {
        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers!!
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        return props
    }

    @Bean
    open fun consumerFactory(): ConsumerFactory<String?, String?>? {
        return DefaultKafkaConsumerFactory(consumerConfigs())
    }

    @Bean
    open fun kafkaListenerContainerFactory(): KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String?, String?>?>? {
        val factory: ConcurrentKafkaListenerContainerFactory<String?, String> =
            ConcurrentKafkaListenerContainerFactory()
        factory.consumerFactory = consumerFactory()
        return factory
    }

    @Bean
    open fun topicPersistenceInput(): NewTopic? {
        return TopicBuilder.name(topicName).build()
    }


}
