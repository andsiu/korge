package com.github.quillraven.fleks.benchmark

import kotlin.system.measureTimeMillis

fun main() {
    compareArtemisFleksSimple()
    compareArtemisFleksComplex()
}

/*
COMPLEX:
Artemis: max(787)    min(720)  avg(747.0)
Fleks:   max(877)    min(800)  avg(846.0)
 */
private fun compareArtemisFleksComplex() {
    val artemisTimes = mutableListOf<Long>()
    val artemisState = ArtemisStateComplex().apply { setup() }
    val artemisBm = ArtemisBenchmark()
    artemisBm.complex(artemisState)
    repeat(3) {
        artemisTimes.add(measureTimeMillis { artemisBm.complex(artemisState) })
    }

    val fleksTimes = mutableListOf<Long>()
    val fleksState = FleksStateComplex().apply { setup() }
    val fleksBm = FleksBenchmark()
    fleksBm.complex(fleksState)
    repeat(3) {
        fleksTimes.add(measureTimeMillis { fleksBm.complex(fleksState) })
    }

    println(
        """
            COMPLEX:
          Artemis: max(${artemisTimes.maxOrNull()})    min(${artemisTimes.minOrNull()})  avg(${artemisTimes.average()})
          Fleks:   max(${fleksTimes.maxOrNull()})    min(${fleksTimes.minOrNull()})  avg(${fleksTimes.average()})
      """.trimIndent()
    )
}

/*
SIMPLE:
Artemis: max(38)    min(31)  avg(33.666666666666664)
Fleks:   max(32)    min(31)  avg(31.333333333333332)
 */
private fun compareArtemisFleksSimple() {
    val artemisTimes = mutableListOf<Long>()
    val artemisState = ArtemisStateSimple().apply { setup() }
    val artemisBm = ArtemisBenchmark()
    artemisBm.simple(artemisState)
    repeat(3) {
        artemisTimes.add(measureTimeMillis { artemisBm.simple(artemisState) })
    }

    val fleksTimes = mutableListOf<Long>()
    val fleksState = FleksStateSimple().apply { setup() }
    val fleksBm = FleksBenchmark()
    fleksBm.simple(fleksState)
    repeat(3) {
        fleksTimes.add(measureTimeMillis { fleksBm.simple(fleksState) })
    }

    println(
        """
            SIMPLE:
          Artemis: max(${artemisTimes.maxOrNull()})    min(${artemisTimes.minOrNull()})  avg(${artemisTimes.average()})
          Fleks:   max(${fleksTimes.maxOrNull()})    min(${fleksTimes.minOrNull()})  avg(${fleksTimes.average()})
      """.trimIndent()
    )
}
