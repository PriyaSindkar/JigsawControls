package com.jigsawcontrols.model;

/**
 * Created by Priya on 9/12/2015.
 */
public class Component {
    String componentName;
    String componentDetails;
    String componentPhoto;

    public Component() {
    }

    public Component(String componentName, String componentDetails, String componentPhoto) {
        this.componentName = componentName;
        this.componentDetails = componentDetails;
        this.componentPhoto = componentPhoto;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getComponentDetails() {
        return componentDetails;
    }

    public void setComponentDetails(String componentDetails) {
        this.componentDetails = componentDetails;
    }

    public String getComponentPhoto() {
        return componentPhoto;
    }

    public void setComponentPhoto(String componentPhoto) {
        this.componentPhoto = componentPhoto;
    }


    @Override
    public String toString() {
        return "Component{" +
                "componentName='" + componentName + '\'' +
                ", componentDetails='" + componentDetails + '\'' +
                ", componentPhoto='" + componentPhoto + '\'' +
                '}';
    }
}
