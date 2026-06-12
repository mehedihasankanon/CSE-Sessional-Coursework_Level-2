#include <bits/stdc++.h>

using namespace std;

using ll = long long;

ll INF = 1e18;

int main()
{
    ll n, m;
    cin >> n >> m;

    typedef struct Edge
    {
        ll from, to, distance;

        Edge() = default;
        Edge(ll a, ll b, ll c) : from(a), to(b), distance(c) {}
    } Edge;

    vector<Edge> edges(m);

    for (ll i = 0; i < m; i++)
    {
        ll a, b, c;
        cin >> a >> b >> c;
        edges[i] = Edge{a, b, c};
    }

    vector<ll> d(n + 1, 0);
    vector<ll> p(n + 1, -1);

    ll x = -1;

    for (int i = 0; i < n; i++)
    {
        x = -1;
        for (auto edge : edges)
        {
            if (d[edge.from] + edge.distance < d[edge.to])
            {
                d[edge.to] = d[edge.from] + edge.distance;
                p[edge.to] = edge.from;
                x = edge.to;
            }
        }
    }

    if (x == -1)
    {
        cout << -1 << endl;
        return 0;
    }

    ll it = x;
    vector<ll> path;
    while (true)
    {
        path.push_back(it);
        it = p[it];

        if (it == x)
            break;
    }

    reverse(path.begin(), path.end());

    for (auto node : path)
    {
        cout << node << " ";
        ;
    }

    cout << endl;
    return 0;
}