#pragma once

#include <iostream>
#include <vector>
#include <set>
#include <string>
#include <type_traits>

using ll = long long;

using std::vector;

enum COLLISION_METHOD
{
    CHAINING,
    DOUBLE_HASHING,
    CUSTOM_PROBING
};

enum HASH_FUNCTION
{
    HASH_1,
    HASH_2
};

template <typename Key, typename Value>
class HashTable
{
private:
    ll table_size;
    ll initial_size;
    ll count;
    COLLISION_METHOD cm;
    HASH_FUNCTION hf;

    ll total_probes;
    ll collisions;

    double max_lf;
    double min_lf;

    ll C1, C2;

    ll insertions_since_expansion;
    ll deletions_since_compaction;
    ll count_at_last_expansion;
    ll count_at_last_compaction;

    vector<Key> elements;

    struct Node
    {
        Key key;
        Value value;
        Node *next;
        Node(const Key &k, const Value &v) : key(k), value(v), next(nullptr) {}
    };
    Node **chain_table;

    struct Entry
    {
        Key key;
        Value value;
        bool active;
        bool deleted;
        Entry() : key(Key()), value(Value()), active(false), deleted(false) {}
    };
    Entry *probe_table;

    static bool is_prime(ll n)
    {
        if (n < 2)
            return false;
        if (n == 2 || n == 3)
            return true;
        if (n % 2 == 0 || n % 3 == 0)
            return false;
        for (ll i = 5; i * i <= n; i += 6)
            if (n % i == 0 || n % (i + 2) == 0)
                return false;
        return true;
    }

    static ll next_prime_gt(ll n)
    {
        n++;
        if (n <= 2)
            return 2;
        if (n % 2 == 0)
            n++;
        while (!is_prime(n))
            n += 2;
        return n;
    }

    static ll next_prime_ge(ll n)
    {
        if (n <= 2)
            return 2;
        if (is_prime(n))
            return n;
        return next_prime_gt(n);
    }

    static ll prev_prime_lt(ll n)
    {
        n--;
        if (n < 2)
            return 2;
        if (n == 2)
            return 2;
        if (n % 2 == 0)
            n--;
        while (n > 2 && !is_prime(n))
            n -= 2;
        return (n >= 2) ? n : 2;
    }

    /**
     * Hash1: djb2 hash
     * Reference: http://www.cse.yorku.ca/~oz/hash.html
     */
    ll hash1_raw(const Key &key) const
    {
        unsigned long long h = 5381;
        if constexpr (std::is_same_v<Key, std::string>)
        {
            for (unsigned char c : key)
                h = h * 33 + c;
        }
        else
        {
            for (unsigned char c : std::to_string(key))
                h = h * 33 + c;
        }
        return static_cast<ll>(h & 0x7FFFFFFFFFFFFFFFLL);
    }

    /**
     * Hash2: FNV-1a hash
     * Reference: http://www.isthe.com/chongo/tech/comp/fnv/
     */
    ll hash2_raw(const Key &key) const
    {
        unsigned long long h = 14695981039346656037ULL;
        if constexpr (std::is_same_v<Key, std::string>)
        {
            for (unsigned char c : key)
            {
                h ^= c;
                h *= 1099511628211ULL;
            }
        }
        else
        {
            for (unsigned char c : std::to_string(key))
            {
                h ^= c;
                h *= 1099511628211ULL;
            }
        }
        return static_cast<ll>(h & 0x7FFFFFFFFFFFFFFFLL);
    }

    ll hash1(const Key &key) const { return hash1_raw(key) % table_size; }
    ll hash2(const Key &key) const { return hash2_raw(key) % table_size; }

    ll primary_hash(const Key &key) const
    {
        return (hf == HASH_1) ? hash1(key) : hash2(key);
    }

    ll aux_hash(const Key &key) const
    {
        unsigned long long h = 0;
        if constexpr (std::is_same_v<Key, std::string>)
        {
            for (unsigned char c : key)
                h = h * 31 + c;
        }
        else
        {
            for (unsigned char c : std::to_string(key))
                h = h * 31 + c;
        }
        h &= 0x7FFFFFFFFFFFFFFFLL;
        ll mod = table_size - 1;
        if (mod <= 0)
            mod = 1;
        return 1 + static_cast<ll>(h % mod);
    }

    ll probe_index(const Key &key, ll i) const
    {
        ll h = primary_hash(key);
        ll a = aux_hash(key);
        ll val;
        if (cm == DOUBLE_HASHING)
            val = h + i * a;
        else
            val = h + C1 * i * a + C2 * i * i;
        return ((val % table_size) + table_size) % table_size;
    }

    void internal_rehash(ll new_size)
    {
        new_size = next_prime_ge(new_size);
        if (new_size == table_size)
            return;

        std::vector<std::pair<Key, Value>> entries;

        if (cm == CHAINING)
        {
            for (ll i = 0; i < table_size; i++)
            {
                Node *cur = chain_table[i];
                while (cur)
                {
                    entries.push_back({cur->key, cur->value});
                    Node *tmp = cur;
                    cur = cur->next;
                    delete tmp;
                }
            }
            delete[] chain_table;

            table_size = new_size;
            count = 0;
            chain_table = new Node *[table_size];
            for (ll i = 0; i < table_size; i++)
                chain_table[i] = nullptr;
        }
        else
        {
            for (ll i = 0; i < table_size; i++)
            {
                if (probe_table[i].active && !probe_table[i].deleted)
                    entries.push_back({probe_table[i].key, probe_table[i].value});
            }
            delete[] probe_table;

            table_size = new_size;
            count = 0;
            probe_table = new Entry[table_size];
        }

        for (auto &p : entries)
            raw_insert(p.first, p.second);
    }

    bool raw_insert(const Key &key, const Value &value)
    {
        if (cm == CHAINING)
        {
            ll idx = primary_hash(key);

            if (chain_table[idx] != nullptr)
                collisions++;

            Node *nd = new Node(key, value);
            nd->next = chain_table[idx];
            chain_table[idx] = nd;
            count++;
            return true;
        }
        else
        {
            for (ll i = 0; i < table_size; i++)
            {
                ll idx = probe_index(key, i);
                if (!probe_table[idx].active || probe_table[idx].deleted)
                {
                    if (i > 0)
                        collisions++;

                    probe_table[idx].key = key;
                    probe_table[idx].value = value;
                    probe_table[idx].active = true;
                    probe_table[idx].deleted = false;
                    count++;
                    return true;
                }
            }
            return false;
        }
    }

    void check_expand()
    {
        double lf = (double)count / table_size;
        if (lf <= max_lf)
            return;

        if (count_at_last_expansion > 0 &&
            insertions_since_expansion < count_at_last_expansion / 2)
            return;

        count_at_last_expansion = count;
        insertions_since_expansion = 0;

        ll new_size = next_prime_gt(2 * table_size);
        internal_rehash(new_size);
    }

    void check_compact()
    {
        if (table_size <= initial_size)
            return;

        double lf = (double)count / table_size;
        if (lf >= min_lf)
            return;

        if (count_at_last_compaction > 0 &&
            deletions_since_compaction < count_at_last_compaction / 2)
            return;

        count_at_last_compaction = count;
        deletions_since_compaction = 0;

        ll new_size = prev_prime_lt(table_size / 2);
        if (new_size < initial_size)
            new_size = initial_size;
        internal_rehash(new_size);
    }

public:
    HashTable(ll size, COLLISION_METHOD cm, HASH_FUNCTION hf = HASH_1,
              double max_load = 0.5, double min_load = 0.25,
              ll c1 = 1, ll c2 = 3)
        : cm(cm), hf(hf), count(0), total_probes(0), collisions(0),
          max_lf(max_load), min_lf(min_load), C1(c1), C2(c2),
          insertions_since_expansion(0), deletions_since_compaction(0),
          count_at_last_expansion(0), count_at_last_compaction(0),
          chain_table(nullptr), probe_table(nullptr), elements(0)
    {
        table_size = next_prime_ge(size);
        initial_size = table_size;

        if (cm == CHAINING)
        {
            chain_table = new Node *[table_size];
            for (ll i = 0; i < table_size; i++)
                chain_table[i] = nullptr;
        }
        else
        {
            probe_table = new Entry[table_size];
        }
    }

    ~HashTable()
    {
        if (cm == CHAINING)
        {
            for (ll i = 0; i < table_size; i++)
            {
                Node *cur = chain_table[i];
                while (cur)
                {
                    Node *tmp = cur;
                    cur = cur->next;
                    delete tmp;
                }
            }
            delete[] chain_table;
        }
        else
        {
            delete[] probe_table;
        }
    }

    bool insert(const Key &key, const Value &value)
    {
        if (cm == CHAINING)
        {
            ll idx = primary_hash(key);

            Node *cur = chain_table[idx];
            while (cur)
            {
                if (cur->key == key)
                    return false;
                cur = cur->next;
            }

            if (chain_table[idx] != nullptr)
                collisions++;

            Node *nd = new Node(key, value);
            nd->next = chain_table[idx];
            chain_table[idx] = nd;
            count++;
        }
        else
        {
            bool inserted = false;
            for (ll i = 0; i < table_size; i++)
            {
                ll idx = probe_index(key, i);

                if (!probe_table[idx].active || probe_table[idx].deleted)
                {
                    if (i > 0)
                        collisions++;

                    probe_table[idx].key = key;
                    probe_table[idx].value = value;
                    probe_table[idx].active = true;
                    probe_table[idx].deleted = false;
                    count++;
                    inserted = true;
                    break;
                }
                else if (probe_table[idx].key == key)
                {
                    return false;
                }
            }
            if (!inserted)
                return false;
        }

        insertions_since_expansion++;
        check_expand();
        elements.push_back(key);
        return true;
    }

    vector<Key> &getElements() { sort(elements.begin(), elements.end());  return elements; }

    void printProbeSequence(const Key &key)
    {
        if (cm == CHAINING)
        {
            std::cout << "NOT SUPPORTED" << std::endl;
            return;
        }

        vector<ll> sequence;

        for (ll i = 0; i < table_size; i++)
        {
            ll idx = probe_index(key, i);

            sequence.push_back(idx);

            if (!probe_table[idx].active || probe_table[idx].deleted || probe_table[idx].key == key)
            {
                break;
            }
        }

        for (ll i = 0; i < sequence.size() - 1; i++)
        {
            std::cout << sequence[i] << " -> ";
        }
        std::cout << sequence.back() << std::endl;
    }

    Value *search(const Key &key)
    {
        ll probes = 0;

        if (cm == CHAINING)
        {
            ll idx = primary_hash(key);
            Node *cur = chain_table[idx];
            while (cur)
            {
                probes++;
                if (cur->key == key)
                {
                    total_probes += probes;
                    return &(cur->value);
                }
                cur = cur->next;
            }

            if (probes == 0)
                probes = 1;
            total_probes += probes;
            return nullptr;
        }
        else
        {
            for (ll i = 0; i < table_size; i++)
            {
                ll idx = probe_index(key, i);
                probes++;

                if (!probe_table[idx].active && !probe_table[idx].deleted)
                {
                    total_probes += probes;
                    return nullptr;
                }

                if (probe_table[idx].active &&
                    !probe_table[idx].deleted &&
                    probe_table[idx].key == key)
                {
                    total_probes += probes;
                    return &(probe_table[idx].value);
                }
            }
            total_probes += probes;
            return nullptr;
        }
    }

    bool delete_key(const Key &key)
    {
        if (cm == CHAINING)
        {
            ll idx = primary_hash(key);
            Node *cur = chain_table[idx];
            Node *prev = nullptr;

            while (cur)
            {
                if (cur->key == key)
                {
                    if (prev)
                        prev->next = cur->next;
                    else
                        chain_table[idx] = cur->next;
                    delete cur;
                    count--;
                    deletions_since_compaction++;
                    check_compact();
                    return true;
                }
                prev = cur;
                cur = cur->next;
            }
            return false;
        }
        else
        {
            for (ll i = 0; i < table_size; i++)
            {
                ll idx = probe_index(key, i);

                if (!probe_table[idx].active && !probe_table[idx].deleted)
                    return false;

                if (probe_table[idx].active &&
                    !probe_table[idx].deleted &&
                    probe_table[idx].key == key)
                {
                    probe_table[idx].deleted = true;
                    count--;
                    deletions_since_compaction++;
                    check_compact();
                    return true;
                }
            }
            return false;
        }
    }

    ll compute_collisions() const
    {
        if (cm == CHAINING)
        {
            ll non_empty = 0;
            for (ll i = 0; i < table_size; i++)
                if (chain_table[i] != nullptr)
                    non_empty++;
            return count - non_empty;
        }
        else
        {
            std::set<ll> unique_hashes;
            for (ll i = 0; i < table_size; i++)
                if (probe_table[i].active && !probe_table[i].deleted)
                    unique_hashes.insert(primary_hash(probe_table[i].key));
            return count - (ll)unique_hashes.size();
        }
    }

    ll get_collisions() const { return collisions; }
    double get_load_factor() const { return (double)count / table_size; }
    ll get_count() const { return count; }
    ll get_table_size() const { return table_size; }
    ll get_total_probes() const { return total_probes; }

    double get_avg_probes(ll num_searches) const
    {
        return num_searches == 0 ? 0.0 : (double)total_probes / num_searches;
    }

    void reset_stats()
    {
        collisions = 0;
        total_probes = 0;
    }

    ll report_hash1(const Key &key) const { return hash1(key); }
    ll report_hash2(const Key &key) const { return hash2(key); }
};