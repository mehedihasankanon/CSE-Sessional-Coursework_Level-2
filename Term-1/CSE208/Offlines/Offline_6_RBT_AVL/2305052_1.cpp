#include <iostream>
#include <fstream>
#include "RBTree.h"

using namespace std;

int main()
{
    ifstream inFile("input.txt");
    ofstream outFile("output.txt");

    if (!inFile.is_open() || !outFile.is_open())
    {
        cerr << "Error opening files!" << endl;
        return 1;
    }

    RBTree<int> tree;
    int n;
    inFile >> n;

    outFile << n << endl;

    for (int i = 0; i < n; i++)
    {
        int e, x;
        inFile >> e >> x;

        if (e == 1)
        {
            bool success = tree.insert(x);
            outFile << e << " " << x << " " << (success ? 1 : 0) << endl;
        }
        else if (e == 0)
        {
            bool success = tree.remove(x);
            outFile << e << " " << x << " " << (success ? 1 : 0) << endl;
        }
        else if (e == 2)
        {
            bool found = tree.search(x);
            outFile << e << " " << x << " " << (found ? 1 : 0) << endl;
        }
        else if (e == 3)
        {
            int count = tree.countLessThan(x);
            outFile << e << " " << x << " " << count << endl;
        }
    }

    inFile.close();
    outFile.close();

    return 0;
}
