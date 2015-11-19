package net.specialattack.util;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.specialattack.forge.core.Objects;
import net.specialattack.forge.core.event.SchedulerEvent;

public final class Scheduler {

    private static List<Scheduler> schedulers = new LinkedList<Scheduler>();

    public static void addScheduler(Scheduler scheduler) {
        Scheduler.schedulers.add(scheduler);
        Objects.MAIN_EVENT_BUS.post(new SchedulerEvent.SchedulerCreated(scheduler));
    }

    public static void removeScheduler(Scheduler scheduler) {
        Objects.MAIN_EVENT_BUS.post(new SchedulerEvent.SchedulerRemoved(scheduler));
        Scheduler.schedulers.remove(scheduler);
        scheduler.cleanup();
    }

    public static void tick(TickEvent.Type tickEventType) {
        for (Scheduler scheduler : Scheduler.schedulers) {
            if (scheduler.type.tickEventType == tickEventType) {
                scheduler.tick();
            }
        }
    }

    private PriorityQueue<Scheduler.Task> queue = new PriorityQueue<Scheduler.Task>(4, new Comparator<Scheduler.Task>() {
        @Override
        public int compare(Task o1, Task o2) {
            return (int) (o1.nextTick - o2.nextTick);
        }
    });

    private AtomicInteger nextTaskId = new AtomicInteger();
    private long currentTick = 0;
    public final Scheduler.Type type;

    public Scheduler(Scheduler.Type type) {
        this.type = type;
    }

    private void cleanup() {
        this.queue.clear();
        this.queue = null;
        this.nextTaskId = null;
    }

    public void tick() {
        this.currentTick++;
        while (!this.queue.isEmpty() && this.queue.peek().nextTick <= this.currentTick) {
            Task task = this.queue.poll();
            task.task.run();
            if (task.repeats) {
                task.nextTick = this.currentTick + task.period;
                this.queue.add(task);
            }
        }
    }

    /**
     * Schedules a task to be executed after a certain amount of ticks has passed.
     *
     * @param task
     *         The task to be executed.
     * @param delay
     *         The delay in ticks before the task should be executed.
     *
     * @return The ID of the scheduled task.
     */
    public int scheduleTask(Runnable task, long delay) {
        if (delay < 0L) {
            delay = 0L;
        }
        return this._scheduleTask(task, delay, false, 0L);
    }

    /**
     * Schedules a task to be executed after a certain amount of ticks has passed and repeated every amount of ticks.
     *
     * @param task
     *         The task to be executed.
     * @param delay
     *         The delay in ticks before the task should be executed.
     * @param period
     *         The time in ticks between executions of this task.
     *
     * @return The ID of the scheduled task.
     */
    public int scheduleRepeatingTask(Runnable task, long delay, long period) {
        if (delay < 0L) {
            delay = 0L;
        }
        if (period < 1L) {
            period = 1L;
        }
        return this._scheduleTask(task, delay, true, period);
    }

    /**
     * Removes a scheduled task from the scheduler.
     *
     * @param id
     *         The ID of the scheduled task to remove.
     */
    public void unscheduleTask(int id) {
        Iterator<Task> i = this.queue.iterator();
        while (i.hasNext()) {
            Task task = i.next();
            if (task.id == id) {
                i.remove();
                return;
            }
        }
    }

    private int _scheduleTask(Runnable task, long delay, boolean repeats, long period) {
        int id = this.nextTaskId.addAndGet(1);
        this.queue.add(new Scheduler.Task(id, this.currentTick + delay, task, repeats, period));
        return id;
    }

    public enum Type {
        CLIENT(TickEvent.Type.CLIENT), SERVER(TickEvent.Type.SERVER);

        public final TickEvent.Type tickEventType;

        Type(TickEvent.Type tickEventType) {
            this.tickEventType = tickEventType;
        }
    }

    private static class Task {

        private int id;
        private long nextTick;
        private long period;
        private Runnable task;
        private boolean repeats;

        public Task(int id, long nextTick, Runnable task, boolean repeats, long period) {
            this.id = id;
            this.nextTick = nextTick;
            this.task = task;
            this.repeats = repeats;
            this.period = period;
        }
    }

}
