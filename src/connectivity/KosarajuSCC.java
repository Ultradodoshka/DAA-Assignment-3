package connectivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KosarajuSCC {
    public static List<List<Integer>> findSCCs(int numVertices, List<List<Integer>> adj) {
        boolean[] visited = new boolean[numVertices];
        List<Integer> order = new ArrayList<>();

        for (int i=0; i<numVertices; i++) {
            if (!visited[i]){
                dfsOrder(i, adj, visited, order);
            }
        }

        List<List<Integer>> transposed = new ArrayList<>();
        for (int i = 0; i < numVertices; i++) {
            transposed.add(new ArrayList<>());
        }
        for (int u = 0; u < numVertices; u++) {
            for (int v : adj.get(u)) {
                transposed.get(v).add(u);
            }
        }

        List<List<Integer>> components = new ArrayList<>();
        visited = new boolean[numVertices];
        Collections.reverse(order);

        for (int u : order) {
            if (!visited[u]) {
                List<Integer> component = new ArrayList<>();
                dfsCollect(u, transposed, visited, component);
                components.add(component);
            }
        }
        return components;
    }

    private static void dfsOrder(int u, List<List<Integer>> adj, boolean[] visited, List<Integer> order) {
        visited[u] = true;
        for (int v : adj.get(u)) {
            if (!visited[v]) {
                dfsOrder(v, adj, visited, order);
            }
        }
        order.add(u);
    }

    private static void dfsCollect(int u, List<List<Integer>> transposed, boolean[] visited, List<Integer> component) {
        visited[u] = true;
        component.add(u);
        for (int v : transposed.get(u)) {
            if (!visited[v]) {
                dfsCollect(v, transposed, visited, component);
            }
        }
    }
}