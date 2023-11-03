import java.util.*;

class Vertex {
    int id;

    public Vertex(int id) {
        this.id = id;
    }
}

class Edge {
    Vertex u;
    Vertex v;
    int weight;

    public Edge(Vertex u, Vertex v, int weight) {
        this.u = u;
        this.v = v;
        this.weight = weight;
    }
}

class Graph {
    List<Vertex> vertices;
    List<Edge> edges;

    public Graph() {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public void addVertex(Vertex vertex) {
        vertices.add(vertex);
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    public List<Edge> getOutgoingEdges(Vertex vertex) {
        List<Edge> outgoingEdges = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.u == vertex) {
                outgoingEdges.add(edge);
            }
        }
        return outgoingEdges;
    }

    public List<Edge> getIncomingEdges(Vertex vertex) {
        List<Edge> incomingEdges = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.v == vertex) {
                incomingEdges.add(edge);
            }
        }
        return incomingEdges;
    }

    public void modifyEdgeValue(Edge edge, int newValue) {
        edge.weight = newValue;
    }
}

public class Edmonds3 {
    public static Graph branchAlgorithm(Graph graph) {
        Graph H = new Graph(); // Create a new graph for H
    
        while (true) {
            Set<Vertex> S = findRootComponent(graph, H);  // Step 1: Find a root component S
            if (S == null) {
                break;
            }
            Edge largestEdge = findLargestUnexaminedEdge(graph, S, H); // Step 2: Find the largest unexamined edge
            if (largestEdge == null) {
                continue;
            }
            Vertex u = largestEdge.u;
            Vertex v = largestEdge.v;
            if (S.contains(u)) {
                // u is already in the component, discard the edge.
            } else {
                Set<Vertex> W = findWeaklyConnectedComponent(graph, H, v); // Step 4: Find the weakly connected component W
                if (!W.contains(u)) {
                     // Step 4.1: If u is not in W, add (u,v) to H
                    H.addVertex(u); // Add vertices to H if not already present
                    H.addVertex(v);
                    H.addEdge(largestEdge); // Add the edge to H
                } else {
                    List<Edge> sequence = findSequence(graph, H, u, v, S, W);  // Step 5: Find the sequence
                    Edge minEdge = findMinEdge(sequence);  // Step 6: Find the minimum edge in the sequence
                    H.addEdge(minEdge); // Add the minimum edge to H // Step 7: Add (u,v) to H (combine S1, ..., Sk into a single component)
                    for (Edge e : sequence) {
                         // Step 7: Modify the values of unexamined edges
                        if (e != minEdge) {
                            graph.modifyEdgeValue(e, minEdge.weight);
                        }
                    }
                }
            }
        }
        return H;
    }

    private static Set<Vertex> findRootComponent(Graph graph, Graph H) {
                // Implement the logic for finding a root component as described in the algorithm.

        for (Vertex vertex : graph.vertices) {

            List<Edge> incomingEdges = graph.getIncomingEdges(vertex);

            boolean hasUnexaminedEdge = false;
            
            for (Edge edge : incomingEdges) {
                if (!H.edges.contains(edge) && edge.weight > 0) {
                    hasUnexaminedEdge = true;
                    break;
                }
            }
          if (hasUnexaminedEdge) {
                Set<Vertex> component = new HashSet<>();
                component.add(vertex);
                return component;
            }
        }
        return null;
    }
    

    private static Edge findLargestUnexaminedEdge(Graph graph, Set<Vertex> S, Graph H) {
        // Implement the logic for finding the largest unexamined edge as described in the algorithm.
        Edge largestEdge = null;
        int largestWeight = -1;

        for (Vertex vertex : S) {
            List<Edge> outgoingEdges = graph.getOutgoingEdges(vertex);
            for (Edge edge : outgoingEdges) {
                if (!H.edges.contains(edge) && edge.weight > largestWeight) {
                    largestWeight = edge.weight;
                    largestEdge = edge;
                }
            }
        }
        return largestEdge;
    }

    private static Set<Vertex> findWeaklyConnectedComponent(Graph graph, Graph H, Vertex v) {
        // Implement the logic for finding the weakly connected component as described in the algorithm.
        Set<Vertex> component = new HashSet<>();
        Queue<Vertex> queue = new LinkedList<>();
        queue.add(v);
        component.add(v);

        while (!queue.isEmpty()) {
            Vertex currentVertex = queue.poll();
            List<Edge> outgoingEdges = graph.getOutgoingEdges(currentVertex);

            for (Edge edge : outgoingEdges) {
                Vertex nextVertex = edge.v;

                if (!component.contains(nextVertex) && !H.edges.contains(edge) && edge.weight > 0) {
                    component.add(nextVertex);
                    queue.add(nextVertex);
                }
            }
        }

        return component;
    }

    private static List<Edge> findSequence(Graph graph, Graph H, Vertex u, Vertex v, Set<Vertex> S, Set<Vertex> W) {
        // Implement the logic for finding the sequence as described in the algorithm.
        List<Edge> sequence = new ArrayList<>();
        Vertex currentVertex = u;

        while (!currentVertex.equals(v)) {
            List<Edge> outgoingEdges = graph.getOutgoingEdges(currentVertex);
            for (Edge edge : outgoingEdges) {
                if (!H.edges.contains(edge) && edge.v.equals(v)) {
                    sequence.add(edge);
                    currentVertex = edge.u;
                    break;
                }
            }
        }

        return sequence;
    }

    private static Edge findMinEdge(List<Edge> sequence) {
        // Implement the logic for finding the edge with the minimum value in the sequence.
        Edge minEdge = sequence.get(0);

        for (Edge edge : sequence) {
            if (edge.weight < minEdge.weight) {
                minEdge = edge;
            }
        }

        return minEdge;
    }

    public static Graph rootAlgorithm(Graph graph, Graph H) {
        while (true) {
            Set<Vertex> R = findRootComponent(graph, H);  // Step 1: Find a root component R
            if (R == null) {
                break;
            }
            List<Edge> sequence = findSequence(graph, H, new ArrayList<Vertex>(R));  // Step 2: Find the sequence
            Edge minEdge = findMinEdge(sequence);  // Step 3: Find the minimum edge in the sequence
            H.edges.remove(minEdge);  // Step 4: Delete the minimum edge to make R a root component
        }
        return H;
    }

    private static List<Edge> findSequence(Graph graph, Graph H, ArrayList<Vertex> arrayList) {
        // Implement the logic for finding the sequence as described in the algorithm.
        List<Edge> sequence = new ArrayList<>();
        Vertex currentVertex = arrayList.get(0);

        while (!currentVertex.equals(arrayList.get(arrayList.size()-1))) {
            List<Edge> outgoingEdges = graph.getOutgoingEdges(currentVertex);
            for (Edge edge : outgoingEdges) {
                if (!H.edges.contains(edge) && edge.v.equals(arrayList.get(arrayList.size()-1))) {
                    sequence.add(edge);
                    currentVertex = edge.u;
                    break;
                }
            }
        }

        return sequence;
    }

    public static void main(String[] args) {
        // Create a graph, add vertices and edges to it.
        Graph graph = new Graph();
        Vertex v1 = new Vertex(1);
        Vertex v2 = new Vertex(2);
        Vertex v3 = new Vertex(3);
        graph.addVertex(v1);
        graph.addVertex(v2);
        graph.addVertex(v3);
        Edge e1 = new Edge(v1, v2, 5);
        Edge e2 = new Edge(v2, v3, 3);
        Edge e3 = new Edge(v3, v1, 2);
        graph.addEdge(e1);
        graph.addEdge(e2);
        graph.addEdge(e3);

        Graph result = branchAlgorithm(graph);
        for (Edge e : result.edges) {
            System.out.println("Edge: " + e.u.id + " - " + e.v.id + " Weight: " + e.weight);
        }
    }
}
