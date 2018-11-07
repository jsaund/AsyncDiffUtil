package com.experiments.jagsaund.asyncdiffutil

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.delay
import java.util.*
import java.util.concurrent.TimeUnit

class Repository {
    private val random: Random = Random()

    fun data(time: Long, unit: TimeUnit): ReceiveChannel<List<Item>> = produce(CommonPool) {
        while (isActive) {
            delay(time, unit)
            val items = (1..100)
                .map { Item(it, label(random), 0xFF000000.toInt() or random.nextInt(0xFFFFFF)) }
                .shuffled(random)
                .subList(0, random.nextInt(100))
            send(items)
        }
    }

    private inline fun label(random: Random): String {
        val c1 = (random.nextInt(25) + 'A'.toInt()).toChar()
        val c2 = (random.nextInt(25) + 'A'.toInt()).toChar()
        val c3 = (random.nextInt(25) + 'A'.toInt()).toChar()
        return String(charArrayOf(c1, c2, c3))
    }
}