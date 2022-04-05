package ru.spbstu.common.model

sealed class EventState {
    object Initial : EventState()
    object Progress : EventState()
    object Success : EventState()
    data class Failure(val error: EventError) : EventState()
}
