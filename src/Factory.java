public class Factory {

    public static ActivityAction createActivityAction(Entity entity, WorldModel world, ImageStore imageStore) {
        return new ActivityAction(entity, world, imageStore);
    }

    public static AnimationAction createAnimationAction(Entity entity, int repeatCount) {
        return new AnimationAction(entity, repeatCount);
    }
}
