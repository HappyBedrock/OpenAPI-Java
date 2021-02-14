package eu.happybe.openapi.api.form.types;

import eu.happybe.openapi.api.form.Form;
import io.gomint.gui.element.Dropdown;

public class CustomForm extends Form {

    protected io.gomint.gui.CustomForm ui;
    private int entryCount = 0;

    public CustomForm(String title) {
        this.ui = io.gomint.gui.CustomForm.create(title);
    }

    public CustomForm addInput(String text) {
        this.ui.input(String.valueOf(this.entryCount++), text, "", "");
        return this;
    }

    public CustomForm addLabel(String text) {
        this.ui.label(text);
        this.entryCount++;
        return this;
    }

    public CustomForm addToggle(String text, Boolean defaultValue) {
        this.ui.dropdown(String.valueOf(this.entryCount++), text); // TODO - Default value
        return this;
    }

    public CustomForm addToggle(String text) {
        return this.addToggle(text, null);
    }

    public CustomForm addDropdown(String text, String[] options, Integer defaultValue) {
        Dropdown dropdown = this.ui.dropdown(String.valueOf(this.entryCount++), text);
        int i = 0;
        for(String option : options) {
            if(defaultValue == i++) {
                dropdown.option(option, true);
                continue;
            }

            dropdown.option(option);
        }
        return this;
    }

    public CustomForm addDropdown(String text, String[] options) {
        return this.addDropdown(text, options, null);
    }
}
