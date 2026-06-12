#pragma once

#include <vector>

using std::vector;

const long long Pmax = 1000000; vector<bool> prime(Pmax+10,true); vector<long long> primes; 

void sieve (void)
{
    prime[1] = false;
    
    primes.push_back(2);
    for(long long i = 4; i <= Pmax; i+=2) prime[i]=false;
    
    for(long long i = 3; i <= Pmax; i += 2)
    {
        if(prime[i]) 
        {
            for(long long j = i*i; j <= Pmax; j += i)
            {
                prime[j] = false;
            }
            primes.push_back(i);
        }
    }

}
