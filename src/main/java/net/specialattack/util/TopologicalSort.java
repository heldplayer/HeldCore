package net.specialattack.util;

import java.lang.ref.WeakReference;
import java.util.*;

/*
 * You are free to use this class as part of SpACore, but are not allowed to distribute it on its own or copy it.
 *
 * You are free to use this class for educational use however, as long as the file and this header are kept in its entirety.
 */

/**
 * Utility class used to sort elements that are related to eachother in some way. Only works for acyclic graphs.
 *
 * Sorting order is dependant on the order of addition and the added relations.
 *
 * This implementation uses Kahn's algorithm to sort its elements.
 *
 * @param <N>
 *         The type of object to sort. Does not have to implement Comparable.
 *
 * @author heldplayer
 */
public class TopologicalSort<N> {

    private final List<N> nodes;
    private final LinkedList<Vertex<N>> vertices;
    private WeakReference<List<N>> lastSort = new WeakReference<List<N>>(null);
    private boolean dirty;

    /**
     * Creates a new instance of a TopologicalSort class.
     *
     * @param nodes
     *         A collection of nodes for this view.
     */
    public TopologicalSort(Collection<N> nodes) {
        this.nodes = new ArrayList<N>(nodes);
        this.vertices = new LinkedList<Vertex<N>>();
        this.dirty = true;
    }

    /**
     * Creates a new instance of a TopologicalSort class.
     */
    public TopologicalSort() {
        this.nodes = new ArrayList<N>();
        this.vertices = new LinkedList<Vertex<N>>();
        this.dirty = true;
    }

    /**
     * Adds a node to the graph.
     *
     * @param node
     *         The node to add.
     *
     * @throws IllegalArgumentException
     *         If node is null.
     */
    public void addNode(N node) {
        if (node == null) {
            throw new IllegalArgumentException("Node cannot be null");
        }
        this.nodes.add(node);
        this.dirty = true;
    }

    /**
     * Adds a unidirectional relation between 2 nodes.
     *
     * @param from
     *         The node where the relation originates from. Also called the tail of the relation
     * @param to
     *         The node where the relation goes to. Also called the head of the relation.
     *
     * @throws IllegalArgumentException
     *         If either the from or to node are not part of the collection of nodes.
     */
    public void addRelation(N from, N to) {
        if (!this.nodes.contains(from)) {
            throw new IllegalArgumentException("Vertex origin node must be in the collection");
        }
        if (!this.nodes.contains(to)) {
            throw new IllegalArgumentException("Vertex destination node must be in the collection");
        }
        this.vertices.add(new TopologicalSort.Vertex<N>(from, to));
        this.dirty = true;
        this.lastSort = new WeakReference<List<N>>(null);
    }

    /**
     * Sorts the nodes in this collection based on the relations that have been defined.
     *
     * This method may decide to use a previously calculated version of the List,
     * if no changes to the structure have been made.
     *
     * @return A sorted list containing all the nodes in this list.
     * Nodes that do not depend on other nodes are first in the list,
     * while nodes that are not depended upon are last in the list.
     *
     * @throws IllegalStateException
     *         If the collection contains cycles.
     */
    public List<N> getSorted() {
        if (!this.dirty) {
            List<N> last = this.lastSort.get();
            if (last != null) {
                return last;
            }
        }
        // First we construct the relations between all the nodes. Namely the parents
        int remaining = this.nodes.size();
        HashMap<N, Relation<N>> relations = new HashMap<N, Relation<N>>(remaining);
        for (N node : this.nodes) { // Create a new blank relation for each node
            relations.put(node, new TopologicalSort.Relation<N>(node));
        }
        for (TopologicalSort.Vertex<N> vertex : this.vertices) { // Populate the relations for each node based on the vertices
            TopologicalSort.Relation<N> relation = relations.get(vertex.from);
            if (relation == null) { // This shouldn't be possible normally
                throw new IllegalStateException("Missing node from relation in collection");
            }
            relation.addParent(vertex.to);
        }
        Deque<N> orphans = new ArrayDeque<N>();
        List<N> result = new ArrayList<N>(remaining);
        Iterator<Map.Entry<N, Relation<N>>> i = relations.entrySet().iterator();
        while (i.hasNext()) { // Find all orphans first, as they appear at the top of the graph
            Map.Entry<N, TopologicalSort.Relation<N>> entry = i.next();
            if (entry.getValue().parents == null) { // null signals that there are no parents in this case
                orphans.add(entry.getKey());
                i.remove();
            }
        }
        while (!orphans.isEmpty()) { // Go over each orphan
            N orphan = orphans.removeFirst();
            i = relations.entrySet().iterator();
            while (i.hasNext()) { // Go over all relations to see if they have the orphan as a parent
                Map.Entry<N, TopologicalSort.Relation<N>> entry = i.next();
                List<N> parents = entry.getValue().parents;
                while (parents != null && parents.contains(orphan)) { // If the orphan is a parent, detach the child
                    // While loop to allow multiple arrows
                    parents.remove(orphan);
                    if (parents.size() == 0) { // If the child has no remaining parents, make it an orphan
                        orphans.add(entry.getKey());
                    }
                }
            }
            result.add(orphan); // Add the orphan to the end of the list and decrease the counter
            remaining--;
        }
        if (remaining != 0) { // If the counter is not 0, then we have nodes with parents that are children, which we can't sort
            throw new IllegalStateException("Collection contains cycles");
        }
        this.lastSort = new WeakReference<List<N>>(result);
        return result;
    }

    /*
     * Represents a relation between 2 nodes that can't change
     */
    private static class Vertex<N> {

        public final N to, from;

        public Vertex(N to, N from) {
            if (to == null || from == null) {
                throw new IllegalArgumentException("Cannot define a relation on a null node");
            }
            this.to = to;
            this.from = from;
        }
    }

    /*
     * Represents a list of relations for a node to be used when sorting.
     */
    private static class Relation<N> {

        public final N node;
        private List<N> parents;

        private Relation(N node) {
            if (node == null) {
                throw new IllegalArgumentException("Cannot define a relation on a null node");
            }
            this.node = node;
        }

        public void addParent(N node) {
            if (node == null) {
                throw new IllegalArgumentException("Cannot add a null node as parent");
            }
            if (this.parents == null) {
                this.parents = new ArrayList<N>();
            }
            this.parents.add(node);
        }
    }
}
