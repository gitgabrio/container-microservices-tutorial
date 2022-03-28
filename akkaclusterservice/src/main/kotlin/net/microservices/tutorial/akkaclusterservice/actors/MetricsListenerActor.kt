package net.microservices.tutorial.akkaclusterservice.actors

import akka.actor.AbstractActor
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.CurrentClusterState
import akka.cluster.metrics.ClusterMetricsChanged
import akka.cluster.metrics.ClusterMetricsExtension
import akka.cluster.metrics.NodeMetrics
import akka.cluster.metrics.StandardMetrics
import akka.cluster.metrics.StandardMetrics.Cpu
import akka.cluster.metrics.StandardMetrics.HeapMemory
import akka.event.Logging
import akka.event.LoggingAdapter
import net.microservices.tutorial.akkaclusterservice.services.HomeService
import org.springframework.beans.factory.annotation.Autowired

class MetricsListenerActor(private val homeService: HomeService) : AbstractActor() {

    private var log = Logging.getLogger(context.system(), this)
    private var cluster = Cluster.get(context.system())
    private var extension = ClusterMetricsExtension.get(context.system())

    // Subscribe unto ClusterMetricsEvent events.
    override fun preStart() {
        extension.subscribe(self())
    }

    // Unsubscribe from ClusterMetricsEvent events.
    override fun postStop() {
        extension.unsubscribe(self())
    }

    override fun createReceive(): AbstractActor.Receive {
        return receiveBuilder().match(ClusterMetricsChanged::class.java) { clusterMetrics ->
            for (nodeMetrics in clusterMetrics.nodeMetrics) {
                if (nodeMetrics.address() == cluster.selfAddress()) {
                    logHeap(nodeMetrics)
                    logCpu(nodeMetrics)
                }
            }
        }.match(CurrentClusterState::class.java) { _ ->
            // Ignore.
        }.build()
    }

    private fun logHeap(nodeMetrics: NodeMetrics) {
        val heap = StandardMetrics.extractHeapMemory(nodeMetrics)
        if (heap != null) {
            val heapMemory = heap.used().toDouble() / 1024.0 / 1024.0
            homeService.heapMemory = heapMemory
        }
    }

    private fun logCpu(nodeMetrics: NodeMetrics) {
        val cpu = StandardMetrics.extractCpu(nodeMetrics)
        if (cpu != null && cpu.systemLoadAverage().isDefined) {
            homeService.processors = cpu.processors()
            homeService.loadAverage = cpu.systemLoadAverage().get() as Double
        }
    }

}
