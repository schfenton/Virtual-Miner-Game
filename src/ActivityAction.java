public final class ActivityAction implements Action
{
    private Actionable entity;
    private WorldModel world;
    private ImageStore imageStore;

    public ActivityAction(
            Actionable entity,
            WorldModel world,
            ImageStore imageStore)
    {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
    }

    public void executeAction(EventScheduler scheduler) {
        this.entity.executeActivity(world, imageStore, scheduler);
    }
}
