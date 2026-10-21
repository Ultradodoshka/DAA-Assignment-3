import connectivity.KosarajuSCC;
import connectivity.TopologicalSort;
import graph.Edge;
import mst.Kruskal;
import mst.Prim;
import shortestpath.Dijkstra;

import java.util.*;

public class Test{
    public static void main(String[] args) {
        testSingleVertex();
        testDisconnectedGraph();
        testMultipleValidMSTAndDuplicates();
        testTopologicalSortDAGAndCycles();
        testSCCProperties();
        testDijkstraUnreachableAndDistances();

        System.out.println("\nAll tests passed successfully!");
    }

    private static void testSingleVertex() {
        int v = 1;
        List<Edge> edges = new ArrayList<>();
        List<List<Edge>> adj = createAdj(v, edges);

        List<Edge> mstK = Kruskal.findMST(v, edges);
        List<Edge> mstP = Prim.findMST(v, adj);
        if (!mstK.isEmpty()) {
            throw new RuntimeException("Kruskal MST for single vertex must be empty");
        }
        if (!mstP.isEmpty()) {
            throw new RuntimeException("Prim MST for single vertex must be empty");
        }

        Dijkstra.Result dij = Dijkstra.findShortestPaths(v, adj, 0);
        if (dij.dist[0] != 0) {
            throw new RuntimeException("Distance to self must be 0");
        }

        List<List<Integer>> scc = KosarajuSCC.findSCCs(v, toDirAdj(v, edges));
        if (scc.size() != 1 || scc.get(0).get(0) != 0) {
            throw new RuntimeException("Single vertex must form exactly one SCC");
        }

        System.out.println("[PASS] Single vertex test");
    }

    private static void testDisconnectedGraph() {
        int v = 4;
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(0, 1, 5));
        edges.add(new Edge(2, 3, 5));

        List<List<Edge>> adj = createAdj(v, edges);

        List<Edge> mstK = Kruskal.findMST(v, edges);
        List<Edge> mstP = Prim.findMST(v, adj);

        boolean kConnected = (v<=1) || (mstK.size()==v-1);
        boolean pConnected = (v<=1) || (mstP.size()==v-1);

        if (kConnected) {
            throw new RuntimeException("Kruskal must detect disconnected graph");
        }
        if (pConnected) {
            throw new RuntimeException("Prim must detect disconnected graph");
        }

        System.out.println("[PASS] Disconnected graph test");
    }

    private static void testMultipleValidMSTAndDuplicates() {
        int v = 4;
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(0, 1, 2));
        edges.add(new Edge(1, 2, 2));
        edges.add(new Edge(2, 3, 2));
        edges.add(new Edge(3, 0, 2));

        List<List<Edge>> adj = createAdj(v, edges);
        List<Edge> mstK = Kruskal.findMST(v, edges);
        List<Edge> mstP = Prim.findMST(v, adj);

        int weightK = 0;
        for (Edge e : mstK) {
            weightK += e.weight;
        }

        int weightP = 0;
        for (Edge e : mstP) {
            weightP += e.weight;
        }

        if (weightK!=weightP) {
            throw new RuntimeException("Kruskal and Prim MST total weights must match");
        }
        if (weightK!=6) {
            throw new RuntimeException("Expected MST weight is 6");
        }

        System.out.println("[PASS] Multiple valid MST and duplicate weights test");
    }

    private static void testTopologicalSortDAGAndCycles() {
        int v = 3;

        List<List<Integer>> dag = new ArrayList<>();
        for (int i = 0; i < v; i++) {
            dag.add(new ArrayList<>());
        }
        dag.get(0).add(1);
        dag.get(1).add(2);

        List<Integer> order = TopologicalSort.sort(v, dag);
        if (order == null) {
            throw new RuntimeException("Topological order must exist for a DAG");
        }
        if (order.indexOf(0) >= order.indexOf(1) || order.indexOf(1) >= order.indexOf(2)) {
            throw new RuntimeException("Incorrect topological order for DAG");
        }

        List<List<Integer>> cyclic = new ArrayList<>();
        for (int i = 0; i < v; i++) {
            cyclic.add(new ArrayList<>());
        }
        cyclic.get(0).add(1);
        cyclic.get(1).add(2);
        cyclic.get(2).add(0);

        List<Integer> cycleOrder = TopologicalSort.sort(v, cyclic);
        if (cycleOrder != null) {
            throw new RuntimeException("Topological sort must return null for cyclic graph");
        }

        System.out.println("[PASS] Topological sort (DAG & Cycle) test");
    }

    private static void testSCCProperties() {
        int v = 5;
        List<List<Integer>> adj = new ArrayList<>();
        for (int i=0; i<v; i++) {
            adj.add(new ArrayList<>());
        }

        adj.get(0).add(1);
        adj.get(1).add(2);
        adj.get(2).add(0);

        adj.get(2).add(3);

        adj.get(3).add(4);
        adj.get(4).add(3);

        List<List<Integer>> sccs = KosarajuSCC.findSCCs(v, adj);
        if (sccs.size()!=2) {
            throw new RuntimeException("Expected exactly 2 SCC components");
        }

        boolean[] seen = new boolean[v];
        int count = 0;
        for (List<Integer> comp : sccs) {
            for (int u : comp) {
                if (seen[u]) {
                    throw new RuntimeException("Vertex "+u+" appears in more than one SCC");
                }
                seen[u] = true;
                count++;
            }
        }
        if (count!=v) {
            throw new RuntimeException("Not all vertices are assigned to an SCC");
        }

        System.out.println("[PASS] SCC property test");
    }

    private static void testDijkstraUnreachableAndDistances() {
        int v = 4;
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(0, 1, 10));
        edges.add(new Edge(1, 2, 20));

        List<List<Edge>> adj = createAdj(v, edges);
        Dijkstra.Result res = Dijkstra.findShortestPaths(v, adj, 0);

        if (res.dist[0] != 0) {
            throw new RuntimeException("Distance to source must be 0");
        }
        if (res.dist[1] != 10) {
            throw new RuntimeException("Shortest distance to 1 must be 10");
        }
        if (res.dist[2] != 30) {
            throw new RuntimeException("Shortest distance to 2 must be 30");
        }
        if (res.dist[3] != Integer.MAX_VALUE) {
            throw new RuntimeException("Vertex 3 must be unreachable");
        }

        List<Integer> path = res.getPath(2);
        if (path.size()!=3 || path.get(0)!=0 || path.get(1)!=1 || path.get(2)!=2) {
            throw new RuntimeException("Expected path to 2 is [0, 1, 2]");
        }
        if (!res.getPath(3).isEmpty()) {
            throw new RuntimeException("Path to unreachable vertex must be empty");
        }

        System.out.println("[PASS] Dijkstra distances & unreachable test");
    }

    private static List<List<Edge>> createAdj(int v, List<Edge> edges) {
        List<List<Edge>> adj = new ArrayList<>();
        for (int i=0; i<v; i++) {
            adj.add(new ArrayList<>());
        }
        for (Edge e : edges) {
            adj.get(e.u).add(e);
            adj.get(e.v).add(new Edge(e.v, e.u, e.weight));
        }
        return adj;
    }

    private static List<List<Integer>> toDirAdj(int v, List<Edge> edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i=0; i<v; i++) {
            adj.add(new ArrayList<>());
        }
        for (Edge e : edges) {
            adj.get(e.u).add(e.v);
        }
        return adj;
    }
}