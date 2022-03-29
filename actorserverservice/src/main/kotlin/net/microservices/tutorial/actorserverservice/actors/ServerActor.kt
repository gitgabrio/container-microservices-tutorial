@file:JvmName("ServerActor")
package net.microservices.tutorial.actorserverservice.actors

import akka.actor.AbstractActor
import akka.cluster.Cluster
import akka.cluster.ClusterEvent
import akka.cluster.Member
import akka.cluster.MemberStatus
import akka.japi.pf.ReceiveBuilder
import net.microservices.tutorial.actorserverservice.services.UsersService
import net.microservices.tutorial.commands.Command
import net.microservices.tutorial.messages.AkkaMessage
import net.microservices.tutorial.messages.AkkaResponse
import net.microservices.tutorial.messages.ServerActorRegistration
import java.util.logging.Logger

/**
 * Created by Gabriele Cardosi - gcardosi@cardosi.net on 07/05/17.
 */

open class ServerActor(private val usersService: UsersService) : AbstractActor() {

    private var logger = Logger.getLogger(ServerActor::class.java.simpleName)

    private var cluster = Cluster.get(context.system())

    //subscribe to MemberUp events
    override fun preStart() {
        cluster.subscribe(self(), ClusterEvent.MemberUp::class.java)
    }

    //re-subscribe when restart
    override fun postStop() {
        cluster.unsubscribe(self())
    }

    override fun createReceive(): Receive {
        return ReceiveBuilder()
            .match(String::class.java) { s ->
                logger.info("Received $s")
                sender().tell("OK", self())
            }
            .match(AkkaMessage::class.java) {
                    message ->
                logger.info("Received $message")
                val response = executeCommand(message)
                logger.info("Sending $response")
                sender().tell(response, self())
            }
            .match(ClusterEvent.CurrentClusterState::class.java) {
                    state ->
                state.members
                    .filter {
                            member ->
                        member.status().equals(MemberStatus.up())
                    }
                    .forEach {
                            member ->
                        register(member)
                    }
            }
            .match(ClusterEvent.MemberUp::class.java) {
                    mUp -> register(mUp.member())
            }
            .build()
    }

    private fun executeCommand(message: AkkaMessage): AkkaResponse {
        var done: Boolean = false
        when (message.command) {
            Command.CREATE -> {
                if (message.user.id?.let { usersService.findByNumber(it) } == null) {
                    usersService.saveUser(message.user)
                    done = true
                }
            }
            Command.READ -> {
                if (message.user.id?.let { usersService.findByNumber(it) } != null) {
                    done = true
                }
            }
            Command.UPDATE -> {
                if (message.user.id?.let { usersService.findByNumber(it) } == null) {
                    usersService.updateUser(message.user)
                    done = true
                }
            }
            Command.DELETE -> {
                if (message.user.id?.let { usersService.findByNumber(it) } == null) {
                    usersService.deleteUser(message.user)
                    done = true
                }
            }
        }
        return AkkaResponse(done, message.id)
    }

    private fun register(member: Member) {
        if (member.hasRole("Client")) {
            context.actorSelection("${member.address()}/user/clientActor")
                    .tell(ServerActorRegistration(), self())
        }
    }


}
