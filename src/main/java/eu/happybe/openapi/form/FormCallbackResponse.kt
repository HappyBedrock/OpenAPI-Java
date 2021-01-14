package eu.happybe.openapi.form

import cn.nukkit.Player
import com.google.gson.Gson
import eu.happybe.openapi.form.types.ModalForm
import eu.happybe.openapi.form.types.SimpleForm
import lombok.Getter

class FormCallbackResponse(@field:Getter private val player: Player, @field:Getter private val form: Form?, response: String) {
    @Getter
    private var jsonData: Set<*>? = null

    @Getter
    private var buttonClicked = -1

    init {
        if (form is SimpleForm) {
            buttonClicked = response.trim { it <= ' ' }.toInt()
        } else if (form is ModalForm) {
            buttonClicked = if (response.trim { it <= ' ' } == "true") 0 else 1 // 1 even if the form is closed using esc WTF mojang
        } else {
            println(response)
            try {
                jsonData = Gson().fromJson(response, jsonData!!.javaClass)
                if (jsonData.size == 0) {
                    jsonData = null
                }
            } catch (ignored: Exception) {
                jsonData = null
            }
        }
    }
}