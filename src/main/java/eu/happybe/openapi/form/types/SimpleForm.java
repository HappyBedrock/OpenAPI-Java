package eu.happybe.openapi.form.types;

import eu.happybe.openapi.form.Form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleForm extends Form {

    public SimpleForm(String title, String content) {
        this.getData().put("type", "form");
        this.getData().put("title", title);
        this.getData().put("content", content);
        this.getData().put("buttons", new ArrayList<Map<String, Object>>());
    }

    public SimpleForm(String title) {
        this(title, "Form Content");
    }

    public SimpleForm() {
        this("Form Title");
    }

    public SimpleForm addButton(String text) {
        Map<String, Object> buttonData = new HashMap<>();
        buttonData.put("text", text);

        ((List<Map<String, Object>>)this.getData().get("buttons")).add(buttonData);
        return this;
    }
}
