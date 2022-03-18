@file:JvmName("ActorClientConfiguration")

package net.microservices.tutorial.actorclientservice.configurations

import java.util.concurrent.TimeUnit.SECONDS
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import com.netflix.discovery.EurekaClient
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigValueFactory
import net.microservices.tutorial.actorclientservice.actors.ClientActor
import net.microservices.tutorial.classes.createUserDTO
import net.microservices.tutorial.commands.Command
import net.microservices.tutorial.dto.UserDTO
import net.microservices.tutorial.messages.AkkaMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import scala.concurrent.duration.Duration
import java.util.*

/**
 * Created by Gabriele Cardosi - gcardosi@cardosi.net on 04/11/2016.
 */
@Configuration
@ComponentScan("net.microservices.tutorial.actorclientservice")
open class ActorClientConfiguration {

    @Value("\${eureka.instance.metadata-map.port}")
    var akkaPort: Int = 0


    @Value("\${actorserverservice.name}")
    var actorServerServiceName :String? = null


    @Autowired
    private val eurekaClient: EurekaClient? = null

    @Bean
    open fun actorSystem(): ActorSystem {
        val hostName = eurekaClient?.applicationInfoManager?.info?.ipAddr ?: "hostname"
        val defaultApplication: Config = ConfigFactory.defaultApplication()
                .withValue("akka.remote.netty.tcp.hostname", ConfigValueFactory.fromAnyRef(hostName))
                .withValue("akka.remote.netty.tcp.port", ConfigValueFactory.fromAnyRef(akkaPort))
        val system = ActorSystem.create("Client", defaultApplication)
        val clientActor: ActorRef = system.actorOf(Props.create(ClientActor::class.java, eurekaClient!!, actorServerServiceName!!), "clientActor")
        val r: Random = Random()
        var counter: Int = 1
        system.scheduler().schedule(Duration.create(1, SECONDS),
                Duration.create(1, SECONDS), Runnable {
            val userDto : UserDTO = createUserDTO(r.nextInt(50))
            val command: Command = Command.values()[r.nextInt(4)]
            clientActor.tell(AkkaMessage(userDto, command, counter), null)
            counter ++
        }, system.dispatcher())
        return system
    }




}
