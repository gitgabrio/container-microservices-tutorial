@file:JvmName("ServerActor")
package net.microservices.tutorial.actorserverservice.actors

import akka.actor.AbstractActor
import akka.japi.pf.ReceiveBuilder
import net.microservices.tutorial.commands.Command
import net.microservices.tutorial.dto.UserDTO
import net.microservices.tutorial.messages.AkkaMessage
import net.microservices.tutorial.messages.AkkaResponse
import scala.PartialFunction
import scala.runtime.BoxedUnit
import java.util.logging.Logger

/**
 * Created by Gabriele Cardosi - gcardosi@cardosi.net on 07/05/17.
 */

open class ServerActor : AbstractActor() {

    private var logger = Logger.getLogger(ServerActor::class.java.simpleName)

    private val users: MutableMap<Int, UserDTO> = HashMap()

    override fun createReceive(): Receive {
        return ReceiveBuilder().match(String::class.java) { s ->
                logger.info("Received $s")
                sender().tell("OK", self())
            }
            .match(AkkaMessage::class.java) { message ->
                logger.info("Received $message")
                val response = executeCommand(message)
                logger.info("Sending $response")
                sender().tell(response, self())
            }
            .build()
    }

    private fun executeCommand(message: AkkaMessage): AkkaResponse {
        var done: Boolean = false
        when (message.command) {
            Command.CREATE -> {
                if (!users.contains(message.user.id)) {
                    users[message.user.id!!] = message.user
                    done = true
                }
            }
            Command.READ -> {
                if (users.contains(message.user.id)) {
                    done = true
                }
            }
            Command.UPDATE -> {
                if (users.contains(message.user.id)) {
                    done = true
                }
            }
            Command.DELETE -> {
                if (users.contains(message.user.id)) {
                    users.remove(message.user.id!!)
                    done = true
                }
            }
        }
        return AkkaResponse(done, message.id)
    }


}
