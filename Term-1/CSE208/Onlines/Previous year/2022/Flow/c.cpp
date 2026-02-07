#include <bits/stdc++.h>

using namespace std;

using ll = int;

struct Edge
{
    ll to, cap, curr_flow;
};

vector<ll> bfs(ll n, ll src, ll sink, vector<Edge> &edges, vector<vector<ll>> &adj)
{
    vector<ll> parent(n + 1, -1);

    queue<ll> q;
    q.push(src);
    parent[src] = src;

    while (!q.empty())
    {
        ll node = q.front();
        q.pop();

        if (node == sink)
        {
            vector<ll> augPath;

            ll t = sink;

            while (t != src)
            {
                augPath.push_back(t);
                t = parent[t];
            }

            augPath.push_back(src);

            reverse(augPath.begin(), augPath.end());

            return augPath;
        }

        for (ll idx : adj[node])
        {
            Edge &e = edges[idx];

            if (parent[e.to] == -1 && e.curr_flow < e.cap)
            {
                parent[e.to] = node;
                q.push(e.to);
            }
        }
    }

    return {};
}

void EdmondsKarp()
{
    ll n, m;
    cin >> n >> m;

    vector<Edge> edges;
    vector<pair<ll, ll>> forwardEdges;
    vector<vector<ll>> adj(n + 1); // adj[node] --> stores the indices of the outgoing edges

    for (ll i = 0; i < m; i++)
    {
        ll from, to, cap;
        cin >> from >> to;

        forwardEdges.push_back({from, to});

        adj[from].push_back(edges.size());
        edges.push_back({to, 1, 0});

        adj[to].push_back(edges.size());
        edges.push_back({from, 0, 0});
    }

    ll src = 1, sink = n;

    ll maxFlow = 0;

    while (true)
    {
        vector<ll> augPath = bfs(n, src, sink, edges, adj);

        if (augPath.empty())
            break;

        ll augPathFlow = 1e7;

        for (ll i = 0; i < augPath.size() - 1; i++)
        {
            ll from = augPath[i], to = augPath[i + 1];

            for (ll idx : adj[from])
            {
                if (edges[idx].to == to)
                {
                    augPathFlow = min(augPathFlow, edges[idx].cap - edges[idx].curr_flow);
                    break;
                }
            }
        }

        maxFlow += augPathFlow;

        for (ll i = 0; i < augPath.size() - 1; i++)
        {
            ll from = augPath[i], to = augPath[i + 1];

            for (ll idx : adj[from])
            {
                if (edges[idx].to == to)
                {
                    edges[idx].curr_flow += augPathFlow;
                    break;
                }
            }

            for (ll idx : adj[to])
            {
                if (edges[idx].to == from)
                {
                    edges[idx].curr_flow -= augPathFlow;
                    break;
                }
            }
        }
    }

    vector<vector<ll>> adj1(n + 1);
    map<pair<ll,ll>, bool> used;
    for(ll i = 1; i <= n; i++)
    {
        for(auto t : adj[i])
        {
            if(edges[t].cap > 0 and edges[t].curr_flow > 0)
            {
                adj1[i].push_back(edges[t].to);
                used[{i, edges[t].to}] = false;
            }
        }
    }

    /*
    // Alternate Solution:
    map<pair<ll, ll>, ll> flow;
    for (ll node = 1; node <= n; node++)
    {
        for (ll edgeIdx : adj[node])
        {
            Edge &e = edges[edgeIdx];
            if (e.cap > 0 && e.curr_flow > 0)
            {
                flow[{node, e.to}] = e.curr_flow;
            }
        }
    }

    // Step 2: Extract paths until no flow remains
    while (true)
    {
        // BFS to find a path with remaining flow
        vector<ll> parent(n + 1, -1);
        queue<ll> q;
        q.push(src);
        parent[src] = src;

        while (!q.empty())
        {
            ll node = q.front();
            q.pop();

            if (node == sink)
                break;

            for (ll edgeIdx : adj[node])
            {
                Edge &e = edges[edgeIdx];
                
                // Check if there's remaining flow on this edge
                if (parent[e.to] == -1 && flow[{node, e.to}] > 0)
                {
                    parent[e.to] = node;
                    q.push(e.to);
                }
            }
        }

        // No more paths found
        if (parent[sink] == -1)
            break;

        // Step 3: Reconstruct path and find minimum flow
        vector<ll> path;
        ll curr = sink;
        ll minFlow = 1e9;

        while (curr != src)
        {
            path.push_back(curr);
            ll prev = parent[curr];
            minFlow = min(minFlow, flow[{prev, curr}]);
            curr = prev;
        }
        path.push_back(src);
        reverse(path.begin(), path.end());

        // Step 4: Reduce flow along the path
        for (ll i = 0; i < path.size() - 1; i++)
        {
            flow[{path[i], path[i + 1]}] -= minFlow;
        }

        // Step 5: Print the path
        for (ll node : path)
            cout << node << " ";
        cout << endl;
    }

    cout << endl;

    */

    while(true)
    {
        vector<ll> parent(n + 2, -1);
        queue<ll> q; q.push(1);

        while(!q.empty())
        {
            ll cur = q.front();
            q.pop();

            // cerr << cur << ": ";
            // for(auto t : adj1[cur]) cerr << t << " ";
            // cerr << endl;

            for(auto child : adj1[cur])
            {
                if(!used[{cur, child}])
                {
                    cerr << cur << " " << child << endl;
                    parent[child] = cur;
                    q.push(child);
                }
            }
        }   

        for(auto t : parent) cerr << t << " ";
        cerr << endl;

        if(parent[n] == -1) break;

        vector<ll> path;
        ll cur = n;
        while(cur != -1)
        {
            path.push_back(cur);
            used[{parent[cur], cur}] = true;
            cur = parent[cur];
        }

        reverse(path.begin(), path.end());

        for(auto t : path) cout << t << " ";
        cout << endl;
    }

    cout << endl;
    return;



    
}

int main()
{
    ll test; cin >> test;
    while(test--)
        EdmondsKarp();
    return 0;
}