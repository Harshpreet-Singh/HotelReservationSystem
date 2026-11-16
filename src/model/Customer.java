package model;

public class Customer {
    private int customerId;
    private String name, phone, email;

    public Customer(String name, String phone, String email) {
        this.name = name; this.phone = phone; this.email = email;
    }
    public Customer(int id, String name, String phone, String email) {
        this(name, phone, email); this.customerId = id;
    }
    public int getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
}