import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Vein extends ActiveEntity {

    public Vein(
            String id,
            Point position,
            List<PImage> images,
            int actionPeriod)
    {
        super(id, position, images, actionPeriod);
    }

    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Point> openPt = world.findOpenAround(getPosition());

        if (openPt.isPresent()) {
            Ore ore = Factory.createOre(Factory.ORE_ID_PREFIX + getId(), openPt.get(),
                    Factory.ORE_CORRUPT_MIN + Factory.rand.nextInt(
                            Factory.ORE_CORRUPT_MAX - Factory.ORE_CORRUPT_MIN),
                    imageStore.getImageList(Parser.ORE_KEY));
            world.addEntity(ore);
            ore.scheduleActions(scheduler, world, imageStore);
        }

        scheduleActions(scheduler, world, imageStore);
    }
}
