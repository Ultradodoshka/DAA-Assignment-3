package mst;

import graph.Edge;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Prim {
    public static List<Edge> findMST(int numVertices, List<List<Edge>> adj) {
        List<Edge> mstEdges = new ArrayList<>();
        if (numVertices<=1) return mstEdges;

        boolean[] visited = new boolean[numVertices];
        PriorityQueue<Edge> pq = new PriorityQueue<>();

        visited[0] = true;
        pq.addAll(adj.get(0));

        while (!pq.isEmpty() && mstEdges.size()<numVertices-1) {
            Edge edge = pq.poll();

            int nextVertex = -1;
            if (!visited[edge.u] && visited[edge.v])nextVertex = edge.u;
            else if (visited[edge.u] && !visited[edge.v])nextVertex = edge.v;

            if (nextVertex!=-1) {
                visited[nextVertex] = true;
                mstEdges.add(edge);
                for (Edge nextEdge : adj.get(nextVertex)) {
                    int neighbor = (nextEdge.u==nextVertex) ? nextEdge.v : nextEdge.u;
                    if (!visited[neighbor]) {
                        pq.add(nextEdge);
                    }
                }
            }
        }
        return mstEdges;
    }
}