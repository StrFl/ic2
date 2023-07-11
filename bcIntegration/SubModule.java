/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  buildcraft.api.statements.IStatementContainer
 *  buildcraft.api.statements.ITriggerExternal
 *  buildcraft.api.statements.ITriggerInternal
 *  buildcraft.api.statements.ITriggerProvider
 *  buildcraft.api.statements.StatementManager
 *  cpw.mods.fml.common.event.FMLInterModComms
 *  net.minecraft.block.Block
 *  net.minecraft.item.Item
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.bcIntegration;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.ITriggerExternal;
import buildcraft.api.statements.ITriggerInternal;
import buildcraft.api.statements.ITriggerProvider;
import buildcraft.api.statements.StatementManager;
import cpw.mods.fml.common.event.FMLInterModComms;
import ic2.bcIntegration.core.TriggerCapacitor;
import ic2.bcIntegration.core.TriggerEnergyFlow;
import ic2.bcIntegration.core.TriggerFuel;
import ic2.bcIntegration.core.TriggerHeat;
import ic2.bcIntegration.core.TriggerScrap;
import ic2.bcIntegration.core.TriggerType;
import ic2.bcIntegration.core.TriggerWork;
import ic2.core.Ic2Items;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.block.generator.tileentity.TileEntityGeoGenerator;
import ic2.core.block.generator.tileentity.TileEntitySemifluidGenerator;
import ic2.core.block.heatgenerator.tileentity.TileEntityFluidHeatGenerator;
import ic2.core.block.heatgenerator.tileentity.TileEntitySolidHeatGenerator;
import ic2.core.block.machine.tileentity.TileEntityBlastFurnace;
import ic2.core.block.machine.tileentity.TileEntityCentrifuge;
import ic2.core.block.machine.tileentity.TileEntityInduction;
import ic2.core.block.machine.tileentity.TileEntityLathe;
import ic2.core.block.machine.tileentity.TileEntityMatter;
import ic2.core.block.machine.tileentity.TileEntityStandardMachine;
import ic2.core.block.wiring.TileEntityCableDetector;
import ic2.core.block.wiring.TileEntityElectricBlock;
import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class SubModule
implements ITriggerProvider {
    public static TriggerCapacitor triggerCapacitorEmpty;
    public static TriggerCapacitor triggerCapacitorHasEnergy;
    public static TriggerCapacitor triggerCapacitorHasRoom;
    public static TriggerCapacitor triggerCapacitorFull;
    public static TriggerCapacitor triggerChargeEmpty;
    public static TriggerCapacitor triggerChargePartial;
    public static TriggerCapacitor triggerChargeFull;
    public static TriggerCapacitor triggerDischargeEmpty;
    public static TriggerCapacitor triggerDischargePartial;
    public static TriggerCapacitor triggerDischargeFull;
    public static TriggerWork triggerWorking;
    public static TriggerWork triggerNotWorking;
    public static TriggerEnergyFlow triggerEnergyFlowing;
    public static TriggerEnergyFlow triggerEnergyNotFlowing;
    public static TriggerFuel triggerHasFuel;
    public static TriggerFuel triggerNoFuel;
    public static TriggerScrap triggerHasScrap;
    public static TriggerScrap triggerNoScrap;
    public static TriggerHeat triggerFullHeat;
    public static TriggerHeat triggerNoFullHeat;

    public static boolean init() {
        triggerCapacitorEmpty = new TriggerCapacitor(TriggerType.CapacitorEmpty);
        triggerCapacitorHasEnergy = new TriggerCapacitor(TriggerType.CapacitorHasEnergy);
        triggerCapacitorHasRoom = new TriggerCapacitor(TriggerType.CapacitorHasRoom);
        triggerCapacitorFull = new TriggerCapacitor(TriggerType.CapacitorFull);
        triggerChargeEmpty = new TriggerCapacitor(TriggerType.ChargeEmpty);
        triggerChargePartial = new TriggerCapacitor(TriggerType.ChargePartial);
        triggerChargeFull = new TriggerCapacitor(TriggerType.ChargeFull);
        triggerDischargeEmpty = new TriggerCapacitor(TriggerType.DischargeEmpty);
        triggerDischargePartial = new TriggerCapacitor(TriggerType.DischargePartial);
        triggerDischargeFull = new TriggerCapacitor(TriggerType.DischargeFull);
        triggerWorking = new TriggerWork(TriggerType.Working);
        triggerNotWorking = new TriggerWork(TriggerType.NotWorking);
        triggerEnergyFlowing = new TriggerEnergyFlow(TriggerType.EnergyFlowing);
        triggerEnergyNotFlowing = new TriggerEnergyFlow(TriggerType.EnergyNotFlowing);
        triggerHasFuel = new TriggerFuel(TriggerType.HasFuel);
        triggerNoFuel = new TriggerFuel(TriggerType.NoFuel);
        triggerHasScrap = new TriggerScrap(TriggerType.HasScrap);
        triggerNoScrap = new TriggerScrap(TriggerType.NoScrap);
        triggerFullHeat = new TriggerHeat(TriggerType.FullHeat);
        triggerNoFullHeat = new TriggerHeat(TriggerType.NoFullHeat);
        StatementManager.registerTriggerProvider((ITriggerProvider)new SubModule());
        FMLInterModComms.sendMessage((String)"BuildCraft|Transport", (String)"add-facade", (String)(Block.getIdFromBlock((Block)Block.getBlockFromItem((Item)Ic2Items.bronzeBlock.getItem())) + "@" + Ic2Items.bronzeBlock.getItemDamage()));
        FMLInterModComms.sendMessage((String)"BuildCraft|Transport", (String)"add-facade", (String)(Block.getIdFromBlock((Block)Block.getBlockFromItem((Item)Ic2Items.copperBlock.getItem())) + "@" + Ic2Items.copperBlock.getItemDamage()));
        FMLInterModComms.sendMessage((String)"BuildCraft|Transport", (String)"add-facade", (String)(Block.getIdFromBlock((Block)Block.getBlockFromItem((Item)Ic2Items.tinBlock.getItem())) + "@" + Ic2Items.tinBlock.getItemDamage()));
        FMLInterModComms.sendMessage((String)"BuildCraft|Transport", (String)"add-facade", (String)(Block.getIdFromBlock((Block)Block.getBlockFromItem((Item)Ic2Items.uraniumBlock.getItem())) + "@" + Ic2Items.uraniumBlock.getItemDamage()));
        return true;
    }

    public Collection<ITriggerInternal> getInternalTriggers(IStatementContainer container) {
        return null;
    }

    public Collection<ITriggerExternal> getExternalTriggers(ForgeDirection side, TileEntity tile) {
        ArrayList<ITriggerExternal> temp = new ArrayList<ITriggerExternal>();
        if (tile instanceof TileEntityStandardMachine || tile instanceof TileEntityBaseGenerator || tile instanceof TileEntityElectricBlock || tile instanceof TileEntityLathe) {
            temp.add(triggerCapacitorEmpty);
            temp.add(triggerCapacitorHasEnergy);
            temp.add(triggerCapacitorHasRoom);
            temp.add(triggerCapacitorFull);
        }
        if (tile instanceof TileEntityBaseGenerator || tile instanceof TileEntityElectricBlock) {
            temp.add(triggerChargeEmpty);
            temp.add(triggerChargePartial);
            temp.add(triggerChargeFull);
        }
        if (tile instanceof TileEntityStandardMachine || tile instanceof TileEntityElectricBlock) {
            temp.add(triggerDischargeEmpty);
            temp.add(triggerDischargePartial);
            temp.add(triggerDischargeFull);
        }
        if (tile instanceof TileEntityStandardMachine || tile instanceof TileEntityBaseGenerator || tile instanceof TileEntityLathe) {
            temp.add(triggerWorking);
            temp.add(triggerNotWorking);
        }
        if (tile instanceof TileEntityBaseGenerator || tile instanceof TileEntityGeoGenerator || tile instanceof TileEntitySolidHeatGenerator || tile instanceof TileEntityFluidHeatGenerator || tile instanceof TileEntitySemifluidGenerator) {
            temp.add(triggerHasFuel);
            temp.add(triggerNoFuel);
        }
        if (tile instanceof TileEntityCableDetector) {
            temp.add(triggerEnergyFlowing);
            temp.add(triggerEnergyNotFlowing);
        }
        if (tile instanceof TileEntityMatter) {
            temp.add(triggerHasScrap);
            temp.add(triggerNoScrap);
        }
        if (tile instanceof TileEntityInduction || tile instanceof TileEntityCentrifuge || tile instanceof TileEntityBlastFurnace) {
            temp.add(triggerFullHeat);
            temp.add(triggerNoFullHeat);
        }
        return temp;
    }
}

