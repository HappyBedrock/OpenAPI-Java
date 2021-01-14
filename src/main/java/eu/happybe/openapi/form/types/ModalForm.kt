package eu.happybe.openapi.form.types

import eu.happybe.openapi.form.Form

class ModalForm @JvmOverloads constructor(title: String? = "Form title", content: String? = "Form content") : Form() {
    fun setFirstButton(text: String?): ModalForm {
        data["button1"] = text
        return this
    }

    fun setSecondButton(text: String?): ModalForm {
        data["button2"] = text
        return this
    }

    init {
        data["type"] = "modal"
        data["title"] = title
        data["content"] = content
    }
}