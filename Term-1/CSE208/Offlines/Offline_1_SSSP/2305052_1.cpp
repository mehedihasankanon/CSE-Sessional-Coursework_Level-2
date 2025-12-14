#include <bits/stdc++.h>

using namespace std;

using ll = long long;
using pll = pair<ll, ll>;

int main()
{
    ll n, m;
    cin >> n >> m;

    vector<vector<pll>> adj(n + 1);

    for (ll i = 0; i < m; i++)
    {
        ll a, b, c;
        cin >> a >> b >> c;

        adj[a].push_back({b, c});
    }

    vector<vector<ll>> d(n + 1, vector<ll>(2, 1e15));

    typedef struct util
    {
        ll node, dist;
        bool ticket_used;

        util(ll node, ll dist, bool used)
        {
            this->node = node;
            this->dist = dist;
            ticket_used = used;
        }
    } util;

    auto cmp = [&](const util &a, const util &b)
    { return a.dist > b.dist; };
    priority_queue<util, vector<util>, decltype(cmp)> pq(cmp);

    pq.push(util(1, 0, false));
    d[1][0] = d[1][1] = 0;

    while (!pq.empty())
    {
        auto current = pq.top();
        pq.pop();

        if (current.dist != d[current.node][current.ticket_used])
            continue;

        for (auto [child, cost] : adj[current.node])
        {
            if (!current.ticket_used)
            {
                if (d[child][1] > current.dist + cost / 2)
                {
                    d[child][1] = current.dist + cost / 2;
                    pq.push(util(child, d[child][1], true));
                }

                if (d[child][0] > current.dist + cost)
                {
                    d[child][0] = current.dist + cost;
                    pq.push(util(child, d[child][0], 0));
                }
            }
            else
            {
                if (d[child][1] > current.dist + cost)
                {
                    d[child][1] = current.dist + cost;
                    pq.push(util(child, d[child][1], true));
                }
            }
        }
    }

    cout << min(d[n][1], d[n][0]) << endl;
    return 0;
}