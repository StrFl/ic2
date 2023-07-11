/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.CommandBase
 *  net.minecraft.command.ICommand
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 */
package ic2.core.command;

import ic2.core.IC2;
import ic2.core.TickrateTracker;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class CommandTps
extends CommandBase {
    public String getCommandName() {
        return "tps";
    }

    public String getCommandUsage(ICommandSender icommandsender) {
        return "/tps";
    }

    public void processCommand(ICommandSender sender, String[] args) {
        TickrateTracker tt = IC2.getInstance().tickrateTracker;
        EntityPlayerMP player = CommandTps.getCommandSenderAsPlayer((ICommandSender)sender);
        double periodTotal = tt.getLastTotal();
        double avgTotal = tt.getAvgTotal();
        double minTotal = tt.getMinTotal();
        double maxTotal = tt.getMaxTotal();
        double periodTick = tt.getLastTick();
        double avgTick = tt.getAvgTick();
        double minTick = tt.getMinTick();
        double maxTick = tt.getMaxTick();
        String msg = String.format("%.1f tps", 1000.0 / periodTotal);
        String msg2 = String.format("total period %.1f ms (avg %.1f ms, min %.1f ms, max %.1f ms)", periodTotal, avgTotal, minTotal, maxTotal);
        String msg3 = String.format("active period %.1f ms (avg %.1f ms, min %.1f ms, max %.1f ms)", periodTick, avgTick, minTick, maxTick);
        IC2.platform.messagePlayer((EntityPlayer)player, msg, new Object[0]);
        IC2.platform.messagePlayer((EntityPlayer)player, msg2, new Object[0]);
        IC2.platform.messagePlayer((EntityPlayer)player, msg3, new Object[0]);
    }

    public int compareTo(Object o) {
        return super.compareTo((ICommand)o);
    }
}

