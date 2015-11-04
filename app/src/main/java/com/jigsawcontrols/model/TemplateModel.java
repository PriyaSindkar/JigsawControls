package com.jigsawcontrols.model;

/**
 * Created by Priya on 9/12/2015.
 */
public class TemplateModel {
    String templateId;
    String templateName;
    String serialNumber;

    public TemplateModel() {
    }

    public TemplateModel(String templateId, String templateName,String serialNumber) {
        this.templateId = templateId;
        this.templateName = templateName;
        this.serialNumber = serialNumber;
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

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
}
