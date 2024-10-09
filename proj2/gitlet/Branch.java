package gitlet;

import java.io.Serializable;
/** Represents a gitlet Branch object.
 *  @author mohamed abdellatif
 */
public class Branch implements Serializable {
    private String name;
    private String I;
    boolean headOrNot;

    public Branch(String name, String I, boolean headOrNot) {
        this.name = name;
        this.I = I;
        this.headOrNot = headOrNot;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setID(String I) {
        this.I = I;
    }

    public void setHeadOrNot(boolean headOrNot) {
        this.headOrNot = headOrNot;
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return I;
    }

    public boolean getHeadOrNot() {
        return headOrNot;
    }
}
