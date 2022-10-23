package com.android.girish.vlog.filter

internal interface Criteria<T> {
    fun meetCriteria(input: List<T>): List<T>
    fun reset()
}
