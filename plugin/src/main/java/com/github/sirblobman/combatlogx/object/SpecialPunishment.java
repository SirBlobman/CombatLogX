package com.github.sirblobman.combatlogx.object;

import java.util.Collections;
import java.util.List;

import com.github.sirblobman.api.utility.Validate;

public final class SpecialPunishment {
    private final int minAmount;
    private final int maxAmount;
    private final boolean reset;
    private final List<String> commandList;
    
    public SpecialPunishment(int minAmount, int maxAmount, boolean reset, List<String> commandList) {
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.reset = reset;
    
        Validate.notEmpty(commandList, "commandList must not be empty!");
        this.commandList = Collections.unmodifiableList(commandList);
    }
    
    public int getMinAmount() {
        return this.minAmount;
    }
    
    public int getMaxAmount() {
        return this.maxAmount;
    }
    
    public boolean isReset() {
        return this.reset;
    }
    
    public List<String> getCommandList() {
        return this.commandList;
    }
}
