package de.crafty.eiv.common.config.instances;

import de.crafty.eiv.common.config.AbstractEivConfig;

public class ClientConfig extends AbstractEivConfig {


    private boolean background = true;
    private boolean itemWrapMode = true;

    public ClientConfig() {
        super("client_settings");
    }


    public boolean drawBackground() {
        return this.background;
    }

    public void setDrawBackground(boolean background) {
        this.background = background;
    }

    public boolean isItemWrapMode() {
        return this.itemWrapMode;
    }

    public void setItemWrapMode(boolean itemWrapMode) {
        this.itemWrapMode = itemWrapMode;
    }

    @Override
    protected void loadData() {
        this.background = this.data().get("background").getAsBoolean();
        this.itemWrapMode = this.data().get("itemWrapMode").getAsBoolean();
    }

    @Override
    protected void saveData() {
        this.data().addProperty("background", this.background);
        this.data().addProperty("itemWrapMode", this.itemWrapMode);
    }
}
