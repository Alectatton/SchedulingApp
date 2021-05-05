/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 *
 * @author alect
 */
public class Customer {
    
    public int customerId;
    public String customerName;
    public String customerAddress;
    public String customerPostal;
    public String customerPhone;
    public String customerCountry;
    public String customerDivision;
    public int customerDivisionId;

    //Constructor 
    public Customer() {
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerPostal = customerPostal;
        this.customerPhone = customerPhone;
        this.customerCountry = customerCountry;
        this.customerDivision = customerDivision;
        this.customerDivisionId = customerDivisionId;
    }

    //Getters
    public int getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public String getCustomerPostal() {
        return customerPostal;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }
    
    public String getCustomerCountry() {
        return customerCountry;
    }
    
    public String getCustomerDivision() {
        return customerDivision;
    }
    
    public int getCustomerDivisionId() {
        return customerDivisionId;
    }
    
    //Setters
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public void setCustomerPostal(String customerPostal) {
        this.customerPostal = customerPostal;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }       
    
    public void setCustomerCountry(String customerCountry) {
        this.customerCountry = customerCountry;
    }
    
    public void setCustomerDivision(String customerDivision) {
        this.customerDivision = customerDivision;
    }
    
    public void setCustomerDivisionId(int customerDivisionId) {
        this.customerDivisionId = customerDivisionId;
    }
    
    @Override
    public String toString() {
        return (customerName);
    }
}
