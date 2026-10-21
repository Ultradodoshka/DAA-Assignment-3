package shortestpath;

import graph.Edge;
import java.util.*;

public class Dijkstra {

    public static class Result {
        public final int[] dist;
        public final int[] parent;

        public Result(int[] dist, int[] parent) {
            this.dist = dist;
            this.parent = parent;
        }

        public List<Integer> getPath(int target) {
            if (dist[target] == Integer.MAX_VALUE) {
                return Collections.emptyList(); // Вершина недостижима
            }
            List<Integer> path = new ArrayList<>();
            for (int at = target; at != -1; at = parent[at]) {
                path.add(at);
            }
            Collections.reverse(path);
            return path;
        }
    }

    private static class Node implements Comparable<Node> {
        int vertex;
        int distance;

        Node(int vertex, int distance) {
            this.vertex = vertex;
            this.distance = distance;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.distance, other.distance);
        }
    }


    public static Result findShortestPaths(int numVertices, List<List<Edge>> adj, int source) {
        int[] dist = new int[numVertices];
        int[] parent = new int[numVertices];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);

        PriorityQueue<Node> pq = new PriorityQueue<>();
        dist[source] = 0;
        pq.add(new Node(source, 0));

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            int u = current.vertex;

            if (current.distance > dist[u]) {
                continue;
            }

            for (Edge edge : adj.get(u)) {
                int v = edge.v;
                int weight = edge.weight;

                if (dist[u] != Integer.MAX_VALUE && dist[u]+weight<dist[v]) {
                    dist[v] = dist[u]+weight;
                    parent[v] = u;
                    pq.add(new Node(v, dist[v]));
                }
            }
        }

        return new Result(dist, parent);
    }
}