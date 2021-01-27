import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;

import processing.core.PImage;
import processing.core.PApplet;

public final class Functions
{
    public static final int ORE_REACH = 1;

    public static final String QUAKE_ID = "quake";
    public static final int QUAKE_ACTION_PERIOD = 1100;
    public static final int QUAKE_ANIMATION_PERIOD = 100;

    public static final int COLOR_MASK = 0xffffff;
    public static final int KEYED_IMAGE_MIN = 5;
    public static final int QUAKE_ANIMATION_REPEAT_COUNT = 10;
    public static final String QUAKE_KEY = "quake";
    public static final Random rand = new Random();
    public static final String BLOB_KEY = "blob";
    public static final String BLOB_ID_SUFFIX = " -- blob";
    public static final int BLOB_PERIOD_SCALE = 4;
    public static final int BLOB_ANIMATION_MIN = 50;
    public static final int BLOB_ANIMATION_MAX = 150;
    public static final String ORE_ID_PREFIX = "ore -- ";
    public static final int ORE_CORRUPT_MIN = 20000;
    public static final int ORE_CORRUPT_MAX = 30000;
    private static final int KEYED_RED_IDX = 2;
    private static final int KEYED_GREEN_IDX = 3;
    private static final int KEYED_BLUE_IDX = 4;

    public static final int PROPERTY_KEY = 0;

    public static final String BGND_KEY = "background";
    public static final int BGND_NUM_PROPERTIES = 4;
    public static final int BGND_ID = 1;
    public static final int BGND_COL = 2;
    public static final int BGND_ROW = 3;

    public static final String MINER_KEY = "miner";
    public static final int MINER_NUM_PROPERTIES = 7;
    public static final int MINER_ID = 1;
    public static final int MINER_COL = 2;
    public static final int MINER_ROW = 3;
    public static final int MINER_LIMIT = 4;
    public static final int MINER_ACTION_PERIOD = 5;
    public static final int MINER_ANIMATION_PERIOD = 6;

    public static final String OBSTACLE_KEY = "obstacle";
    public static final int OBSTACLE_NUM_PROPERTIES = 4;
    public static final int OBSTACLE_ID = 1;
    public static final int OBSTACLE_COL = 2;
    public static final int OBSTACLE_ROW = 3;

    public static final String ORE_KEY = "ore";
    public static final int ORE_NUM_PROPERTIES = 5;
    public static final int ORE_ID = 1;
    public static final int ORE_COL = 2;
    public static final int ORE_ROW = 3;
    public static final int ORE_ACTION_PERIOD = 4;

    public static final String SMITH_KEY = "blacksmith";
    public static final int SMITH_NUM_PROPERTIES = 4;
    public static final int SMITH_ID = 1;
    public static final int SMITH_COL = 2;
    public static final int SMITH_ROW = 3;

    public static final String VEIN_KEY = "vein";
    public static final int VEIN_NUM_PROPERTIES = 5;
    public static final int VEIN_ID = 1;
    public static final int VEIN_COL = 2;
    public static final int VEIN_ROW = 3;
    public static final int VEIN_ACTION_PERIOD = 4;

    public static void executeAction(Action action, EventScheduler scheduler) {
        switch (action.kind) {
            case ACTIVITY:
                executeActivityAction(action, scheduler);
                break;

            case ANIMATION:
                executeAnimationAction(action, scheduler);
                break;
        }
    }

    public static void executeAnimationAction(
            Action action, EventScheduler scheduler)
    {
        action.entity.nextImage();

        if (action.repeatCount != 1) {
            scheduler.scheduleEvent(action.entity,
                          Action.createAnimationAction(action.entity,
                                                Math.max(action.repeatCount - 1,
                                                         0)), action.entity.getAnimationPeriod());
        }
    }

    public static void executeActivityAction( //possibly Action
            Action action, EventScheduler scheduler)
    {
        switch (action.entity.kind) {
            case MINER_FULL:
                action.entity.executeMinerFullActivity(action.world,
                                         action.imageStore, scheduler);
                break;

            case MINER_NOT_FULL:
                action.entity.executeMinerNotFullActivity(action.world,
                                            action.imageStore, scheduler);
                break;

            case ORE:
                action.entity.executeOreActivity(action.world,
                                   action.imageStore, scheduler);
                break;

            case ORE_BLOB:
                action.entity.executeOreBlobActivity(action.world,
                                       action.imageStore, scheduler);
                break;

            case QUAKE:
                action.entity.executeQuakeActivity(action.world,
                                     action.imageStore, scheduler);
                break;

            case VEIN:
                action.entity.executeVeinActivity(action.world,
                                    action.imageStore, scheduler);
                break;

            default:
                throw new UnsupportedOperationException(String.format(
                        "executeActivityAction not supported for %s",
                        action.entity.kind));
        }
    }

    public static void executeMinerFullActivity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> fullTarget =
                findNearest(world, entity.position, EntityKind.BLACKSMITH);

        if (fullTarget.isPresent() && moveToFull(entity, world,
                fullTarget.get(), scheduler))
        {
            transformFull(entity, world, scheduler, imageStore);
        }
        else {
            scheduler.scheduleEvent(entity,
                    Action.createActivityAction(entity, world, imageStore),
                    entity.actionPeriod);
        }
    }

    public static void executeMinerNotFullActivity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> notFullTarget =
                findNearest(world, entity.position, EntityKind.ORE);

        if (!notFullTarget.isPresent() || !moveToNotFull(entity, world,
                                                         notFullTarget.get(),
                                                         scheduler)
                || !transformNotFull(entity, world, scheduler, imageStore))
        {
            scheduler.scheduleEvent(entity,
                          Action.createActivityAction(entity, world, imageStore),
                          entity.actionPeriod);
        }
    }

    public static void executeOreActivity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Point pos = entity.position;

        world.removeEntity(entity);
        scheduler.unscheduleAllEvents(entity);

        Entity blob = Entity.createOreBlob(entity.id + BLOB_ID_SUFFIX, pos,
                                    entity.actionPeriod / BLOB_PERIOD_SCALE,
                                    BLOB_ANIMATION_MIN + rand.nextInt(
                                            BLOB_ANIMATION_MAX
                                                    - BLOB_ANIMATION_MIN),
                                    getImageList(imageStore, BLOB_KEY));

        world.addEntity(blob);
        scheduleActions(blob, scheduler, world, imageStore);
    }

    public static void executeOreBlobActivity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> blobTarget =
                findNearest(world, entity.position, EntityKind.VEIN);
        long nextPeriod = entity.actionPeriod;

        if (blobTarget.isPresent()) {
            Point tgtPos = blobTarget.get().position;

            if (moveToOreBlob(entity, world, blobTarget.get(), scheduler)) {
                Entity quake = Entity.createQuake(tgtPos,
                                           getImageList(imageStore, QUAKE_KEY));

                world.addEntity(quake);
                nextPeriod += entity.actionPeriod;
                scheduleActions(quake, scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(entity,
                      Action.createActivityAction(entity, world, imageStore),
                      nextPeriod);
    }

    public static void executeQuakeActivity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        scheduler.unscheduleAllEvents(entity);
        world.removeEntity(entity);
    }

    public static void executeVeinActivity(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Point> openPt = findOpenAround(world, entity.position);

        if (openPt.isPresent()) {
            Entity ore = Entity.createOre(ORE_ID_PREFIX + entity.id, openPt.get(),
                                   ORE_CORRUPT_MIN + rand.nextInt(
                                           ORE_CORRUPT_MAX - ORE_CORRUPT_MIN),
                                   getImageList(imageStore, ORE_KEY));
            world.addEntity(ore);
            scheduleActions(ore, scheduler, world, imageStore);
        }

        scheduler.scheduleEvent(entity,
                      Action.createActivityAction(entity, world, imageStore),
                      entity.actionPeriod);
    }

    public static void scheduleActions(
            Entity entity,
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore)
    {
        switch (entity.kind) {
            case MINER_FULL:
                scheduler.scheduleEvent(entity,
                              Action.createActivityAction(entity, world, imageStore),
                              entity.actionPeriod);
                scheduler.scheduleEvent(entity,
                              Action.createAnimationAction(entity, 0), entity.getAnimationPeriod());
                break;

            case MINER_NOT_FULL:
                scheduler.scheduleEvent(entity,
                              Action.createActivityAction(entity, world, imageStore),
                              entity.actionPeriod);
                scheduler.scheduleEvent(entity,
                              Action.createAnimationAction(entity, 0),
                              entity.getAnimationPeriod());
                break;

            case ORE:
                scheduler.scheduleEvent(entity,
                              Action.createActivityAction(entity, world, imageStore),
                              entity.actionPeriod);
                break;

            case ORE_BLOB:
                scheduler.scheduleEvent(entity,
                              Action.createActivityAction(entity, world, imageStore),
                              entity.actionPeriod);
                scheduler.scheduleEvent(entity,
                              Action.createAnimationAction(entity, 0),
                              entity.getAnimationPeriod());
                break;

            case QUAKE:
                scheduler.scheduleEvent(entity,
                              Action.createActivityAction(entity, world, imageStore),
                              entity.actionPeriod);
                scheduler.scheduleEvent(entity, Action.createAnimationAction(entity,
                                                                       QUAKE_ANIMATION_REPEAT_COUNT),
                              entity.getAnimationPeriod());
                break;

            case VEIN:
                scheduler.scheduleEvent(entity,
                              Action.createActivityAction(entity, world, imageStore),
                              entity.actionPeriod);
                break;

            default:
        }
    }

    public static boolean transformNotFull(
            Entity entity,
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        if (entity.resourceCount >= entity.resourceLimit) {
            Entity miner = Entity.createMinerFull(entity.id, entity.resourceLimit,
                                           entity.position, entity.actionPeriod,
                                           entity.animationPeriod,
                                           entity.images);

            world.removeEntity(entity);
            scheduler.unscheduleAllEvents(entity);

            world.addEntity(miner);
            scheduleActions(miner, scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public static void transformFull(
            Entity entity,
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        Entity miner = Entity.createMinerNotFull(entity.id, entity.resourceLimit,
                                          entity.position, entity.actionPeriod,
                                          entity.animationPeriod,
                                          entity.images);

        world.removeEntity(entity);
        scheduler.unscheduleAllEvents(entity);

        world.addEntity(miner);
        scheduleActions(miner, scheduler, world, imageStore);
    }

    public static boolean moveToNotFull(
            Entity miner,
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (adjacent(miner.position, target.position)) {
            miner.resourceCount += 1;
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);

            return true;
        }
        else {
            Point nextPos = miner.nextPositionMiner(world, target.position);

            if (!miner.position.equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(miner, nextPos);
            }
            return false;
        }
    }

    public static boolean moveToFull(
            Entity miner,
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (adjacent(miner.position, target.position)) {
            return true;
        }
        else {
            Point nextPos = miner.nextPositionMiner(world, target.position);

            if (!miner.position.equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(miner, nextPos);
            }
            return false;
        }
    }

    public static boolean moveToOreBlob(
            Entity blob,
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (adjacent(blob.position, target.position)) {
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);
            return true;
        }
        else {
            Point nextPos = blob.nextPositionOreBlob(world, target.position);

            if (!blob.position.equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(blob, nextPos);
            }
            return false;
        }
    }

    public static boolean adjacent(Point p1, Point p2) {
        return (p1.x == p2.x && Math.abs(p1.y - p2.y) == 1) || (p1.y == p2.y
                && Math.abs(p1.x - p2.x) == 1);
    }

    public static Optional<Point> findOpenAround(WorldModel world, Point pos) {
        for (int dy = -ORE_REACH; dy <= ORE_REACH; dy++) {
            for (int dx = -ORE_REACH; dx <= ORE_REACH; dx++) {
                Point newPt = new Point(pos.x + dx, pos.y + dy);
                if (newPt.withinBounds(world) && !world.isOccupied(newPt)) {
                    return Optional.of(newPt);
                }
            }
        }

        return Optional.empty();
    }

    public static void removePendingEvent(
            EventScheduler scheduler, Event event)
    {
        List<Event> pending = scheduler.pendingEvents.get(event.entity);

        if (pending != null) {
            pending.remove(event);
        }
    }

    public static void updateOnTime(EventScheduler scheduler, long time) {
        while (!scheduler.eventQueue.isEmpty()
                && scheduler.eventQueue.peek().time < time) {
            Event next = scheduler.eventQueue.poll();

            removePendingEvent(scheduler, next);

            executeAction(next.action, scheduler);
        }
    }

    public static void loadImages(
            Scanner in, ImageStore imageStore, PApplet screen)
    {
        int lineNumber = 0;
        while (in.hasNextLine()) {
            try {
                processImageLine(imageStore.images, in.nextLine(), screen);
            }
            catch (NumberFormatException e) {
                System.out.println(
                        String.format("Image format error on line %d",
                                      lineNumber));
            }
            lineNumber++;
        }
    }

    public static void processImageLine(
            Map<String, List<PImage>> images, String line, PApplet screen)
    {
        String[] attrs = line.split("\\s");
        if (attrs.length >= 2) {
            String key = attrs[0];
            PImage img = screen.loadImage(attrs[1]);
            if (img != null && img.width != -1) {
                List<PImage> imgs = getImages(images, key);
                imgs.add(img);

                if (attrs.length >= KEYED_IMAGE_MIN) {
                    int r = Integer.parseInt(attrs[KEYED_RED_IDX]);
                    int g = Integer.parseInt(attrs[KEYED_GREEN_IDX]);
                    int b = Integer.parseInt(attrs[KEYED_BLUE_IDX]);
                    setAlpha(img, screen.color(r, g, b), 0);
                }
            }
        }
    }

    public static List<PImage> getImages(
            Map<String, List<PImage>> images, String key)
    {
        List<PImage> imgs = images.get(key);
        if (imgs == null) {
            imgs = new LinkedList<>();
            images.put(key, imgs);
        }
        return imgs;
    }

    /*
      Called with color for which alpha should be set and alpha value.
      setAlpha(img, color(255, 255, 255), 0));
    */
    public static void setAlpha(PImage img, int maskColor, int alpha) {
        int alphaValue = alpha << 24;
        int nonAlpha = maskColor & COLOR_MASK;
        img.format = PApplet.ARGB;
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++) {
            if ((img.pixels[i] & COLOR_MASK) == nonAlpha) {
                img.pixels[i] = alphaValue | nonAlpha;
            }
        }
        img.updatePixels();
    }

    public static boolean contains(Viewport viewport, Point p) {
        return p.y >= viewport.row && p.y < viewport.row + viewport.numRows
                && p.x >= viewport.col && p.x < viewport.col + viewport.numCols;
    }

    public static void load(
            Scanner in, WorldModel world, ImageStore imageStore)
    {
        int lineNumber = 0;
        while (in.hasNextLine()) {
            try {
                if (!processLine(in.nextLine(), world, imageStore)) {
                    System.err.println(String.format("invalid entry on line %d",
                                                     lineNumber));
                }
            }
            catch (NumberFormatException e) {
                System.err.println(
                        String.format("invalid entry on line %d", lineNumber));
            }
            catch (IllegalArgumentException e) {
                System.err.println(
                        String.format("issue on line %d: %s", lineNumber,
                                      e.getMessage()));
            }
            lineNumber++;
        }
    }

    public static boolean processLine( // keep in Func
            String line, WorldModel world, ImageStore imageStore)
    {
        String[] properties = line.split("\\s");
        if (properties.length > 0) {
            switch (properties[PROPERTY_KEY]) {
                case BGND_KEY:
                    return parseBackground(properties, world, imageStore);
                case MINER_KEY:
                    return parseMiner(properties, world, imageStore);
                case OBSTACLE_KEY:
                    return parseObstacle(properties, world, imageStore);
                case ORE_KEY:
                    return parseOre(properties, world, imageStore);
                case SMITH_KEY:
                    return parseSmith(properties, world, imageStore);
                case VEIN_KEY:
                    return parseVein(properties, world, imageStore);
            }
        }

        return false;
    }

    public static boolean parseBackground(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == BGND_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[BGND_COL]),
                                 Integer.parseInt(properties[BGND_ROW]));
            String id = properties[BGND_ID];
            setBackground(world, pt,
                          new Background(id, imageStore.getImageList(id)));
        }

        return properties.length == BGND_NUM_PROPERTIES;
    }

    public static boolean parseMiner(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == MINER_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[MINER_COL]),
                                 Integer.parseInt(properties[MINER_ROW]));
            Entity entity = Entity.createMinerNotFull(properties[MINER_ID],
                                               Integer.parseInt(
                                                       properties[MINER_LIMIT]),
                                               pt, Integer.parseInt(
                            properties[MINER_ACTION_PERIOD]), Integer.parseInt(
                            properties[MINER_ANIMATION_PERIOD]),
                            imageStore.getImageList(MINER_KEY));
            world.tryAddEntity(entity);
        }

        return properties.length == MINER_NUM_PROPERTIES;
    }

    public static boolean parseObstacle(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == OBSTACLE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[OBSTACLE_COL]),
                                 Integer.parseInt(properties[OBSTACLE_ROW]));
            Entity entity = Entity.createObstacle(properties[OBSTACLE_ID], pt,
                                           imageStore.getImageList(OBSTACLE_KEY));
            world.tryAddEntity(entity);
        }

        return properties.length == OBSTACLE_NUM_PROPERTIES;
    }

    public static boolean parseOre(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == ORE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[ORE_COL]),
                                 Integer.parseInt(properties[ORE_ROW]));
            Entity entity = Entity.createOre(properties[ORE_ID], pt, Integer.parseInt(
                    properties[ORE_ACTION_PERIOD]),
                                      imageStore.getImageList(ORE_KEY));
            world.tryAddEntity(entity);
        }

        return properties.length == ORE_NUM_PROPERTIES;
    }

    public static boolean parseSmith(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == SMITH_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[SMITH_COL]),
                                 Integer.parseInt(properties[SMITH_ROW]));
            Entity entity = Entity.createBlacksmith(properties[SMITH_ID], pt,
                                             imageStore.getImageList(SMITH_KEY));
            world.tryAddEntity(entity);
        }

        return properties.length == SMITH_NUM_PROPERTIES;
    }

    public static boolean parseVein(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == VEIN_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[VEIN_COL]),
                                 Integer.parseInt(properties[VEIN_ROW]));
            Entity entity = Entity.createVein(properties[VEIN_ID], pt,
                                       Integer.parseInt(
                                               properties[VEIN_ACTION_PERIOD]),
                                       imageStore.getImageList(VEIN_KEY));
            world.tryAddEntity(entity);
        }

        return properties.length == VEIN_NUM_PROPERTIES;
    }

    public static Optional<PImage> getBackgroundImage(
            WorldModel world, Point pos)
    {
        if (pos.withinBounds(world)) {
            return Optional.of(getBackgroundCell(world, pos).getCurrentImage());
        }
        else {
            return Optional.empty();
        }
    }

    public static void setBackground(
            WorldModel world, Point pos, Background background)
    {
        if (pos.withinBounds(world)) {
            setBackgroundCell(world, pos, background);
        }
    }

    public static Background getBackgroundCell(WorldModel world, Point pos) {
        return world.background[pos.y][pos.x];
    }

    public static void setBackgroundCell(
            WorldModel world, Point pos, Background background)
    {
        world.background[pos.y][pos.x] = background;
    }

    public static Point viewportToWorld(Viewport viewport, int col, int row) {
        return new Point(col + viewport.col, row + viewport.row);
    }

    public static Point worldToViewport(Viewport viewport, int col, int row) {
        return new Point(col - viewport.col, row - viewport.row);
    }

    public static int clamp(int value, int low, int high) {
        return Math.min(high, Math.max(value, low));
    }

    public static void drawBackground(WorldView view) {
        for (int row = 0; row < view.getViewport().numRows; row++) {
            for (int col = 0; col < view.getViewport().numCols; col++) {
                Point worldPoint = viewportToWorld(view.getViewport(), col, row);
                Optional<PImage> image =
                        getBackgroundImage(view.getWorld(), worldPoint);
                if (image.isPresent()) {
                    view.screen.image(image.get(), col * view.tileWidth,
                                      row * view.tileHeight);
                }
            }
        }
    }

    public static void drawEntities(WorldView view) {
        for (Entity entity : view.getWorld().entities) {
            Point pos = entity.position;

            if (contains(view.getViewport(), pos)) {
                Point viewPoint = worldToViewport(view.getViewport(), pos.x, pos.y);
                view.screen.image(entity.getCurrentImage(),
                                  viewPoint.x * view.tileWidth,
                                  viewPoint.y * view.tileHeight);
            }
        }
    }

    public static void drawViewport(WorldView view) {
        drawBackground(view);
        drawEntities(view);
    }

}
