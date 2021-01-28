import java.util.*;

public final class EventScheduler
{
    public PriorityQueue<Event> eventQueue;
    public Map<Entity, List<Event>> pendingEvents;
    public double timeScale;

    public EventScheduler(double timeScale) {
        this.eventQueue = new PriorityQueue<>(new EventComparator());
        this.pendingEvents = new HashMap<>();
        this.timeScale = timeScale;
    }

    public void scheduleEvent(
            Entity entity,
            Action action,
            long afterPeriod)
    {
        long time = System.currentTimeMillis() + (long)(afterPeriod
                * timeScale);
        Event event = new Event(action, time, entity);

        eventQueue.add(event);

        // update list of pending events for the given entity
        List<Event> pending = pendingEvents.getOrDefault(entity,
                new LinkedList<>());
        pending.add(event);
        pendingEvents.put(entity, pending);
    }

    public void unscheduleAllEvents(Entity entity)
    {
        List<Event> pending = pendingEvents.remove(entity);

        if (pending != null) {
            for (Event event : pending) {
                eventQueue.remove(event);
            }
        }
    }

    public void removePendingEvent(Event event)
    {
        List<Event> pending = this.pendingEvents.get(event.entity);

        if (pending != null) {
            pending.remove(event);
        }
    }

    public void updateOnTime(long time) {
        while (!eventQueue.isEmpty()
                && eventQueue.peek().time < time) {
            Event next = eventQueue.poll();

            removePendingEvent(next);

            next.action.executeAction(this);
        }
    }
}
