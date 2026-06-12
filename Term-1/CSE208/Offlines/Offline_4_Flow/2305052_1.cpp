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
        cin >> from >> to >> cap;

        forwardEdges.push_back({from, to});

        adj[from].push_back(edges.size());
        edges.push_back({to, cap, 0});

        adj[to].push_back(edges.size());
        edges.push_back({from, 0, 0});
    }

    ll src, sink;
    cin >> src >> sink;

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

    cout << maxFlow << endl;

    for (ll i = 0; i < forwardEdges.size(); i++)
    {
        cout << forwardEdges[i].first << " " << forwardEdges[i].second << " "
             << edges[2 * i].curr_flow << "/" << edges[2 * i].cap << endl;
    }
    return;
}

int main()
{
    EdmondsKarp();
    return 0;
}