package eu.happybe.openapi.form;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.ModalFormRequestPacket;
import cn.nukkit.network.protocol.ModalFormResponsePacket;
import eu.happybe.openapi.OpenAPI;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FormQueue {

    public static Map<String, Map<Integer, Form>> formQueue = new HashMap<>();

    public static void sendForm(Player player, Form form) {
        int formId = FormQueue.getWindowId(player);
        if(!FormQueue.formQueue.containsKey(player.getName())) {
            FormQueue.formQueue.put(player.getName(), new HashMap<>());
        }

        FormQueue.formQueue.get(player.getName()).put(formId, form);

        ModalFormRequestPacket pk = new ModalFormRequestPacket();
        pk.formId = formId;
        pk.data = form.serialize();

        player.dataPacket(pk);
    }

    private static int getWindowId(Player player) {
        int windowId;

        try {
            Field field = player.getClass().getDeclaredField("formWindowCount");
            field.setAccessible(true);

            windowId = field.getInt(player);
            field.setInt(player,windowId + 1);
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            OpenAPI.getInstance().getLogger().error(e.getMessage());
            e.printStackTrace();

            windowId = new Random().nextInt();
        }

        return windowId;
    }

    public static void handleQuit(Player player) {
        FormQueue.formQueue.remove(player.getName());
    }

    public static boolean handleFormDataPacket(Player player, ModalFormResponsePacket packet) {
        if(!FormQueue.formQueue.containsKey(player.getName())) {
            return false;
        }
        if(!FormQueue.formQueue.get(player.getName()).containsKey(packet.formId)) {
            return false;
        }

        Form form = FormQueue.formQueue.get(player.getName()).get(packet.formId);

        if(form.getCallback() != null && !packet.data.trim().equals("null")) { // Currently we aren't able to handle closing, maybe I will implement it later, but we don't use this as a functionality
            form.getCallback().accept(new FormCallbackResponse(player, form, packet.data));
        }

        FormQueue.formQueue.get(player.getName()).remove(packet.formId);
        return true;
    }
}