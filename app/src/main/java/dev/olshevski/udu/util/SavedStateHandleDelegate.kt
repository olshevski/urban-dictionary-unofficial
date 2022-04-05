@file:Suppress("unused")

package dev.olshevski.udu.util

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

inline fun <reified T> SavedStateHandle.delegate(key: String? = null): ReadWriteProperty<Any, T?> =
    object : ReadWriteProperty<Any, T?> {
        override fun getValue(thisRef: Any, property: KProperty<*>): T? {
            val stateKey = key ?: property.name
            return this@delegate[stateKey]
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
            val stateKey = key ?: property.name
            this@delegate[stateKey] = value
        }
    }

inline fun <reified T : Any> SavedStateHandle.stateFlow(key: String? = null) =
    ReadOnlyProperty<Any, MutableStateFlow<T?>> { _, property ->
        val stateKey = key ?: property.name
        val liveData = this@stateFlow.getLiveData<T>(stateKey)
        liveData.asMutableStateFlow(null)
    }

inline fun <reified T> SavedStateHandle.stateFlow(key: String? = null, initialValue: T) =
    ReadOnlyProperty<Any, MutableStateFlow<T>> { _, property ->
        val stateKey = key ?: property.name
        val liveData = this@stateFlow.getLiveData(stateKey, initialValue)
        liveData.asMutableStateFlow(initialValue)
    }

fun <T> MutableLiveData<T>.asMutableStateFlow(initialValue: T): MutableStateFlow<T> =
    MutableStateFlowAdapter(this, initialValue)

private class MutableStateFlowAdapter<T>(
    private val liveData: MutableLiveData<T>,
    initialValue: T
) : MutableStateFlow<T> {

    private val stateFlow: MutableStateFlow<T> = MutableStateFlow(initialValue)

    init {
        liveData.observeForever { stateFlow.value = it }
    }

    override var value: T
        get() = stateFlow.value
        set(value) {
            liveData.value = value
        }

    @InternalCoroutinesApi
    override suspend fun collect(collector: FlowCollector<T>) = stateFlow.collect(collector)

}