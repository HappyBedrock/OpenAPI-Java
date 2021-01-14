package eu.happybe.openapi.form

import cn.nukkit.Player
import cn.nukkit.network.protocol.ModalFormRequestPacket
import cn.nukkit.network.protocol.ModalFormResponsePacket
import eu.happybe.openapi.OpenAPI
import java.util.*

object FormQueue {
    var formQueue: MutableMap<String, MutableMap<Int, Form>> = HashMap()
    fun sendForm(player: Player, form: Form) {
        val formId = getWindowId(player)
        if (!formQueue.containsKey(player.name)) {
            formQueue[player.name] = HashMap()
        }
        formQueue[player.name]!![formId] = form
        val pk = ModalFormRequestPacket()
        pk.formId = formId
        pk.data = form.serialize()
        player.dataPacket(pk)
    }

    private fun getWindowId(player: Player): Int {
        var windowId: Int
        try {
            val field = player.javaClass.getDeclaredField("formWindowCount")
            field.isAccessible = true
            windowId = field.getInt(player)
            field.setInt(player, windowId + 1)
        } catch (e: NoSuchFieldException) {
            OpenAPI.getInstance().logger.error(e.message)
            e.printStackTrace()
            windowId = Random().nextInt()
        } catch (e: IllegalAccessException) {
            OpenAPI.getInstance().logger.error(e.message)
            e.printStackTrace()
            windowId = Random().nextInt()
        }
        return windowId
    }

    fun handleQuit(player: Player) {
        formQueue.remove(player.name)
    }

    fun handleFormDataPacket(player: Player, packet: ModalFormResponsePacket): Boolean {
        if (!formQueue.containsKey(player.name)) {
            return false
        }
        if (!formQueue[player.name]!!.containsKey(packet.formId)) {
            return false
        }
        val form = formQueue[player.name]!![packet.formId]
        if (form.getCallback() != null && packet.data.trim { it <= ' ' } != "null") { // Currently we aren't able to handle closing, maybe I will implement it later, but we don't use this as a functionality
            form.getCallback().accept(FormCallbackResponse(player, form, packet.data))
        }
        formQueue[player.name]!!.remove(packet.formId)
        return true
    }
}