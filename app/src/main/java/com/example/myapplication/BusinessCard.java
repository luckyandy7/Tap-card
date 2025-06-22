package com.example.myapplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BusinessCard implements Serializable {
    private String name;
    private String phone;
    private String email;
    private String company;
    private String address;
    private List<String> tags;
    private long createdDate;
    private String imagePath;
    private int templateId;
    private String qrCode;
    private boolean isReceived; // 받은 명함인지 만든 명함인지 구분

    public BusinessCard() {
        this.tags = new ArrayList<>();
        this.createdDate = System.currentTimeMillis();
        this.isReceived = false;
    }

    public BusinessCard(String name, String phone, String email, String company, String address) {
        this();
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.company = company;
        this.address = address;
    }

    // Getters
    public String getName() {
        return name != null ? name : "";
    }

    public String getPhone() {
        return phone != null ? phone : "";
    }

    public String getEmail() {
        return email != null ? email : "";
    }

    public String getCompany() {
        return company != null ? company : "";
    }

    public String getAddress() {
        return address != null ? address : "";
    }

    public List<String> getTags() {
        return tags != null ? tags : new ArrayList<>();
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public String getImagePath() {
        return imagePath != null ? imagePath : "";
    }

    public int getTemplateId() {
        return templateId;
    }

    public String getQrCode() {
        return qrCode != null ? qrCode : "";
    }

    public boolean isReceived() {
        return isReceived;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTags(List<String> tags) {
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public void setReceived(boolean received) {
        isReceived = received;
    }

    // 유틸리티 메서드
    public void addTag(String tag) {
        if (tag != null && !tag.trim().isEmpty() && !this.tags.contains(tag.trim())) {
            this.tags.add(tag.trim());
        }
    }

    public void removeTag(String tag) {
        this.tags.remove(tag);
    }

    public void setTagsFromString(String tagsString) {
        this.tags.clear();
        if (tagsString != null && !tagsString.trim().isEmpty()) {
            String[] tagArray = tagsString.split(",");
            for (String tag : tagArray) {
                addTag(tag.trim());
            }
        }
    }

    public String getTagsAsString() {
        if (tags == null || tags.isEmpty()) {
            return "";
        }
        return String.join(", ", tags);
    }

    public boolean hasTag(String tag) {
        return tags != null && tags.contains(tag);
    }

    @Override
    public String toString() {
        return "BusinessCard{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", company='" + company + '\'' +
                ", address='" + address + '\'' +
                ", tags=" + tags +
                ", createdDate=" + createdDate +
                ", imagePath='" + imagePath + '\'' +
                ", templateId=" + templateId +
                ", isReceived=" + isReceived +
                '}';
    }
} 