package com.jigsawcontrols.model;

/**
 * Created by Priya on 9/12/2015.
 */
public class TemplateModel {
    String templateId;
    String templateName;

    public TemplateModel() {
    }

    public TemplateModel(String templateId, String templateName) {
        this.templateId = templateId;
        this.templateName = templateName;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
}
