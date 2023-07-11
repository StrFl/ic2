/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  buildcraft.api.statements.IStatementContainer
 *  buildcraft.api.statements.IStatementParameter
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.bcIntegration.core;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;
import ic2.api.item.ElectricItem;
import ic2.bcIntegration.core.Trigger;
import ic2.bcIntegration.core.TriggerType;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.block.machine.tileentity.TileEntityLathe;
import ic2.core.block.machine.tileentity.TileEntityStandardMachine;
import ic2.core.block.wiring.TileEntityElectricBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TriggerCapacitor
extends Trigger {
    public TriggerCapacitor(TriggerType type) {
        super(type);
    }

    @Override
    public String getDescription() {
        switch (this.type) {
            case CapacitorEmpty: {
                return "Capacitor Empty";
            }
            case CapacitorHasEnergy: {
                return "Capacitor Has Energy";
            }
            case CapacitorHasRoom: {
                return "Space For Energy";
            }
            case CapacitorFull: {
                return "Capacitor Full";
            }
            case ChargeEmpty: {
                return "Charging Empty Item";
            }
            case ChargePartial: {
                return "Charging Partially Charged Item";
            }
            case ChargeFull: {
                return "Charging Fully Charged Item";
            }
            case DischargeEmpty: {
                return "Discharging Empty Item";
            }
            case DischargePartial: {
                return "Discharging Partially Charged Item";
            }
            case DischargeFull: {
                return "Discharging Fully Charged Item";
            }
        }
        return "";
    }

    @Override
    public boolean isTriggerActive(TileEntity target, ForgeDirection side, IStatementContainer source, IStatementParameter[] parameters) {
        if (target == null) {
            return false;
        }
        if (target instanceof TileEntityStandardMachine) {
            TileEntityStandardMachine te = (TileEntityStandardMachine)target;
            boolean hasEnergy = te.energy >= (double)te.defaultEnergyConsume;
            boolean hasRoom = te.energy <= (double)(te.maxEnergy - te.defaultEnergyConsume);
            ItemStack item = te.dischargeSlot.get();
            switch (this.type) {
                case CapacitorEmpty: {
                    return !hasEnergy;
                }
                case CapacitorHasEnergy: {
                    return hasEnergy;
                }
                case CapacitorHasRoom: {
                    return hasRoom;
                }
                case CapacitorFull: {
                    return !hasRoom;
                }
                case DischargeEmpty: {
                    return item != null && !this.canDischarge(item);
                }
                case DischargePartial: {
                    return item != null && this.canDischarge(item) && this.canCharge(item);
                }
                case DischargeFull: {
                    return item != null && !this.canCharge(item);
                }
            }
            return false;
        }
        if (target instanceof TileEntityBaseGenerator) {
            TileEntityBaseGenerator te = (TileEntityBaseGenerator)target;
            boolean hasEnergy = te.storage > 0.0;
            boolean hasRoom = te.storage < (double)te.maxStorage;
            ItemStack item = te.chargeSlot.get();
            switch (this.type) {
                case CapacitorEmpty: {
                    return !hasEnergy;
                }
                case CapacitorHasEnergy: {
                    return hasEnergy;
                }
                case CapacitorHasRoom: {
                    return hasRoom;
                }
                case CapacitorFull: {
                    return !hasRoom;
                }
                case ChargeEmpty: {
                    return item != null && !this.canDischarge(item);
                }
                case ChargePartial: {
                    return item != null && this.canDischarge(item) && this.canCharge(item);
                }
                case ChargeFull: {
                    return item != null && !this.canCharge(item);
                }
            }
            return false;
        }
        if (target instanceof TileEntityElectricBlock) {
            TileEntityElectricBlock te = (TileEntityElectricBlock)target;
            boolean hasEnergy = te.energy >= (double)te.output;
            boolean hasRoom = te.energy < (double)te.maxStorage;
            ItemStack chargeItem = te.chargeSlot.get();
            ItemStack dischargeItem = te.dischargeSlot.get();
            switch (this.type) {
                case CapacitorEmpty: {
                    return !hasEnergy;
                }
                case CapacitorHasEnergy: {
                    return hasEnergy;
                }
                case CapacitorHasRoom: {
                    return hasRoom;
                }
                case CapacitorFull: {
                    return !hasRoom;
                }
                case ChargeEmpty: {
                    return chargeItem != null && !this.canDischarge(chargeItem);
                }
                case ChargePartial: {
                    return chargeItem != null && this.canDischarge(chargeItem) && this.canCharge(chargeItem);
                }
                case ChargeFull: {
                    return chargeItem != null && !this.canCharge(chargeItem);
                }
                case DischargeEmpty: {
                    return dischargeItem != null && !this.canDischarge(dischargeItem);
                }
                case DischargePartial: {
                    return dischargeItem != null && this.canDischarge(dischargeItem) && this.canCharge(dischargeItem);
                }
                case DischargeFull: {
                    return dischargeItem != null && !this.canCharge(dischargeItem);
                }
            }
            return false;
        }
        if (target instanceof TileEntityLathe) {
            TileEntityLathe te = (TileEntityLathe)target;
            boolean hasEnergy = te.kUBuffer > 0;
            boolean hasRoom = te.kUBuffer < 10000;
            switch (this.type) {
                case CapacitorEmpty: {
                    return !hasEnergy;
                }
                case CapacitorHasEnergy: {
                    return hasEnergy;
                }
                case CapacitorHasRoom: {
                    return hasRoom;
                }
                case CapacitorFull: {
                    return !hasRoom;
                }
            }
            return false;
        }
        return false;
    }

    private boolean canDischarge(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (item == null) {
            return false;
        }
        return ElectricItem.manager.getCharge(itemStack) > 0.0;
    }

    private boolean canCharge(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (item == null) {
            return false;
        }
        return ElectricItem.manager.charge(itemStack, Double.POSITIVE_INFINITY, Integer.MAX_VALUE, true, true) > 0.0;
    }
}

