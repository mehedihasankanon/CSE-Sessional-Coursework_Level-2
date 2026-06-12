#include <bits/stdc++.h>
#include "HashTable.h"
#include "RandomGenerator.h"

using namespace std;
using ll = long long;

const ll INITIAL_TABLE_SIZE = 13;
const double CUTOFF_LOAD_FACTOR = 0.5;
const double MIN_LOAD_FACTOR = 0.25;
const int N = 10000;
const int WORD_LEN = 10;
const int SEARCH_N = 1000;
const ll C1 = 1;
const ll C2 = 3;

const char *method_name(COLLISION_METHOD cm)
{
    switch (cm)
    {
    case CHAINING:
        return "Chaining Method";
    case DOUBLE_HASHING:
        return "Double Hashing";
    case CUSTOM_PROBING:
        return "Custom Probing";
    }
    return "Unknown";
}

pair<ll, double> run_experiment(
    const vector<string> &words,
    const vector<string> &search_words,
    COLLISION_METHOD cm,
    HASH_FUNCTION hf)
{
    HashTable<string, int> ht(INITIAL_TABLE_SIZE, cm, hf,
                              CUTOFF_LOAD_FACTOR, MIN_LOAD_FACTOR, C1, C2);

    for (int i = 0; i < (int)words.size(); i++)
        ht.insert(words[i], i + 1);

    ll col = ht.get_collisions();

    ht.reset_stats();
    for (const string &w : search_words)
        ht.search(w);

    double avg = ht.get_avg_probes((ll)search_words.size());

    return {col, avg};
}

void run_test(
    const vector<string> &words,
    const vector<string> &search_words,
    COLLISION_METHOD cm,
    HASH_FUNCTION hf)
{
    HashTable<string, int> ht(INITIAL_TABLE_SIZE, cm, hf,
                              CUTOFF_LOAD_FACTOR, MIN_LOAD_FACTOR, C1, C2);

    for (int i = 0; i < (int)words.size(); i++)
        ht.insert(words[i], i + 1);

    

    ll x; cin >> x;

    for(ll i = 0; i < x; i++)
    {
        string s; cin >> s;
        ht.printProbeSequence(s);
    }

    return;
}

void verify_hash_uniqueness(const vector<string> &words)
{
    HashTable<string, int> ht(INITIAL_TABLE_SIZE, CHAINING, HASH_1,
                              CUTOFF_LOAD_FACTOR, MIN_LOAD_FACTOR);
    for (int i = 0; i < (int)words.size(); i++)
        ht.insert(words[i], i + 1);

    ll tbl_size = ht.get_table_size();

    set<ll> h1_vals, h2_vals;
    for (const string &w : words)
    {
        h1_vals.insert(ht.report_hash1(w));
        h2_vals.insert(ht.report_hash2(w));
    }

    double pct1 = 100.0 * h1_vals.size() / N;
    double pct2 = 100.0 * h2_vals.size() / N;

    cout << fixed << setprecision(2);
    cout << "  N = " << N
         << ", table_size = " << tbl_size << "\n";
    cout << "  Hash1 (djb2):   " << h1_vals.size() << "/" << N
         << " unique  (" << pct1 << "%)\n";
    cout << "  Hash2 (FNV-1a): " << h2_vals.size() << "/" << N
         << " unique  (" << pct2 << "%)\n";
}

int main()
{
    vector<string> words = generate_random_words(N, WORD_LEN);

    vector<string> search_words;
    {
        vector<int> indices(N);
        iota(indices.begin(), indices.end(), 0);
        mt19937 rng(123);
        shuffle(indices.begin(), indices.end(), rng);
        for (int i = 0; i < SEARCH_N; i++)
            search_words.push_back(words[indices[i]]);
    }

    verify_hash_uniqueness(words);

    COLLISION_METHOD methods[] = {CHAINING, DOUBLE_HASHING, CUSTOM_PROBING};
    HASH_FUNCTION hashes[] = {HASH_1, HASH_2};

    pair<ll, double> results[3][2];

    for (int m = 0; m < 3; m++)
        for (int h = 0; h < 2; h++)
            results[m][h] = run_experiment(words, search_words, methods[m], hashes[h]);

    run_test(words, search_words, CUSTOM_PROBING, HASH_1);

    cout << endl
         << endl
         << left
         << setw(20) << ""
         << "| " << setw(28) << "         Hash1 (djb2)"
         << "| " << setw(28) << "        Hash2 (FNV-1a)" << "\n";

    cout << left
         << setw(20) << ""
         << "| " << setw(14) << " Collisions"
         << setw(14) << "  Avg Hits"
         << "| " << setw(14) << " Collisions"
         << setw(14) << "  Avg Hits" << "\n";

    cout << string(78, '-') << "\n";

    for (int m = 0; m < 3; m++)
    {
        cout << left << setw(20) << method_name(methods[m])
             << "| " << setw(14) << results[m][0].first
             << setw(14) << results[m][0].second
             << "| " << setw(14) << results[m][1].first
             << setw(14) << results[m][1].second << "\n";
    }

    cout << string(78, '-') << "\n";

    
}
