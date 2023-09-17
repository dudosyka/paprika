package com.paprika.dto

data class UploadedDataDto (
    var dishes: ByteArray,
    var ingredients: ByteArray,
    var dishToIngredient: ByteArray,
    var measures: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UploadedDataDto

        if (!dishes.contentEquals(other.dishes)) return false
        if (!ingredients.contentEquals(other.ingredients)) return false
        return dishToIngredient.contentEquals(other.dishToIngredient)
    }

    override fun hashCode(): Int {
        var result = dishes.contentHashCode()
        result = 31 * result + ingredients.contentHashCode()
        result = 31 * result + dishToIngredient.contentHashCode()
        return result
    }
}