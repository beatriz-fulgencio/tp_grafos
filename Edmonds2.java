import java.util.*;

public class Edmonds2 {
    private static class Edge {
        int from;
        int to;
        int cost;

        public Edge(int from, int to, int cost) {
            this.from = from;
            this.to = to;
            this.cost = cost;
        }
    }

    public static List<Edge> branchAndRoot(List<List<Edge>> graph) {
        int numVertices = graph.size();
        List<Edge> H = new ArrayList<>();

        while (true) {
            int[] parent = new int[numVertices];
            Arrays.fill(parent, -1);

            for (int u = 0; u < numVertices; u++) {
                if (u == 0 || parent[u] != -1) continue;

                int bestIncoming = -1;
                for (Edge edge : graph.get(u)) {
                    int v = edge.to;
                    if (bestIncoming == -1 || edge.cost > graph.get(u).get(bestIncoming).cost) {
                        bestIncoming = graph.get(u).indexOf(edge);
                    }
                }

                int v = graph.get(u).get(bestIncoming).to;
                parent[v] = u;

                int x = u;
                int w;
                while (x != 0) {
                    w = x;
                    x = parent[x];
                    parent[w] = v;
                    v = w;
                }
            }

            int u = findRootComponent(numVertices, parent);
            if (u == -1) {
                break;
            }

            int v = contractComponent(u, parent, H, graph);

            if (v != -1) {
                List<Integer> contractVertices = new ArrayList<>();
                contractVertices.add(v);
                while (v != u) {
                    v = parent[v];
                    contractVertices.add(v);
                }
                findCycles(u, contractVertices, H, graph);
                unblock(u, H, graph);
            }
        }

        return H;
    }

    public static void rootAlgorithm(List<Edge> H, List<List<Edge>> graph) {
        while (true) {
            int[] parent = new int[graph.size()];
            Arrays.fill(parent, -1);

            int R = findRootComponent(graph, H, parent);
            if (R == -1) {
                break;
            }

            List<Integer> contractVertices = new ArrayList<>();
            int u = R;
            while (parent[u] != R) {
                contractVertices.add(u);
                u = parent[u];
            }
            contractVertices.add(R);

            int x_j = findMinimumValueEdge(contractVertices, H, graph);

            if (x_j != -1) {
                H.removeIf(edge -> (edge.from == x_j || edge.to == x_j));
                int S_j = contractComponent(x_j, parent, H, graph);
                parent[S_j] = R;
            }
        }
    }

    public static void main(String[] args) {
        List<List<Edge>> graph = new ArrayList<>();
        graph.add(Arrays.asList(new Edge(1, 3,0), new Edge(2, 2,0)));
        graph.add(Collections.singletonList(new Edge(2, 1,0)));
        graph.add(Arrays.asList(new Edge(0, 1,0), new Edge(3, 4,0)));
        graph.add(Collections.emptyList());

        List<Edge> H = branchAndRoot(graph);
        rootAlgorithm(H, graph);

        System.out.println("Optimum Branching:");
        for (Edge edge : H) {
            System.out.println(edge.from + " -> " + edge.to);
        }
    }

    private static int findRootComponent(int numVertices, int[] parent) {
        for (int u = 1; u < numVertices; u++) {
            if (parent[u] == -1) {
                return u;
            }
        }
        return -1;
    }

    private static int contractComponent(int u, int[] parent, List<Edge> H, List<List<Edge>> graph) {
        int S = u;
        List<Integer> contractVertices = new ArrayList<>();
        while (parent[u] != u) {
            contractVertices.add(u);
            u = parent[u];
        }
        contractVertices.add(u);

        for (int v : contractVertices) {
            for (Edge edge : graph.get(v)) {
                int w = edge.to;
                if (contractVertices.contains(w)) {
                    continue;
                }
                H.add(edge);
            }
        }

        return S;
    }

    private static void findCycles(int u, List<Integer> contractVertices, List<Edge> H, List<List<Edge>> graph) {
        Set<Integer> cycleVertices = new HashSet<>(contractVertices);
        int S = u;

        for (int v : contractVertices) {
            for (Edge edge : graph.get(v)) {
                if (cycleVertices.contains(edge.to)) {
                    S = v;
                }
            }
        }

        while (S != u) {
            for (Edge edge : H) {
                if (contractVertices.contains(edge.from) && edge.to == S) {
                    S = edge.from;
                    break;
                }
            }
        }

        for (int i = 0; i < contractVertices.size(); i++) {
            int x_i = contractVertices.get(i);
            int y_i = contractVertices.get((i + 1) % contractVertices.size());
            int x_j = contractVertices.get((i + 1) % contractVertices.size());
            int y_j = S;
            int minEdgeValue = Integer.MAX_VALUE;

            for (Edge edge : H) {
                if (edge.from == x_i && edge.to == y_i) {
                    minEdgeValue = Math.min(minEdgeValue, edge.cost);
                }
            }

            for (Edge edge : H) {
                if (edge.from == x_j && edge.to == y_j) {
                    minEdgeValue = Math.min(minEdgeValue, edge.cost);
                }
            }

            for (Edge edge : H) {
                if (edge.from == x_i && edge.to == y_i) {
                    edge.cost -= minEdgeValue;
                }
                if (edge.to == x_i && edge.from == y_i) {
                    edge.cost += minEdgeValue;
                }
                if (edge.from == x_j && edge.to == y_j) {
                    edge.cost -= minEdgeValue;
                }
                if (edge.to == x_j && edge.from == y_j) {
                    edge.cost += minEdgeValue;
                }
            }
        }
    }

    private static void unblock(int u, List<Edge> H, List<List<Edge>> graph) {
        for (Edge edge : graph.get(u)) {
            int v = edge.to;
            for (Edge h : H) {
                if (h.from == u && h.to == v) {
                    return;
                }
            }
        }
    }

    private static int findRootComponent(List<List<Edge>> graph, List<Edge> H, int[] parent) {
        Set<Integer> roots = new HashSet<>();
        for (Edge edge : H) {
            roots.add(edge.from);
        }
        for (int u = 1; u < graph.size(); u++) {
            if (!roots.contains(u)) {
                return u;
            }
        }
        return -1;
    }

    private static int findMinimumValueEdge(List<Integer> contractVertices, List<Edge> H, List<List<Edge>> graph) {
        int minEdgeValue = Integer.MAX_VALUE;
        int x_j = -1;
        for (int x : contractVertices) {
            for (Edge edge : H) {
                if (edge.from == x) {
                    if (edge.cost < minEdgeValue) {
                        minEdgeValue = edge.cost;
                        x_j = x;
                    }
                }
            }
        }
        return x_j;
    }
}
