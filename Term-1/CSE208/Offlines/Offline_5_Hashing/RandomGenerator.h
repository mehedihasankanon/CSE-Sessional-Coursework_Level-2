#pragma once

#include <string>
#include <vector>
#include <set>
#include <random>

std::vector<std::string> generate_random_words(int N, int word_len, unsigned seed = 42)
{
    std::mt19937 rng(seed);
    std::uniform_int_distribution<int> dist(0, 25);

    std::set<std::string> seen;
    std::vector<std::string> words;

    while ((int)words.size() < N)
    {
        std::string word(word_len, 'a');
        for (int i = 0; i < word_len; i++)
            word[i] = 'a' + dist(rng);

        if (seen.find(word) == seen.end())
        {
            seen.insert(word);
            words.push_back(word);
        }
    }

    return words;
}
