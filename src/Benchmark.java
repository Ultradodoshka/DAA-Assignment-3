import dsu.DSU;
import graph.Edge;
import mst.Kruskal;
import mst.Prim;
import connectivity.TopologicalSort;
import connectivity.KosarajuSCC;
import shortestpath.Dijkstra;

import java.util.*;

public class Benchmark {
    public static List<Edge> generateConnectedGraph(int v, int targetEdges, Random rnd) {
        List<Edge> edges = new ArrayList<>();
        Set<String> existing = new HashSet<>();

        for (int i=1; i<v; i++) {
            int p = rnd.nextInt(i);
            int weight = rnd.nextInt(100)+1;
            edges.add(new Edge(p, i, weight));
            existing.add(Math.min(p, i)+"-"+Math.max(p, i));
        }

        while (edges.size()<targetEdges) {
            int u = rnd.nextInt(v);
            int w = rnd.nextInt(v);
            if (u==w) continue;
            String key = Math.min(u, w)+"-"+Math.max(u, w);
            if (!existing.contains(key)) {
                existing.add(key);
                edges.add(new Edge(u, w, rnd.nextInt(100) + 1));
            }
        }
        return edges;
    }

    public static List<List<Edge>> toUndirectedAdj(int v, List<Edge> edges) {
        List<List<Edge>> adj = new ArrayList<>();
        for (int i = 0; i<v; i++) adj.add(new ArrayList<>());
        for (Edge e : edges) {
            adj.get(e.u).add(e);
            adj.get(e.v).add(new Edge(e.v, e.u, e.weight));
        }
        return adj;
    }

    public static List<List<Integer>> generateDAG(int v, int targetEdges, Random rnd) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i=0; i<v; i++) adj.add(new ArrayList<>());
        Set<String> existing = new HashSet<>();
        int count = 0;

        while (count < targetEdges) {
            int u = rnd.nextInt(v);
            int w = rnd.nextInt(v);
            if (u>=w) continue;
            String key = u+"->"+w;
            if (!existing.contains(key)) {
                existing.add(key);
                adj.get(u).add(w);
                count++;
            }
        }
        return adj;
    }

    public static void main(String[] args) {
        int[][] configs = {{10, 30}, {100, 500}, {1000, 5000}};

        System.out.println("Start benchmark");

        for (int[] cfg : configs) {
            int v = cfg[0];
            int e = cfg[1];
            System.out.println("\nConfiguration: V = "+v+", E = "+e);

            Random rnd = new Random(42);
            List<Edge> edges = generateConnectedGraph(v, e, rnd);
            List<List<Edge>> undirAdj = toUndirectedAdj(v, edges);
            List<List<Integer>> dagAdj = generateDAG(v, e, rnd);

            List<List<Integer>> dirAdj = new ArrayList<>();
            for (int i = 0; i < v; i++) dirAdj.add(new ArrayList<>());
            for (Edge edge : edges) dirAdj.get(edge.u).add(edge.v);

            long totalTime = 0;
            for (int run =0; run<5; run++) {
                long start = System.nanoTime();
                Kruskal.findMST(v, edges);
                totalTime += (System.nanoTime() - start);
            }
            System.out.println("  Kruskal Avg Time: "+(totalTime/5)+" ns");

            totalTime = 0;
            for (int run=0; run<5; run++) {
                long start = System.nanoTime();
                Prim.findMST(v, undirAdj);
                totalTime += (System.nanoTime() - start);
            }
            System.out.println("  Prim Avg Time:    "+(totalTime/5)+" ns");


            totalTime = 0;
            for (int run =0; run<5; run++) {
                long start = System.nanoTime();
                Dijkstra.findShortestPaths(v, undirAdj, 0);
                totalTime += (System.nanoTime() - start);
            }
            System.out.println("  Dijkstra Avg:     " + (totalTime/5) + " ns");

            totalTime = 0;
            for (int run=0; run<5; run++) {
                long start = System.nanoTime();
                TopologicalSort.sort(v, dagAdj);
                totalTime += (System.nanoTime() - start);
            }
            System.out.println("  TopoSort Avg:     " + (totalTime/5) + " ns");

            totalTime = 0;
            for (int run = 0; run<5; run++) {
                long start = System.nanoTime();
                KosarajuSCC.findSCCs(v, dirAdj);
                totalTime += (System.nanoTime() - start);
            }
            System.out.println("  Kosaraju Avg:     " + (totalTime/5) + " ns");
        }
    }
}