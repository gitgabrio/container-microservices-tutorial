@file:JvmName("ClientActor")

package net.microservices.tutorial.actorclientservice.actors

import akka.actor.*
import akka.japi.pf.ReceiveBuilder
import akka.remote.*
import com.netflix.appinfo.InstanceInfo
import com.netflix.discovery.EurekaClient
import com.netflix.discovery.shared.Application
import net.microservices.tutorial.classes.notNull
import net.microservices.tutorial.messages.AkkaMessage
import net.microservices.tutorial.messages.AkkaResponse
import scala.PartialFunction
import scala.concurrent.duration.Duration
import scala.runtime.BoxedUnit
import java.util.*
import java.util.concurrent.TimeUnit.SECONDS
import java.util.logging.Logger

/**
 * Created by Gabriele Cardosi - gcardosi@cardosi.net on 07/05/17.
 */

open class ClientActor(private val eurekaClient: EurekaClient, private val actorServerServiceName: String) : AbstractActor() {

    protected var logger: Logger = Logger.getLogger(ClientActor::class.java.simpleName)

    init {
        sendIdentifyRequest()
    }

    private var remoteActor: ActorRef? = null

    private val pendingMessages: MutableMap<Int, AkkaMessage> = HashMap()

    private var active: Receive =
        ReceiveBuilder()
            .match(AkkaMessage::class.java) { s ->
                logger.info("Received $s")
                remoteActor?.tell(s, self())
                pendingMessages[s.id] = s
            }
            .match(AkkaResponse::class.java) { s ->
                logger.info("Received $s")
                pendingMessages.remove(s.id)
                logger.info("We still have ${pendingMessages.size} pending messages")
            }
            .match(Terminated::class.java) {
                logger.warning("ActorServer terminated")
                context.unwatch(remoteActor)
                context.become(inactive, true)
                sendIdentifyRequest()
            }
            .match(ReceiveTimeout::class.java) {
                logger.warning("ReceiveTimeout")
            }
            .match(DeadLetter::class.java) {
                deadLetter ->
                logger.warning("DeadLetter ${deadLetter.message()}" )
            }
            .match(DisassociatedEvent::class.java) {
                disassociatedEvent ->
                logger.warning("DisassociatedEvent ${disassociatedEvent.remoteAddress}")
                logger.info("unbecome ${self()}")
                context.unwatch(remoteActor)
                context.become(inactive, true)
                sendIdentifyRequest()
            }
            .build()

    private var inactive: Receive  =
        ReceiveBuilder().match(ActorIdentity::class.java) {
                identity ->
                remoteActor = identity.ref
                if (remoteActor == null) {
                    logger.warning("Remote actor not available: " + identity.correlationId())
                } else {
                    context.watch(remoteActor)
                    context.become(active, true)
                }
            }
            .match(AssociatedEvent::class.java) {
                associatedEvent ->
                logger.warning("AssociatedEvent ${associatedEvent.remoteAddress}")
            }
            .match(ReceiveTimeout::class.java) {
                x ->
                logger.warning("ReceiveTimeout : $x")
                sendIdentifyRequest()
            }
            .build()

    override fun unhandled(message: Any?) {
        logger.info("Unhandled  " + message?.javaClass.toString())
        super.unhandled(message)
    }

    override fun preStart() {
        context.system().eventStream().subscribe(self(), DisassociatedEvent::class.java)
        context.system().eventStream().subscribe(self(), DeadLetter::class.java)
    }

    override fun createReceive(): Receive {
        return inactive
    }

    fun sendIdentifyRequest() {
        val actorServerApplication: Application? = eurekaClient.getApplication(actorServerServiceName)
        actorServerApplication?.shuffleAndStoreInstances(true)
        val instances: List<InstanceInfo>? = actorServerApplication?.instances
        var instanceInfo: InstanceInfo? = null
        if (instances != null && instances.isNotEmpty()) {
            instanceInfo = instances[0]
        }
        val ipAddr: String? = instanceInfo?.ipAddr
        val servicePort: String? = instanceInfo?.metadata?.get("port")
        if (notNull(ipAddr, servicePort)) {
            val serviceUrl = "$ipAddr:$servicePort"
            val path = "akka.tcp://RemoteWorkerSystem@$serviceUrl/user/serverActor"
            logger.info("Sending Identify Request to $path")
            context.actorSelection(path).tell(Identify(path), self())
            context.system().scheduler()
                    .scheduleOnce(Duration.create(5, SECONDS), self(),
                            ReceiveTimeout.getInstance(), context.dispatcher(), self())
        } else {
            logger.warning("Cannot found remote path for $actorServerServiceName; retry in 5 seconds...")
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    logger.info("Retry....")
                    sendIdentifyRequest()
                }
            }, 5000)
        }
    }

}
