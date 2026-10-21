package mst;

import dsu.DSU;
import graph.Edge;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Kruskal {
    public static List<Edge> findMST(int numVertices, List<Edge> edges) {
        List<Edge> sortedEdges = new ArrayList<>(edges);
        Collections.sort(sortedEdges);

        DSU dsu = new DSU(numVertices);
        List<Edge> mstEdges = new ArrayList<>();

        for (Edge edge : sortedEdges) {
            if (dsu.union(edge.u, edge.v)) {
                mstEdges.add(edge);
                if (mstEdges.size()==numVertices-1) {
                    break;
                }
            }
        }
        return mstEdges;
    }
}