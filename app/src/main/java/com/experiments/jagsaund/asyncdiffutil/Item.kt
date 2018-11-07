package com.experiments.jagsaund.asyncdiffutil

data class Item(val id: Int, val label: String, val color: Int) {
    fun isSame(other: Item): Boolean {
        return id == other.id
    }

    fun isContentSame(other: Item): Boolean {
        return this === other
    }
}
