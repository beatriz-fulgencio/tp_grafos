import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Edmonds {
    private final List<List<Edge>> graph; // Representa o grafo como uma lista de listas de arestas.
    private final int numVertices; // Número de vértices no grafo.
    private int[] incomingEdge; // Para cada vértice, mantém a aresta de entrada.
    private int[] incomingEdgeCost; // Para cada vértice, mantém o custo da aresta de entrada.
    private int root; // Raiz a partir da qual o branching será encontrado.

    public Edmonds(List<List<Edge>> graph) {
        this.graph = graph;
        this.numVertices = graph.size();
        this.incomingEdge = new int[numVertices];
        this.incomingEdgeCost = new int[numVertices];
        Arrays.fill(this.incomingEdge, 0); // Inicializa as arestas de entrada como 0.
        Arrays.fill(this.incomingEdgeCost, Integer.MAX_VALUE); // Inicializa os custos das arestas de entrada como infinito.
    }

    public void run() {
        // Encontra o branching ótimo a partir da raiz especificada.
        for (int v = 0; v < numVertices; v++) {
            if (v != root) {
                for (int u = 0; u < numVertices; u++) {
                    for (Edge edge : graph.get(u)) {
                        int neighbor = edge.to;
                        int cost = edge.cost;
                        if (incomingEdgeCost[neighbor] > cost) {
                            incomingEdgeCost[neighbor] = cost;
                            incomingEdge[neighbor] = u;
                        }
                    }
                }
            }
        }

        for (int v = 0; v < numVertices; v++) {
            if (v != root) {
                int u = v;
                boolean[] seen = new boolean[numVertices];
                while (true) {
                    if (seen[u] || u == root) {
                        break;
                    }
                    seen[u] = true;
                    u = incomingEdge[u];
                }

                if (u == root) {
                    continue;
                }

                u = incomingEdge[u];
                while (v != u) {
                    incomingEdge[v] = u;
                    v = u;
                    u = incomingEdge[u];
                }
            }
        }
    }

    public List<Edge> findOptimumBranching(int root) {
        // Encontra o branching ótimo a partir da raiz especificada e retorna as arestas no branching.
        List<Edge> result = new ArrayList<>();
        for (int v = 0; v < numVertices; v++) {
            if (v != root) {
                result.add(new Edge(incomingEdge[v], v, incomingEdgeCost[v]));
            }
        }
        return result;
    }

    public static class Edge {
        int from; // Vértice de origem.
        int to; // Vértice de destino.
        int cost; // Custo da aresta.

        public Edge(int from, int to, int cost) {
            this.from = from;
            this.to = to;
            this.cost = cost;
        }
    }

    public static void main(String[] args) {
        String filePath = "edges.txt"; // Especifique o caminho para o arquivo de arestas.
        List<List<Edge>> graph = readFile(filePath); // Lê as arestas do arquivo e cria o grafo.

        Scanner sc = new Scanner(System.in);
        System.out.println("Escolha a raiz: ");
        int root = sc.nextInt(); // Solicita ao usuário para escolher a raiz.

        Edmonds edmondsOptimum = new Edmonds(graph);
        edmondsOptimum.root = root; // Define a raiz no objeto Edmonds.
        edmondsOptimum.run(); // Executa o algoritmo para encontrar o branching ótimo.

        List<Edge> optimumBranching = edmondsOptimum.findOptimumBranching(root); // Encontra o branching ótimo.

        if (!optimumBranching.isEmpty()) {
            System.out.println("Optimum Branching:");
            for (Edge edge : optimumBranching) {
                if(edge.cost < Integer.MAX_VALUE) {
                    System.out.println(edge.from + " -> " + edge.to + " - Cost: " + edge.cost);
                }
            }
        } else {
            System.out.println("No optimum branching exists.");
        }
    }

    public static List<List<Edge>> readFile(String filePath) {
        // Lê as arestas de um arquivo e cria o grafo.
        List<List<Edge>> graph = new ArrayList<>();

        try (Scanner sc = new Scanner(new File(filePath))) {
            int numVertices = sc.nextInt();
            int numEdges = sc.nextInt();

            for (int i = 0; i < numVertices; i++) {
                graph.add(new ArrayList<>());
            }

            while (sc.hasNext()) {
                int from = sc.nextInt();
                int to = sc.nextInt();
                int cost = sc.nextInt();

                graph.get(from).add(new Edge(from, to, cost));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return graph;
    }
}
