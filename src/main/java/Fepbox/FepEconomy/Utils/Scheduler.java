package Fepbox.FepEconomy.Utils;

import org.bukkit.Bukkit;

import Fepbox.FepEconomy.FepEconomy;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

public class Scheduler {

    public static void runAsync(Runnable task) {
        Bukkit.getAsyncScheduler().runNow(FepEconomy.getPlugin(), t -> task.run());
    }

    public static void runSync(Runnable task) {
        Bukkit.getGlobalRegionScheduler().run(FepEconomy.getPlugin(), t -> task.run());
    }

    public static void runLater(Runnable task, long delayTicks) {
        Bukkit.getGlobalRegionScheduler().runDelayed(FepEconomy.getPlugin(), t -> task.run(), delayTicks);
    }

    public static ScheduledTask runAsyncTimer(Runnable task, long delayTicks, long periodTicks) {
        return Bukkit.getGlobalRegionScheduler().runAtFixedRate(
                FepEconomy.getPlugin(),
                t -> runAsync(task),
                delayTicks,
                periodTicks);
    }
}
