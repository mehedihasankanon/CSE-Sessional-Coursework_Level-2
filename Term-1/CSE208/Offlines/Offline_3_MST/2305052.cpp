#include <bits/stdc++.h>

using namespace std;

using ll = long long;
using pll = pair<ll, ll>;

// string algortihm = "prims";
string algortihm = "kruskals";

// ===================================================================================================
// Prim's Algorithm

void Prims()
{
    ll n, m;
    cin >> n >> m;

    vector<vector<pll>> adjList(n + 1); // stores {<cost, neighbour>}

    for (ll i = 0; i < m; i++)
    {
        ll a, b, c;
        cin >> a >> b >> c;

        adjList[a].push_back({c, b});
        adjList[b].push_back({c, a});
    }

    ll startNode;
    cin >> startNode;

    vector<pll> mstEdges;

    priority_queue<tuple<ll, ll, ll>, vector<tuple<ll, ll, ll>>, greater<tuple<ll, ll, ll>>> pq;
    // <cost, current_node, parent_node>

    ll mstCost = 0;
    vector<bool> vis(n, false);

    pq.push({0, startNode, -1});

    while (!pq.empty())
    {
        auto cur = pq.top();
        pq.pop();

        ll cost = get<0>(cur);
        ll node = get<1>(cur);
        ll parent = get<2>(cur);

        if (vis[node])
            continue;

        vis[node] = true;
        mstCost += cost;

        if (parent != -1)
        {
            mstEdges.push_back({parent, node});
        }

        for (auto it : adjList[node])
        {
            if (!vis[it.second])
            {
                pq.push({it.first, it.second, node});
            }
        }
    }

    cout << "Total weight " << mstCost << endl;
    cout << "Root node " << startNode << endl;
    for (auto x : mstEdges)
    {
        cout << x.first << " " << x.second << endl;
    }
    return;
}

// ==============================================================================================
// Kruskal's Algorithm

using Edge = struct edge
{
    ll cost, from, to;

    bool operator<(const edge &a) const
    {
        if (cost != a.cost)
            return cost < a.cost;
        if (from != a.from)
            return from < a.from;
        return to < a.to;
    }
};

using DSU = struct DSU
{
    vector<ll> parent, rank;

    DSU(ll n) : parent(n + 1), rank(n + 1, 0)
    {
        for (ll i = 0; i <= n; i++)
            parent[i] = i;
    }

    int find(ll element)
    {
        if (parent[element] == element)
        {
            return element;
        }
        return parent[element] = find(parent[element]);
    }

    bool is_same_component(ll element1, ll element2)
    {
        return find(element1) == find(element2);
    }

    ll merge(ll element1, ll element2)
    {
        ll root1 = find(element1);
        ll root2 = find(element2);

        if (root1 == root2)
        {
            return -1;
        }

        if (rank[root1] > rank[root2])
            swap(root1, root2);

        parent[root1] = root2;

        if (rank[root2] == rank[root1])
            rank[root2]++;

        return root2;
    }
};

void Kruskals()
{
    ll n, m;
    cin >> n >> m;

    vector<Edge> edges(m);

    for (ll i = 0; i < m; i++)
    {
        cin >> edges[i].from >> edges[i].to >> edges[i].cost;
    }

    ll startNode;
    cin >> startNode;

    sort(edges.begin(), edges.end());

    DSU dsu(n);
    vector<pll> mstEdges;
    ll mstCost = 0;

    for (ll i = 0; i < m; i++)
    {
        if (!dsu.is_same_component(edges[i].from, edges[i].to))
        {
            mstEdges.push_back({edges[i].from, edges[i].to});
            mstCost += edges[i].cost;
            dsu.merge(edges[i].from, edges[i].to);
        }
    }

    cout << "Total weight " << mstCost << endl;
    for (auto x : mstEdges)
    {
        cout << x.first << " " << x.second << endl;
    }
    return;
}

// =============================================================================================

int main()
{

    for (int i = 1; i <= 10; i++)
    {
        freopen(("./sampleio/input/test" + to_string(i) + ".txt").c_str(), "r", stdin);
        freopen(("./test_output/output" + to_string(i) + ".txt").c_str(), "w", stdout);

        cin.clear();

        // cout << "Test " << i << endl;
        if (algortihm == "prims")
            Prims();
        else
            Kruskals();

        cout << endl;
    }
}
