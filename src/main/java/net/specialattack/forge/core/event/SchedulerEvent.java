package net.specialattack.forge.core.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.specialattack.util.Scheduler;

public abstract class SchedulerEvent extends Event {

    public final Scheduler scheduler;

    public SchedulerEvent(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public static class SchedulerCreated extends SchedulerEvent {

        public SchedulerCreated(Scheduler scheduler) {
            super(scheduler);
        }
    }

    public static class SchedulerRemoved extends SchedulerEvent {

        public SchedulerRemoved(Scheduler scheduler) {
            super(scheduler);
        }
    }
}
