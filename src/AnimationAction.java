public final class AnimationAction implements Action
{
    private Entity entity;
    private int repeatCount;

    public AnimationAction(
            Entity entity,
            int repeatCount)
    {
        this.entity = entity;
        this.repeatCount = repeatCount;
    }

    public void executeAction(EventScheduler scheduler) {
        this.entity.nextImage();

        if (this.repeatCount != 1) {
            scheduler.scheduleEvent(this.entity,
                    Factory.createAnimationAction(  this.entity,
                                                    Math.max(this.repeatCount - 1, 0)),
                                                    this.entity.getAnimationPeriod()  );
        }
    }

}
