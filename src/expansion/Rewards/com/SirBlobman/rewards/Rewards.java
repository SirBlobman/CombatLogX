package com.SirBlobman.rewards;

import com.SirBlobman.combatlogx.expansion.CLXExpansion;

public class Rewards implements CLXExpansion {
    @Override
    public void enable() {
        
    }
    
    public String getUnlocalizedName() {return getName();}
    public String getName() {return "Rewards";}
    public String getVersion() {return "0.0.1";}
    
    
}