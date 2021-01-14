package eu.happybe.openapi.form.types

import eu.happybe.openapi.form.Form
import java.util.*

class CustomForm(title: String?) : Form() {
    fun addInput(text: String): CustomForm {
        val input: MutableMap<String, Any> = HashMap()
        input["type"] = "input"
        input["text"] = text
        (data["content"] as MutableList<Map<String, Any>?>).add(input)
        return this
    }

    fun addLabel(text: String): CustomForm {
        val label: MutableMap<String, Any> = HashMap()
        label["type"] = "label"
        label["text"] = text
        (data["content"] as MutableList<Map<String, Any>?>).add(label)
        return this
    }

    @JvmOverloads
    fun addToggle(text: String?, defaultValue: Boolean? = null): CustomForm {
        val toggle: MutableMap<String, Any?> = HashMap()
        toggle["type"] = "toggle"
        toggle["text"] = text
        if (defaultValue != null) {
            toggle["default"] = defaultValue
        }
        (data["content"] as MutableList<Map<String, Any?>?>).add(toggle)
        return this
    }

    @JvmOverloads
    fun addDropdown(text: String, options: Array<String?>, defaultValue: Int? = null): CustomForm {
        val dropdown: MutableMap<String, Any> = HashMap()
        dropdown["type"] = "dropdown"
        dropdown["text"] = text
        dropdown["options"] = options
        if (defaultValue != null) {
            dropdown["default"] = defaultValue
        }
        (data["content"] as MutableList<Map<String, Any>?>).add(dropdown)
        return this
    }

    init {
        data["type"] = "custom_form"
        data["title"] = title
        data["content"] = ArrayList<Map<String, Any>>()
    }
}