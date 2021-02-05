public final class ActivityAction implements Action
{
    private Entity entity;
    private WorldModel world;
    private ImageStore imageStore;
    private int repeatCount;

    public ActivityAction(
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


    public static Action createAction(Entity entity, WorldModel world, ImageStore imageStore)
    {
        return new ActivityAction(entity, world, imageStore, 0);
    }

    public void executeAction(EventScheduler scheduler) {
        switch (this.entity.kind) {
            case MINER_FULL:
                this.entity.executeMinerFullActivity(this.world,
                        this.imageStore, scheduler);
                break;

            case MINER_NOT_FULL:
                this.entity.executeMinerNotFullActivity(this.world,
                        this.imageStore, scheduler);
                break;

            case ORE:
                this.entity.executeOreActivity(this.world,
                        this.imageStore, scheduler);
                break;

            case ORE_BLOB:
                this.entity.executeOreBlobActivity(this.world,
                        this.imageStore, scheduler);
                break;

            case QUAKE:
                this.entity.executeQuakeActivity(this.world,
                        scheduler);
                break;

            case VEIN:
                this.entity.executeVeinActivity(this.world,
                        this.imageStore, scheduler);
                break;

            default:
                throw new UnsupportedOperationException(String.format(
                        "executeActivityAction not supported for %s",
                        this.entity.kind));
        }
    }
}
