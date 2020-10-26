package eu.happybe.openapi.form.types;

import eu.happybe.openapi.form.Form;

public class ModalForm extends Form {

    public ModalForm(String title, String content) {
        this.getData().put("type", "modal");
        this.getData().put("title", title);
        this.getData().put("content", content);
    }

    public ModalForm(String title) {
        this(title, "Form content");
    }

    public ModalForm() {
        this("Form title");
    }

    public ModalForm setFirstButton(String text) {
        this.getData().put("button1", text);

        return this;
    }

    public ModalForm setSecondButton(String text) {
        this.getData().put("button2", text);

        return this;
    }
}
