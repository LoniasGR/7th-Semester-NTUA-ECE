#include <stdio.h>
#include <stdlib.h>
#include <limits.h>
#include <stdbool.h>

// A structure to represent a node in adjacency list
//each node is an edge between two nodes
struct AdjListNode
{
    int dest;
     int weight;
    struct AdjListNode* next;
};

// A structure to represent an adjacency list
struct AdjList
{
    struct AdjListNode *head;  // pointer to head node of list
};

// A structure to represent a graph. A graph is an array of adjacency lists.
// Size of array will be V (number of vertices in graph)
struct Graph
{
    int V;
    struct AdjList* array;
};

// A utility function to create a new adjacency list node
struct AdjListNode* newAdjListNode(int dest, int weight)
{
    struct AdjListNode* newNode =
            (struct AdjListNode*) malloc(sizeof(struct AdjListNode));
    newNode->dest = dest;
    newNode->weight = weight;
    newNode->next = NULL;
    return newNode;
}

// A utility function that creates a graph of V vertices
struct Graph* createGraph(int V)
{
    struct Graph* graph = (struct Graph*) malloc(sizeof(struct Graph));
    graph->V = V;

    // Create an array of adjacency lists.  Size of array will be V
    graph->array = (struct AdjList*) malloc(V * sizeof(struct AdjList));

     // Initialize each adjacency list as empty by making head as NULL
    for (int i = 0; i < V; ++i)
        graph->array[i].head = NULL;

    return graph;
}

// Adds an edge to an undirected graph
void addEdge(struct Graph* graph, int src, int dest,  int weight)
{
    // Add an edge from src to dest.  A new node is added to the adjacency
    // list of src.  The node is added at the begining
    struct AdjListNode* newNode = newAdjListNode(dest, weight);
    newNode->next = graph->array[src].head;
    graph->array[src].head = newNode;

    // Since graph is undirected, add an edge from dest to src also
    newNode = newAdjListNode(src, weight);
    newNode->next = graph->array[dest].head;
    graph->array[dest].head = newNode;
}

// Structure to represent a min heap node
struct MinHeapNode
{
    int  v;
     int key;
};

// Structure to represent a min heap
struct MinHeap
{
    int size;      // Number of heap nodes present currently
    int capacity;  // Capacity of min heap
    int *pos;     // This is needed for decreaseKey()
    struct MinHeapNode **array;
};

// A utility function to create a new Min Heap Node
struct MinHeapNode* newMinHeapNode(int v, int key)
{
    struct MinHeapNode* minHeapNode =
           (struct MinHeapNode*) malloc(sizeof(struct MinHeapNode));
    minHeapNode->v = v;
    minHeapNode->key = key;
    return minHeapNode;
}

// A utilit function to create a Min Heap
struct MinHeap* createMinHeap(int capacity)
{
    struct MinHeap* minHeap =
         (struct MinHeap*) malloc(sizeof(struct MinHeap));
    minHeap->pos = (int *)malloc(capacity * sizeof(int));
    minHeap->size = 0;
    minHeap->capacity = capacity;
    minHeap->array =
         (struct MinHeapNode**) malloc(capacity * sizeof(struct MinHeapNode*));
    return minHeap;
}

// A utility function to swap two nodes of min heap. Needed for min heapify
void swapMinHeapNode(struct MinHeapNode** a, struct MinHeapNode** b)
{
    struct MinHeapNode* t = *a;
    *a = *b;
    *b = t;
}

// A standard function to heapify at given idx
// This function also updates position of nodes when they are swapped.
// Position is needed for decreaseKey()
void minHeapify(struct MinHeap* minHeap, int idx)
{
    int smallest, left, right;
    smallest = idx;
    left = 2 * idx + 1;
    right = 2 * idx + 2;

    if (left < minHeap->size &&
        minHeap->array[left]->key < minHeap->array[smallest]->key )
      smallest = left;

    if (right < minHeap->size &&
        minHeap->array[right]->key < minHeap->array[smallest]->key )
      smallest = right;

    if (smallest != idx)
    {
        // The nodes to be swapped in min heap
        struct MinHeapNode *smallestNode = minHeap->array[smallest];
        struct MinHeapNode *idxNode = minHeap->array[idx];

        // Swap positions
        minHeap->pos[smallestNode->v] = idx;
        minHeap->pos[idxNode->v] = smallest;

        // Swap nodes
        swapMinHeapNode(&minHeap->array[smallest], &minHeap->array[idx]);

        minHeapify(minHeap, smallest);
    }
}

// A utility function to check if the given minHeap is ampty or not
int isEmpty(struct MinHeap* minHeap)
{
    return minHeap->size == 0;
}

// Standard function to extract minimum node from heap
struct MinHeapNode* extractMin(struct MinHeap* minHeap)
{
    if (isEmpty(minHeap))
        return NULL;

    // Store the root node
    struct MinHeapNode* root = minHeap->array[0];

    // Replace root node with last node
    struct MinHeapNode* lastNode = minHeap->array[minHeap->size - 1];
    minHeap->array[0] = lastNode;

    // Update position of last node
    minHeap->pos[root->v] = minHeap->size-1;
    minHeap->pos[lastNode->v] = 0;

    // Reduce heap size and heapify root
    --minHeap->size;
    minHeapify(minHeap, 0);

    return root;
}

// Function to decreasy key value of a given vertex v. This function
// uses pos[] of min heap to get the current index of node in min heap
void decreaseKey(struct MinHeap* minHeap, int v, int key)
{
    // Get the index of v in  heap array
    int i = minHeap->pos[v];

    // Get the node and update its key value
    minHeap->array[i]->key = key;

    // Travel up while the complete tree is not hepified.
    // This is a O(Logn) loop
    while (i && minHeap->array[i]->key < minHeap->array[(i - 1) / 2]->key)
    {
        // Swap this node with its parent
        minHeap->pos[minHeap->array[i]->v] = (i-1)/2;
        minHeap->pos[minHeap->array[(i-1)/2]->v] = i;
        swapMinHeapNode(&minHeap->array[i],  &minHeap->array[(i - 1) / 2]);

        // move to parent index
        i = (i - 1) / 2;
    }
}

// A utility function to check if a given vertex
// 'v' is in min heap or not
bool isInMinHeap(struct MinHeap *minHeap, int v)
{
   if (minHeap->pos[v] < minHeap->size)
     return true;
   return false;
}

// A utility function used to print the constructed MST
void printArr(int arr[], int n)
{
    for (int i = 1; i < n; ++i)
        printf("%d - %d\n", arr[i], i);
}

void createMST(struct Graph* MST, int parent[], int key[], int V) {
    for(int i = 1; i <V ; i++)
        addEdge(MST, parent[i], i, key[i]);

}

// The main function that constructs Minimum Spanning Tree (MST)
// using Prim's algorithm
struct Graph* PrimMST(struct Graph* graph)
{
    int V = graph->V;// Get the number of vertices in graph
    int parent[V];   // Array to store constructed MST
    int key[V];  // Key values used to pick minimum weight edge in cut

    struct Graph* MST = createGraph(V);

    // minHeap represents set E
    struct MinHeap* minHeap = createMinHeap(V);

    // Initialize min heap with all vertices. Key value of
    // all vertices (except 0th vertex) is initially infinite
    for (int v = 1; v < V; ++v)
    {
        parent[v] = -1;
        key[v] = INT_MAX;
        minHeap->array[v] = newMinHeapNode(v, key[v]);
        minHeap->pos[v] = v;
    }

    // Make key value of 0th vertex as 0 so that it
    // is extracted first
    key[0] = 0;
    minHeap->array[0] = newMinHeapNode(0, key[0]);
    minHeap->pos[0]   = 0;

    // Initially size of min heap is equal to V
    minHeap->size = V;

    // In the followin loop, min heap contains all nodes
    // not yet added to MST.
    while (!isEmpty(minHeap))
    {
        // Extract the vertex with minimum key value
        struct MinHeapNode* minHeapNode = extractMin(minHeap);
        int u = minHeapNode->v; // Store the extracted vertex number

        // Traverse through all adjacent vertices of u (the extracted
        // vertex) and update their key values
        struct AdjListNode* pCrawl = graph->array[u].head;
        while (pCrawl != NULL)
        {
            int v = pCrawl->dest;

            // If v is not yet included in MST and weight of u-v is
            // less than key value of v, then update key value and
            // parent of v
            if (isInMinHeap(minHeap, v) && pCrawl->weight < key[v])
            {
                key[v] = pCrawl->weight;
                parent[v] = u;
                decreaseKey(minHeap, v, key[v]);
            }
            pCrawl = pCrawl->next;
        }
    }

    createMST(MST, parent, key, V);
    return MST;
}

struct trips {
    int source;
    int destination;
    struct trips* next;
};

struct tripsList {
    int Q;
    struct trips* head;
};

struct tripsList* createList(int Q) {
    struct tripsList* List = (struct tripsList*)
        malloc(sizeof(struct tripsList));
    List->Q = Q;
    List->head = NULL;

    return List;
}

void addElement(struct tripsList* list, int startNode, int endNode) {
    if (list->head == NULL) {
        struct trips* node = (struct trips*) malloc (sizeof(struct trips));
        node->source = startNode;
        node->destination = endNode;
        node->next = NULL;
        list->head = node;
    }
    else {
    struct trips* temp = list->head;
    while(temp->next != NULL)
        temp = temp->next;
    struct trips* node = (struct trips*) malloc (sizeof(struct trips));
    node->source = startNode;
    node->destination = endNode;
    node->next = NULL;
    temp->next = node;
    }
}
void DFS (struct Graph* graph, int start, bool* visited, int* cost) {
    int result;
    visited[start] = true;


    struct AdjListNode* edges = graph->array[start].head;

    while (edges != NULL) {
        if(!visited[edges->dest]) {

            cost[edges->dest] = cost[start] + edges->weight;
            cost[edges->dest] =
                cost[start] > edges->weight ? cost[start] : edges->weight;
            visited[edges->dest] = true;
            DFS(graph, edges->dest, visited, cost);

        }
        edges = edges->next;
    }
}
void DFS_minimax (struct Graph* MST, struct tripsList* list, int V) {
    bool visited[V];
    int cost[V];
    struct trips* previous = NULL;
    struct trips* current = NULL;

    for (int cntr = 0; cntr < list->Q; cntr++) {
        previous = current;
        current = list->head;
        list->head = list->head->next;
        if (previous == NULL || ((previous->source) != (current->source))) {
            for (int i = 0; i < V; i++) {
                visited[i] = false;
                cost[i] = 0;
            }
            DFS(MST, current->source, visited, cost);
            printf("%d\n", cost[current->destination]);
        }
        else {
            printf("%d\n", cost[current->destination]);
        }
    }
}
int main (int argc, char **argv) {
	FILE *fp;
	if (argc == 1) {
	fp = stdin;
	}
	else {
	 fp = fopen(argv[1], "r");
	}

    int N, M, Q;
    fscanf(fp, "%d %d\n", &N, &M);

    struct Graph* graph = createGraph(N);

    for (int cntr = 0; cntr < M; cntr++)
    {
        int srcNode, dstNode;
        int cost;
        fscanf(fp, "%d %d %d \n", &srcNode, &dstNode, &cost);
        addEdge(graph,  srcNode-1, dstNode-1, cost);
    }

    fscanf(fp, "%d\n", &Q);
    struct tripsList* list = createList(Q);

    for (int cntr2 = 0; cntr2 < Q; cntr2++) {
        int startNode, endNode;
        fscanf(fp, "%d %d\n", &startNode, &endNode);
        addElement(list, startNode-1, endNode-1);
    }

    struct Graph* MST;
    MST = PrimMST(graph);
    DFS_minimax(MST, list,N);
    return 0;
}
