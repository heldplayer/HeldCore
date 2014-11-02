package net.specialattack.forge.core.sync;

import java.util.concurrent.Callable;
import org.apache.logging.log4j.Level;

public class PlayersOnlineChecker implements Runnable {

    private int exceptionCount = 0;

    @Override
    public void run() {
        while (true) {
            try {
                synchronized (SyncHandler.Server.playerSet) {
                    for (final PlayerTracker tracker : SyncHandler.Server.playerSet) {
                        if (tracker.getPlayer() == null) {
                            SyncHandler.Server.addDelayedTask(new Callable<Void>() {
                                @Override
                                public Void call() {
                                    SyncHandler.Server.stopTracking(tracker.uuid);
                                    return null;
                                }
                            });
                        }
                    }
                }

                Thread.sleep(100L);
            } catch (Exception e) {
                SyncHandler.Server.log.log(Level.ERROR, "Exception while running player online checks", e);
                exceptionCount++;
                if (exceptionCount > 10) {
                    SyncHandler.Server.log.log(Level.FATAL, "Too many exceptions! Terminating!");
                    SyncHandler.Server.terminateSynchronization();
                    break;
                }
            } catch (Throwable e) {
                SyncHandler.Server.log.log(Level.FATAL, "Error while running player online checks! Terminating!", e);
                SyncHandler.Server.terminateSynchronization();
                break;
            }
        }
    }

}
