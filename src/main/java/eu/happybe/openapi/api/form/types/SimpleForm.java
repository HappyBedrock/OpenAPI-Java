package eu.happybe.openapi.api.form.types;

import eu.happybe.openapi.api.form.Form;
import io.gomint.gui.ButtonList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleForm extends Form {

    protected ButtonList ui;
    private int buttonCount = 0;

    public SimpleForm(String title, String content) {
        this.ui = ButtonList.create(title);
        this.ui.content(content);
    }

    public SimpleForm(String title) {
        this(title, "Form Content");
    }

    public SimpleForm() {
        this("Form Title");
    }

    public SimpleForm addButton(String text) {
        this.ui.button(String.valueOf(this.buttonCount++), text);
        return this;
    }
}
