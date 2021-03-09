import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

class AStarPathingStrategy
        implements PathingStrategy
{


    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors)
    {
        List<Point> path = new ArrayList<>(); // ArrayList because only storing and accessing data, not changing
        HashMap<Integer, Node> open_list = new HashMap<>(); // HashMap orders by f value and does not allow duplicates (TreeSet hard to get specific value)
        HashMap<Integer, Node> closed_list = new HashMap<>(); //order does not matter, just need to put non-dup points somewhere

        Node currentNode = new Node(start, null, 0, heuristicDist(start, end));
        open_list.put(start.hashCode(), currentNode); // add start node to open list

        while(!withinReach.test(currentNode.pos, end)) { // while currentPoint is not in reach of end (6)

            Point currentPoint = currentNode.pos; // work around for lambda? why?

            // add every neighbor of point to open list with dist from start node one more than current
            Node finalCurrentNode = currentNode; //why lambda workaround?
            potentialNeighbors.apply(currentPoint)
                    .filter(pt -> !pt.equals(start) && !pt.equals(end))
                    .filter(canPassThrough)
                    // only add/update points that are not both in the list and have a smaller g value
                    .filter(pt -> !(closed_list.containsKey(pt.hashCode())))
                    .filter(pt -> !(open_list.containsKey(pt.hashCode()) && open_list.get(pt.hashCode()).g > finalCurrentNode.g))
                    .forEach(pt -> {

                        open_list.put(pt.hashCode(), new Node(pt, finalCurrentNode, finalCurrentNode.g + 1, heuristicDist(pt, end)));

                    }); //review this (3a)

            closed_list.put(currentPoint.hashCode(), currentNode); // 4
            open_list.remove(currentPoint.hashCode()); // 4
            currentNode = findMinFNode(open_list); // 5
            if(currentNode == null){
                break;
            }
        }

        while(currentNode != null &&  currentNode.priorNode != null){
            path.add(currentNode.pos);
            currentNode = currentNode.priorNode;
        }

        Collections.reverse(path);
        return path;
    }

    private static int heuristicDist(Point p1, Point p2){ //best way to implement heuristicDist?
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }

    private static Node findMinFNode(HashMap<Integer, Node> m){
        Object[] a = m.values().toArray();

        if(a.length > 0){
            Node lowest = (Node) a[0];
            for (Object n : a){
                if(((Node) n).f < lowest.f){
                    lowest = (Node) n;
                }
            }
            return lowest;
        }

        return null;
    }

    private class Node
    {
        public final Point pos;
        public final Node priorNode;
        public final int g;
        public final int h;
        public final int f;

        public Node(Point pt, Node priorNode, int g, int h){
            this.pos = pt;
            this.priorNode = priorNode;
            this.g = g;
            this.h = h;
            f = g + h;
        }
    }
}


