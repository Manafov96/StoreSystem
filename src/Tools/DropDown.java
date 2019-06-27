/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tools;
/**
 *
 * @author Viko
 */
public class DropDown {

    private final int id;
    private String description;


    public DropDown(int id, String description) {
        this.id = id;
        this.description = description;
    }
    
    public DropDown(int id, String description, String code) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String name) {
        description = name;
    }
    

    @Override
    public String toString() {
        return description;
    }
}
