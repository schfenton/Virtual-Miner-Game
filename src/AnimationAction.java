public final class AnimationAction implements Action
{
    private Entity entity;
    private WorldModel world;
    private ImageStore imageStore;
    private int repeatCount;

    public AnimationAction(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            int repeatCount)
    {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
        this.repeatCount = repeatCount;
    }


    public static AnimationAction createAction(Entity entity, int repeatCount) {
        return new AnimationAction(entity, null, null,
                          repeatCount);
    }

    public void executeAction(EventScheduler scheduler) {
        this.entity.nextImage();

        if (this.repeatCount != 1) {
            scheduler.scheduleEvent(this.entity,
                    createAction(this.entity, Math.max(this.repeatCount - 1, 0)), this.entity.getAnimationPeriod());
        }
    }

}
