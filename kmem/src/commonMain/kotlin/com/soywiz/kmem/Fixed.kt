package com.soywiz.kmem

/**
 * Fixed point class, to handle decimal values with a fixed precision.
 *
 * Floating points have more precision in the range [0, 1] and losses decimal precision outside that range.
 * This fixed point won't lose precision in any integer range.
 */
inline class Fixed private constructor(val value: Int) : Comparable<Fixed> {
    companion object {
        const val SCALE_DIGITS = 3
        const val SCALE = 1000

        operator fun invoke(value: Int, unit: Unit = Unit): Fixed = Fixed((value * SCALE))
        operator fun invoke(value: Double): Fixed = Fixed((value * SCALE).toInt())
        operator fun invoke(value: String): Fixed {
            val int = value.substringBefore('.')
            val dec = value.substringAfter('.', "0")
            return Fixed(int.toInt() * SCALE + (dec.toInt() % SCALE))
        }
    }

    operator fun unaryPlus(): Fixed = this
    operator fun unaryMinus(): Fixed = Fixed(-value)
    operator fun plus(other: Fixed): Fixed = Fixed(value + other.value)
    operator fun minus(other: Fixed): Fixed = Fixed(value - other.value)
    // @TODO: We should do this without casting to double to avoid precision issues. Maybe we can multiply integer part and then decimal part separately and add them
    operator fun times(other: Fixed): Fixed = Fixed((value * other.toDouble()).toInt())
    operator fun div(other: Fixed): Fixed = Fixed((value / other.toDouble()).toInt())
    override fun compareTo(other: Fixed): Int = this.value.compareTo(other.value)

    fun toDouble(): Double = value.toDouble() / SCALE
    fun toFloat(): Float = toDouble().toFloat()
    fun toLong(): Long = toInt().toLong()
    fun toInt(): Int = value / SCALE

    override fun toString(): String {
        val str = "$value"
        return when {
            str.length <= SCALE_DIGITS -> "0.$str"
            else -> str.substring(0, str.length - SCALE_DIGITS) + "." + str.substring(str.length - SCALE_DIGITS)
        }.trimEnd('0').trimEnd('.')
    }
}

val String.fixed: Fixed get() = Fixed(this)
val Long.fixed: Fixed get() = Fixed(this.toInt())
val Int.fixed: Fixed get() = Fixed(this)
val Double.fixed: Fixed get() = Fixed(this)
val Float.fixed: Fixed get() = Fixed(this.toDouble())
inline val Number.fixed: Fixed get() = Fixed(this.toDouble())
