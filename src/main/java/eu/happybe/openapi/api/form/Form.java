package eu.happybe.openapi.api.form;

import io.gomint.gui.FormResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

public abstract class Form {

    protected io.gomint.gui.Form<FormResponse> ui;

    @Getter
    private Consumer<FormCallbackResponse> callback;
    @Getter @Setter
    private Object customData;

    public Form setCallable(Consumer<FormCallbackResponse> callback) {
        this.callback = callback;
        return this;
    }
}
