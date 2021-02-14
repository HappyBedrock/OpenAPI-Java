package eu.happybe.openapi.api.form.types;

import eu.happybe.openapi.api.form.Form;
import io.gomint.gui.Modal;

public class ModalForm extends Form {

    private final Modal ui;

    public ModalForm(String title, String content) {
        this.ui = Modal.create(title, content);
    }

    public ModalForm(String title) {
        this(title, "Form content");
    }

    public ModalForm() {
        this("Form title");
    }

    public ModalForm setFirstButton(String text) {
        this.ui.trueText(text);

        return this;
    }

    public ModalForm setSecondButton(String text) {
        this.ui.falseText(text);

        return this;
    }
}
