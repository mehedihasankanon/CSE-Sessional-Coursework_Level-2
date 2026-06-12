#include <bits/stdc++.h>
#include "HashTable.h"

using namespace std;

int main () {


    int n1; cin >> n1;
    HashTable<int, bool> ht1(n1, CHAINING);
    vector<int> v1(n1);
    for(int i = 0; i < n1; i++)
    {
        // int x; cin >> x;
        cin >> v1[i];
        ht1.insert(v1[i], true);
    }

    int n2; cin >> n2;
    HashTable<int, bool> ht2(n2, CHAINING);
    vector<int> v2(n2);
    for(int i = 0; i < n2; i++)
    {
        // int x; cin >> x;
        cin >> v2[i];
        ht2.insert(v2[i], true);
    }

    // intersection
    cout << "Intersection: ";
    for(auto t : ht1.getElements())
    {
        if(ht2.search(t))
        {
            cout << t << " ";
        }
    }
    cout << endl;

    // union
    HashTable<int,bool> ht3(n1 + n2, CHAINING);
    for(auto t : ht1.getElements())
    {
        ht3.insert(t, true);
    }
    for(auto t : ht2.getElements())
    {
        ht3.insert(t, true);
    }

    cout << "Union: ";
    for(auto t : ht3.getElements())
    {
        cout << t << " ";
    }
    cout << endl;

    // difference
    cout << "Difference: ";
    for(auto t : ht1.getElements())
    {
        if(!ht2.search(t))
        {
            cout << t << " ";
        }
    }
    cout << endl;


}