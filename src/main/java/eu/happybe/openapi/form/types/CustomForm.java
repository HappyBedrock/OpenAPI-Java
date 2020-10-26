package eu.happybe.openapi.form.types;

import eu.happybe.openapi.form.Form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomForm extends Form {

    public CustomForm(String title) {
       this.getData().put("type", "custom_form");
       this.getData().put("title", title);
       this.getData().put("content", new ArrayList<Map<String, Object>>());
    }

    public CustomForm addInput(String text) {
        Map<String, Object> input = new HashMap<>();
        input.put("type", "input");
        input.put("text", text);

        ((List<Map<String, Object>>)this.getData().get("content")).add(input);
        return this;
    }

    public CustomForm addLabel(String text) {
        Map<String, Object> label = new HashMap<>();
        label.put("type", "label");
        label.put("text", text);

        ((List<Map<String, Object>>)this.getData().get("content")).add(label);
        return this;
    }

    public CustomForm addToggle(String text, Boolean defaultValue) {
        Map<String, Object> toggle = new HashMap<>();

        toggle.put("type", "toggle");
        toggle.put("text", text);
        if(defaultValue != null) {
            toggle.put("default", defaultValue);
        }

        ((List<Map<String, Object>>)this.getData().get("content")).add(toggle);
        return this;
    }

    public CustomForm addToggle(String text) {
        return this.addToggle(text, null);
    }

    public CustomForm addDropdown(String text, String[] options, Integer defaultValue) {
        Map<String, Object> dropdown = new HashMap<>();

        dropdown.put("type", "dropdown");
        dropdown.put("text", text);
        dropdown.put("options", options);
        if(defaultValue != null) {
            dropdown.put("default", defaultValue);
        }

        ((List<Map<String, Object>>)this.getData().get("content")).add(dropdown);
        return this;
    }

    public CustomForm addDropdown(String text, String[] options) {
        return this.addDropdown(text, options, null);
    }
}
