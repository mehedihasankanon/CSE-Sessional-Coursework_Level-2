#include <bits/stdc++.h>

using namespace std;

using ll = long long;
// using ll = int;
using pll = pair<ll,ll>;

ll INF = 1e18;


int main()
{
    ll n, m; cin >> n >> m;

    typedef struct Edge {
        ll from, to, distance;

        Edge (ll a, ll b, ll c) : from(a), to(b), distance(c) {}
    } Edge;

    vector<Edge> edges(m);

    for(ll i = 0; i < m; i++)
    {
        ll a, b, c; cin >> a >> b >> c;
        edges[i] = Edge{a,b,c};
    }

    vector<ll> d(n + 1, INF);

    
}