package eu.happybe.openapi.form;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class Form {

    @Getter
    private final Map<String, Object> data = new HashMap<>();
    @Getter
    private Consumer<FormCallbackResponse> callback;

    @Setter @Getter
    private Object customData;

    public Form setCallable(Consumer<FormCallbackResponse> callback) {
        this.callback = callback;
        return this;
    }

    public String serialize() {
        return new Gson().toJson(this.data);
    }
}
