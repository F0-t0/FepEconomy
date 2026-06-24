package Fepbox.FepEconomy.MenuManager;

import org.bukkit.entity.Player;

public class DataManger {
    //Define variables to transfer
    // from one GUI to another vvvv
    // private String playerName; for example

    private Player owner;

    public Player getTarget() {
        return target;
    }

    public void setTarget(Player target) {
        this.target = target;
    }

    private Player target;

    public DataManger(Player p) {
        this.owner = p;
    }

    public Player getOwner() {
        return owner;
    }

    //Define gettters and setters for the variables vv
}
