package com.paprika.utils.generator

import com.paprika.utils.database.BaseIntEntity
import kotlin.math.roundToInt

abstract class Generator<T : BaseIntEntity>(count: Int, clearOld: Boolean = true) {
    protected abstract val generated: List<T>
    protected var sequence: Sequence<Int> = sequenceOf()

    init {
        this.sequence = generateSequence(1) { it + 1 }.takeWhile { it < count }
    }

    protected fun <T : Number> getRandomValue(min: Int, max: Int, float: Boolean = true): T {
        val randomized = min + Math.random() * (max - min)
        if (float)
            return randomized as T
        else
            return randomized.roundToInt() as T
    }
}