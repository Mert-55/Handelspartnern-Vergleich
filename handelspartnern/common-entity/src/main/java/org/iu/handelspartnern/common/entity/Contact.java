package org.iu.handelspartnern.common.entity;

public class Contact {
    private String name;
    private String email;
    private String phone;
    private String role;

    public Contact() {
    }

    public Contact(String name, String email, String phone, String role) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    @Override
    public String toString() {
        return "Contact{" + "name='" + name + '\'' + ", email='" + email + '\'' + ", phone='" + phone + '\''
                + ", role='" + role + '\'' + '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
