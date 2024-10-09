package gitlet;

import java.io.Serializable;
/** Represents a gitlet Branch object.
 *  @author mohamed abdellatif
 */
public class Branch implements Serializable {
    private String name;
    private String ID;
    boolean headOrNot;

    public Branch(String name, String ID, boolean headOrNot) {
        this.name = name;
        this.ID = ID;
        this.headOrNot = headOrNot;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setHeadOrNot(boolean headOrNot) {
        this.headOrNot = headOrNot;
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return ID;
    }

    public boolean getHeadOrNot() {
        return headOrNot;
    }
}
