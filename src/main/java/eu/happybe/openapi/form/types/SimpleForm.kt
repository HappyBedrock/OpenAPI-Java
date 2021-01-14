package eu.happybe.openapi.form.types

import eu.happybe.openapi.form.Form
import java.util.*

class SimpleForm @JvmOverloads constructor(title: String? = "Form Title", content: String? = "Form Content") : Form() {
    fun addButton(text: String): SimpleForm {
        val buttonData: MutableMap<String, Any> = HashMap()
        buttonData["text"] = text
        (data["buttons"] as MutableList<Map<String, Any>?>).add(buttonData)
        return this
    }

    init {
        data["type"] = "form"
        data["title"] = title
        data["content"] = content
        data["buttons"] = ArrayList<Map<String, Any>>()
    }
}