@file:JvmName("ActorServerConfiguration")

package net.microservices.tutorial.actorserverservice.configurations


import akka.actor.ActorSystem
import akka.actor.Props
import com.netflix.discovery.EurekaClient
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigValueFactory
import net.microservices.tutorial.actorserverservice.actors.ServerActor
import net.microservices.tutorial.actorserverservice.controllers.HomeController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 * Created by Gabriele Cardosi - gcardosi@cardosi.net on 04/11/2016.
 */
@Configuration
@EnableDiscoveryClient
@ComponentScan("net.microservices.tutorial.actorserverservice")
open class ActorServerConfiguration {

    @Value("\${eureka.instance.metadata-map.port}")
    var akkaPort: Int = 0

//    @Value("\${akka.remote.netty.tcp.hostname}")
//    var akkaHostName: String = ""

    @Autowired
    private val eurekaClient: EurekaClient? = null



    @Bean
    open fun actorSystem(): ActorSystem {
        val hostName = eurekaClient?.applicationInfoManager?.info?.ipAddr ?: "hostname"
        val defaultApplication: Config = ConfigFactory.defaultApplication()
                .withValue("akka.remote.netty.tcp.hostname", ConfigValueFactory.fromAnyRef(hostName))
                .withValue("akka.remote.netty.tcp.port", ConfigValueFactory.fromAnyRef(akkaPort))
        val system = ActorSystem.create("RemoteWorkerSystem", defaultApplication)
        system.actorOf(Props.create(ServerActor::class.java), "serverActor")
        return system
    }

    @Bean
    open fun homeController(): HomeController {
        return HomeController()
    }

}
