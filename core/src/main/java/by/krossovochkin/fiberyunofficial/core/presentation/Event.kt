package by.krossovochkin.fiberyunofficial.core.presentation

open class Event<out T>(
    private val content: T
) {
    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}
