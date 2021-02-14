package eu.happybe.openapi.api.form;

import eu.happybe.openapi.api.form.types.ModalForm;
import eu.happybe.openapi.api.form.types.SimpleForm;
import io.gomint.entity.EntityPlayer;
import lombok.Getter;

import java.util.Set;

public class FormCallbackResponse {

    @Getter
    private final EntityPlayer player;
    @Getter
    private final Form form;

    @Getter
    private Set jsonData;

    @Getter
    private int buttonClicked = -1;

    public FormCallbackResponse(EntityPlayer player, Form form, String response) {
        this.player = player;
        this.form = form;

        if(form instanceof SimpleForm) {
            this.buttonClicked = Integer.parseInt(response.trim());
        }
        else if (form instanceof ModalForm) {
            this.buttonClicked = response.trim().equals("true") ? 0 : 1; // 1 even if the form is closed using esc WTF mojang
        }
        else {
            System.out.println(response);
            try {
                this.jsonData = (new Gson()).fromJson(response, jsonData.getClass());
                if(this.jsonData.size() == 0) {
                    this.jsonData = null;
                }
            }
            catch (Exception ignored) {
                this.jsonData = null;
            }
        }
    }
}
