#include <bits/stdc++.h>

using ll = int;

using namespace std;

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

void BipartiteMatching()
{
    ll n, k, m;
    cin >> n >> k >> m;

    vector<Edge> edges;
    vector<pair<ll, ll>> forwardEdges;
    vector<vector<ll>> adj(n + 2);

    ll src = n;
    ll sink = n + 1;

    for (ll i = 0; i < k; i++)
    {
        adj[src].push_back(edges.size());
        edges.push_back({i, 1, 0});

        adj[i].push_back(edges.size());
        edges.push_back({src, 0, 0});
    }

    for (ll i = k; i < n; i++)
    {
        adj[i].push_back(edges.size());
        edges.push_back({sink, 1, 0});

        adj[sink].push_back(edges.size());
        edges.push_back({i, 0, 0});
    }

    for (ll i = 0; i < m; i++)
    {
        ll from, to;
        cin >> from >> to;

        forwardEdges.push_back({from, to});

        adj[from].push_back(edges.size());
        edges.push_back({to, 1, 0});

        adj[to].push_back(edges.size());
        edges.push_back({from, 0, 0});
    }

    ll maxFlow = 0;

    while (true)
    {
        vector<ll> augPath = bfs(n + 2, src, sink, edges, adj);

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

    vector<pair<ll, ll>> matching_pairs;

    for (ll i = 0; i < forwardEdges.size(); i++)
    {
        ll idx = 2 * (n + i);
        if (edges[idx].curr_flow == 1)
        {
            matching_pairs.push_back(forwardEdges[i]);
        }
    }

    cout << matching_pairs.size() << endl;
    for (auto &p : matching_pairs)
    {
        cout << p.first << " " << p.second << endl;
    }

    return;
}

int main()
{
    BipartiteMatching();
    return 0;
}
