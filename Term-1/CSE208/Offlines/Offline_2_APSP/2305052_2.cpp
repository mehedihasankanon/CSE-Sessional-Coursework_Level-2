#include <bits/stdc++.h>

using namespace std;
using ld = long double;
using ll = int;

int main()
{
    ll n;
    cin >> n;

    map<string, ll> curr_ind;
    for (ll i = 1; i <= n; i++)
    {
        string currency;
        cin >> currency;
        curr_ind[currency] = i;
    }

    ll m;
    cin >> m;

    using Edge = struct edge
    {
        ll from, to;
        ld cost;

        edge(ll a, ll b, ld cost) : from{a}, to{b}, cost{cost} {};
    };

    vector<Edge> edges;

    for (ll i = 0; i < m; i++)
    {
        string from, to;
        ld cost;
        cin >> from >> cost >> to;

        edges.push_back(Edge{curr_ind[from], curr_ind[to], -logl(cost)});
    }

    vector<ld> dist(n + 1, (ld)0.0);

    for (ll i = 0; i < n - 1; i++)
    {
        for (Edge e : edges)
        {
            dist[e.to] = min(dist[e.to], dist[e.from] + e.cost);
        }
    }

    bool negCyc = false;
    for (Edge e : edges)
    {
        if (dist[e.from] + e.cost < dist[e.to] - 1e-9)
        {
            negCyc = true;
            break;
        }
    }

    cout << (negCyc ? "Yes" : "No") << endl;

    return 0;
}