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
    ll p, q, r;
    cin >> p >> q >> r;

    vector<Edge> edges;
    vector<pair<ll, ll>> forwardEdges;
    vector<vector<ll>> adj(p + q + 2*r + 5); // adj[node] --> stores the indices of the outgoing edges

    /*
        idea:
        src(0) --> students with capacity credit_hour
        students --> eligible course_numebr with capacity 1
        courses --> eligible teacher with capacity course_capacity
        teachers --> sink(p + q + r + 1) with capacity teacher_capacity

        run max flow

        check if all students have their max credit hours filled

        then iterate through student ids for the number of course assignments made
    */

    for (ll i = 1; i <= p; i++)
    {
        ll cr_hr;
        cin >> cr_hr;

        forwardEdges.push_back({0, i});

        adj[0].push_back(edges.size());
        edges.push_back({i, cr_hr, 0}); // 0 -> i , cr_hr

        adj[i].push_back(edges.size());
        edges.push_back({0, 0, 0});
    }

    for (ll i = p + 2 * r + 1; i <= p + 2 * r + q; i++)
    {
        ll teach_cap;
        cin >> teach_cap;

        forwardEdges.push_back({i, p + q + 2 * r + 1});

        adj[i].push_back(edges.size());
        edges.push_back({p + q + 2 * r + 1, teach_cap, 0});

        adj[p + q + 2 * r + 1].push_back(edges.size());
        edges.push_back({i, 0, 0});
    }

    vector<ll> course_cap(r + 1, 0);

    for (ll i = 1; i <= r; i++)
    {
        cin >> course_cap[i];

        
        forwardEdges.push_back({p + i,p + r + i});

        

        adj[p + i].push_back(edges.size());
        edges.push_back({p + r + i, course_cap[i], 0});

        adj[p + r + i].push_back(edges.size());
        edges.push_back({p + i, 0, 0});

    }

    ll k;
    cin >> k;
    for (ll i = 0; i < k; i++)
    {
        ll s, c;
        cin >> s >> c;

        forwardEdges.push_back({s, p + c});

        adj[s].push_back(edges.size());
        edges.push_back({p + c, 1, 0});

        adj[p + c].push_back(edges.size());
        edges.push_back({s, 0, 0});
    }

    ll m;
    cin >> m;

    for (ll i = 0; i < k; i++)
    {
        ll t, c;
        cin >> t >> c;

        forwardEdges.push_back({p + r + c, p + r * 2 + t});

        adj[p + r + c].push_back(edges.size());
        edges.push_back({p + r * 2 + t, course_cap[c], 0});

        adj[p + r*2 + t].push_back(edges.size());
        edges.push_back({p + c + r, 0, 0});
    }

    ll src = 0, sink = p + q + 2*r + 1;

    ll maxFlow = 0;

    while (true)
    {
        vector<ll> augPath = bfs(p + q + 2*r + 2, src, sink, edges, adj);

        if (augPath.empty())
            break;

        // for(ll t : augPath)
        // {
        //     cout << t << " ";
        // }
        // cout << endl;

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

    for (auto edge_idx : adj[0])
    {
        if (edges[edge_idx].cap != edges[edge_idx].curr_flow)
        {
            cout << "NO" << endl;
            return;
        }
    }

    ll ans = 0;

    for (ll i = 1; i <= p; i++)
    {
        for (auto idx : adj[i])
        {
            if (edges[idx].curr_flow > 0)
            {
                ans++;
            }
        }
    }

    cout << "YES" << " " << ans << endl;

    return;
}

int main()
{
    EdmondsKarp();
    return 0;
}