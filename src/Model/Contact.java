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
public class Contact {
    
    public int contactId;
    public String contactName;

    public Contact() {
        this.contactId = contactId;
        this.contactName = contactName;
    }

    public int getContactId() {
        return contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
    
    @Override
    public String toString() {
        return (contactName);
    }
}
