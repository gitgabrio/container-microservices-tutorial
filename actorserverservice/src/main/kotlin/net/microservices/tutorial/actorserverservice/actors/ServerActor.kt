@file:JvmName("ServerActor")
package net.microservices.tutorial.actorserverservice.actors

import akka.actor.UntypedActor
import java.lang.Exception
import java.util.logging.Logger

/**
 * Created by Gabriele Cardosi - gcardosi@cardosi.net on 07/05/17.
 */

open class ServerActor() : UntypedActor() {

    protected var logger = Logger.getLogger(ServerActor::class.java.simpleName)

    @Throws(Exception::class)
    override fun onReceive(o: Any) {
        if (o is String && !o.equals("OK")) {
            logger.info("Received: " + o)
            sender.tell("OK", self)
        } else {
            unhandled(o)
        }
    }

}
