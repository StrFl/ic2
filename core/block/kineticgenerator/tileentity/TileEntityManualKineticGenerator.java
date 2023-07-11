/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraftforge.common.util.ForgeDirection
 */
package ic2.core.block.kineticgenerator.tileentity;

import ic2.api.energy.tile.IKineticSource;
import ic2.core.block.TileEntityBlock;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import ic2.core.util.Util;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityManualKineticGenerator
extends TileEntityBlock
implements IKineticSource {
    public int clicks;
    public static final int maxClicksPerTick = 10;
    public final int maxKU = 1000;
    public int currentKU;
    private static final float outputModifier = Math.round(ConfigUtil.getFloat(MainConfig.get(), "balance/energy/kineticgenerator/manual"));

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        this.clicks = 0;
    }

    public boolean playerKlicked(EntityPlayer player) {
        if (player.getFoodStats().getFoodLevel() <= 6) {
            return false;
        }
        if (!(player instanceof EntityPlayerMP)) {
            return true;
        }
        if (this.clicks >= 10) {
            return true;
        }
        int ku = !Util.isFakePlayer(player, false) ? 400 : 20;
        ku = (int)((float)ku * outputModifier);
        this.currentKU += ku;
        if (this.currentKU > 1000) {
            this.currentKU = 1000;
        }
        player.addExhaustion(0.25f);
        ++this.clicks;
        return true;
    }

    @Override
    public int maxrequestkineticenergyTick(ForgeDirection directionFrom) {
        return this.currentKU;
    }

    @Override
    public int requestkineticenergy(ForgeDirection directionFrom, int requestkineticenergy) {
        int max = Math.min(this.currentKU, requestkineticenergy);
        this.currentKU -= max;
        return max;
    }
}

