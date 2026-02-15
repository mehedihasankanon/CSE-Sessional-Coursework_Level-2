#pragma once // AVLTREE_H

#include <iostream>
#include <algorithm>
#include <queue>

using namespace std;

template <typename Key>
class AVLNode
{
public:
    Key key;
    AVLNode *left;
    AVLNode *right;
    int height;

    AVLNode(Key k) : key(k), left(nullptr), right(nullptr), height(1) {}
};

template <typename Key>
class AVLTree
{
private:
    AVLNode<Key> *root;

    int getHeight(AVLNode<Key> *node)
    {
        return node ? node->height : 0;
    }

    int getBalance(AVLNode<Key> *node)
    {
        return node ? getHeight(node->left) - getHeight(node->right) : 0;
    }

    void updateHeight(AVLNode<Key> *node)
    {
        if (node)
        {
            node->height = 1 + max(getHeight(node->left), getHeight(node->right));
        }
    }

    AVLNode<Key> *rotateRight(AVLNode<Key> *y)
    {
        AVLNode<Key> *x = y->left;
        AVLNode<Key> *T2 = x->right;

        x->right = y;
        y->left = T2;

        updateHeight(y);
        updateHeight(x);

        return x;
    }

    AVLNode<Key> *rotateLeft(AVLNode<Key> *x)
    {
        AVLNode<Key> *y = x->right;
        AVLNode<Key> *T2 = y->left;

        y->left = x;
        x->right = T2;

        updateHeight(x);
        updateHeight(y);

        return y;
    }

    AVLNode<Key> *balance(AVLNode<Key> *node)
    {
        updateHeight(node);
        int balanceFactor = getBalance(node);

        if (balanceFactor > 1) // --> left-heavy imbalance
        {
            if (getBalance(node->left) < 0)
            {
                node->left = rotateLeft(node->left);
            }
            return rotateRight(node);
        }

        if (balanceFactor < -1) // --> right-heavy imbalance
        {
            if (getBalance(node->right) > 0)
            {
                node->right = rotateRight(node->right);
            }
            return rotateLeft(node);
        }

        return node;
    }

    AVLNode<Key> *insertNode(AVLNode<Key> *node, Key key, bool &success)
    {
        if (!node)
        {
            success = true;
            return new AVLNode<Key>(key);
        }

        if (key < node->key)
        {
            node->left = insertNode(node->left, key, success);
        }
        else if (key > node->key)
        {
            node->right = insertNode(node->right, key, success);
        }
        else
        {
            success = false;
            return node;
        }

        return balance(node);
    }

    AVLNode<Key> *findMin(AVLNode<Key> *node)
    {
        while (node->left)
        {
            node = node->left;
        }
        return node;
    }

    AVLNode<Key> *findMax(AVLNode<Key> *node)
    {
        while (node->right)
        {
            node = node->right;
        }
        return node;
    }

    AVLNode<Key> *deleteNode(AVLNode<Key> *node, Key key, bool &success)
    {
        if (!node)
        {
            success = false;
            return nullptr;
        }

        if (key < node->key)
        {
            node->left = deleteNode(node->left, key, success);
        }
        else if (key > node->key)
        {
            node->right = deleteNode(node->right, key, success);
        }
        else
        {
            success = true;

            if (!node->left || !node->right) // --> at least one subtree is empty
            {
                AVLNode<Key> *temp = node->left ? node->left : node->right;
                delete node;
                return temp;
            }

            AVLNode<Key> *temp = findMin(node->right);
            node->key = temp->key;
            node->right = deleteNode(node->right, temp->key, success);
        }

        return balance(node);
    }

    void preOrder(AVLNode<Key> *node, ostream &out = cout)
    {
        if (node)
        {
            out << node->key << " ";
            preOrder(node->left, out);
            preOrder(node->right, out);
        }
    }

    void inOrder(AVLNode<Key> *node, ostream &out = cout)
    {
        if (node)
        {
            inOrder(node->left, out);
            out << node->key << " ";
            inOrder(node->right, out);
        }
    }

    void postOrder(AVLNode<Key> *node, ostream &out = cout)
    {
        if (node)
        {
            postOrder(node->left, out);
            postOrder(node->right, out);
            out << node->key << " ";
        }
    }

    void levelOrder(AVLNode<Key> *node, ostream &out = cout)
    {
        if (!node)
            return;

        queue<AVLNode<Key> *> q;
        q.push(node);

        while (!q.empty())
        {
            AVLNode<Key> *current = q.front();
            q.pop();
            out << current->key << " ";

            if (current->left)
                q.push(current->left);
            if (current->right)
                q.push(current->right);
        }
    }

    void destroyTree(AVLNode<Key> *node)
    {
        if (node)
        {
            destroyTree(node->left);
            destroyTree(node->right);
            delete node;
        }
    }

public:
    AVLTree() : root(nullptr) {}

    ~AVLTree()
    {
        destroyTree(root);
    }

    bool insert(Key key)
    {
        bool success = false;
        root = insertNode(root, key, success);
        return success;
    }

    bool remove(Key key)
    {
        bool success = false;
        root = deleteNode(root, key, success);
        return success;
    }

    void traverse(int order, ostream &out = cout)
    {
        switch (order)
        {
        case 1:
            preOrder(root, out);
            break;
        case 2:
            inOrder(root, out);
            break;
        case 3:
            postOrder(root, out);
            break;
        case 4:
            levelOrder(root, out);
            break;
        }
    }
};
