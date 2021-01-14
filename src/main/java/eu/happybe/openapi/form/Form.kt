package eu.happybe.openapi.form

import com.google.gson.Gson
import lombok.Getter
import lombok.Setter
import java.util.*
import java.util.function.Consumer

abstract class Form {
    @Getter
    private val data: Map<String, Any> = HashMap()

    @Getter
    private var callback: Consumer<FormCallbackResponse>? = null

    @Setter
    @Getter
    private val customData: Any? = null
    fun setCallable(callback: Consumer<FormCallbackResponse>?): Form {
        this.callback = callback
        return this
    }

    fun serialize(): String {
        return Gson().toJson(data)
    }
}