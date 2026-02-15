#pragma once // RBTREE_H

#include <iostream>
#include <algorithm>
#include <queue>

using namespace std;

enum Color
{
    RED,
    BLACK
};

template <typename Key>
class RBNode
{
public:
    Key key;
    RBNode *left;
    RBNode *right;
    RBNode *parent;
    Color color;
    int size;

    RBNode(Key k) : key(k), left(nullptr), right(nullptr), parent(nullptr), color(RED), size(1) {}
};

template <typename Key>
class RBTree
{
private:
    RBNode<Key> *root;
    RBNode<Key> *NIL;

    void initNIL()
    {
        NIL = new RBNode<Key>(Key());
        NIL->color = BLACK;
        NIL->left = nullptr;
        NIL->right = nullptr;
        NIL->parent = nullptr;
        NIL->size = 0;
    }

    void updateSize(RBNode<Key> *node)
    {
        if (node != NIL)
        {
            node->size = node->left->size + node->right->size + 1;
        }
    }

    void rotateLeft(RBNode<Key> *x)
    {
        RBNode<Key> *y = x->right;
        x->right = y->left;

        if (y->left != NIL)
        {
            y->left->parent = x;
        }

        y->parent = x->parent;

        if (x->parent == nullptr)
        {
            root = y;
        }
        else if (x == x->parent->left)
        {
            x->parent->left = y;
        }
        else
        {
            x->parent->right = y;
        }

        y->left = x;
        x->parent = y;

        updateSize(x);
        updateSize(y);
    }

    void rotateRight(RBNode<Key> *y)
    {
        RBNode<Key> *x = y->left;
        y->left = x->right;

        if (x->right != NIL)
        {
            x->right->parent = y;
        }

        x->parent = y->parent;

        if (y->parent == nullptr)
        {
            root = x;
        }
        else if (y == y->parent->right)
        {
            y->parent->right = x;
        }
        else
        {
            y->parent->left = x;
        }

        x->right = y;
        y->parent = x;

        updateSize(y);
        updateSize(x);
    }

    void fixInsertion(RBNode<Key> *z)
    {
        while (z->parent && z->parent->color == RED)
        {
            if (z->parent == z->parent->parent->left)
            {
                RBNode<Key> *y = z->parent->parent->right;
                if (y->color == RED)
                {
                    z->parent->color = BLACK;
                    y->color = BLACK;
                    z->parent->parent->color = RED;
                    z = z->parent->parent;
                }
                else
                {
                    if (z == z->parent->right)
                    {
                        z = z->parent;
                        rotateLeft(z);
                    }
                    z->parent->color = BLACK;
                    z->parent->parent->color = RED;
                    rotateRight(z->parent->parent);
                }
            }
            else
            {
                RBNode<Key> *y = z->parent->parent->left;
                if (y->color == RED)
                {
                    z->parent->color = BLACK;
                    y->color = BLACK;
                    z->parent->parent->color = RED;
                    z = z->parent->parent;
                }
                else
                {
                    if (z == z->parent->left)
                    {
                        z = z->parent;
                        rotateRight(z);
                    }
                    z->parent->color = BLACK;
                    z->parent->parent->color = RED;
                    rotateLeft(z->parent->parent);
                }
            }
        }
        root->color = BLACK;
    }

    bool insertNode(Key key)
    {
        RBNode<Key> *node = new RBNode<Key>(key);
        node->left = NIL;
        node->right = NIL;

        RBNode<Key> *y = nullptr;
        RBNode<Key> *x = root;

        while (x != NIL)
        {
            y = x;
            if (node->key < x->key)
            {
                x = x->left;
            }
            else if (node->key > x->key)
            {
                x = x->right;
            }
            else
            {
                delete node;
                return false;
            }
        }

        RBNode<Key> *p = y;
        while (p != nullptr)
        {
            p->size++;
            p = p->parent;
        }

        node->parent = y;

        if (y == nullptr)
        {
            root = node;
        }
        else if (node->key < y->key)
        {
            y->left = node;
        }
        else
        {
            y->right = node;
        }

        node->color = RED;
        fixInsertion(node);

        return true;
    }

    void replaceNode(RBNode<Key> *u, RBNode<Key> *v)
    {
        if (u->parent == nullptr)
        {
            root = v;
        }
        else if (u == u->parent->left)
        {
            u->parent->left = v;
        }
        else
        {
            u->parent->right = v;
        }
        v->parent = u->parent;
    }

    RBNode<Key> *minimum(RBNode<Key> *node)
    {
        while (node->left != NIL)
        {
            node = node->left;
        }
        return node;
    }

    void fixDeletion(RBNode<Key> *x)
    {
        while (x != root && x->color == BLACK)
        {
            if (x == x->parent->left)
            {
                RBNode<Key> *w = x->parent->right;
                if (w->color == RED)
                {
                    w->color = BLACK;
                    x->parent->color = RED;
                    rotateLeft(x->parent);
                    w = x->parent->right;
                }
                if (w->left->color == BLACK && w->right->color == BLACK)
                {
                    w->color = RED;
                    x = x->parent;
                }
                else
                {
                    if (w->right->color == BLACK)
                    {
                        w->left->color = BLACK;
                        w->color = RED;
                        rotateRight(w);
                        w = x->parent->right;
                    }
                    w->color = x->parent->color;
                    x->parent->color = BLACK;
                    w->right->color = BLACK;
                    rotateLeft(x->parent);
                    x = root;
                }
            }
            else
            {
                RBNode<Key> *w = x->parent->left;
                if (w->color == RED)
                {
                    w->color = BLACK;
                    x->parent->color = RED;
                    rotateRight(x->parent);
                    w = x->parent->left;
                }
                if (w->right->color == BLACK && w->left->color == BLACK)
                {
                    w->color = RED;
                    x = x->parent;
                }
                else
                {
                    if (w->left->color == BLACK)
                    {
                        w->right->color = BLACK;
                        w->color = RED;
                        rotateLeft(w);
                        w = x->parent->left;
                    }
                    w->color = x->parent->color;
                    x->parent->color = BLACK;
                    w->left->color = BLACK;
                    rotateRight(x->parent);
                    x = root;
                }
            }
        }
        x->color = BLACK;
    }

    bool deleteNode(Key key)
    {
        RBNode<Key> *z = root;
        while (z != NIL)
        {
            if (key < z->key)
            {
                z = z->left;
            }
            else if (key > z->key)
            {
                z = z->right;
            }
            else
            {
                break;
            }
        }

        if (z == NIL)
        {
            return false;
        }

        RBNode<Key> *p = z->parent;
        while (p != nullptr)
        {
            p->size--;
            p = p->parent;
        }

        RBNode<Key> *y = z;
        RBNode<Key> *x;
        Color yOriginal = y->color;

        if (z->left == NIL)
        {
            x = z->right;
            replaceNode(z, z->right);
        }
        else if (z->right == NIL)
        {
            x = z->left;
            replaceNode(z, z->left);
        }
        else
        {
            y = minimum(z->right);
            yOriginal = y->color;
            x = y->right;

            if (y->parent == z)
            {
                x->parent = y;
            }
            else
            {
                RBNode<Key> *q = y->parent;
                while (q != z)
                {
                    q->size--;
                    q = q->parent;
                }
                replaceNode(y, y->right);
                y->right = z->right;
                y->right->parent = y;
            }

            replaceNode(z, y);
            y->left = z->left;
            y->left->parent = y;
            y->color = z->color;
            y->size = y->left->size + y->right->size + 1;
        }

        delete z;

        if (yOriginal == BLACK)
        {
            fixDeletion(x);
        }

        return true;
    }

    RBNode<Key> *searchNode(Key key)
    {
        RBNode<Key> *current = root;
        while (current != NIL)
        {
            if (key < current->key)
            {
                current = current->left;
            }
            else if (key > current->key)
            {
                current = current->right;
            }
            else
            {
                return current;
            }
        }
        return NIL;
    }

    int countLessThanHelper(RBNode<Key> *node, Key key)
    {
        int count = 0;
        while (node != NIL)
        {
            if (key <= node->key)
            {
                node = node->left;
            }
            else
            {
                count += node->left->size + 1;
                node = node->right;
            }
        }
        return count;
    }

    void destroyTree(RBNode<Key> *node)
    {
        if (node != NIL)
        {
            destroyTree(node->left);
            destroyTree(node->right);
            delete node;
        }
    }

public:
    RBTree()
    {
        initNIL();
        root = NIL;
    }

    ~RBTree()
    {
        destroyTree(root);
        delete NIL;
    }

    bool insert(Key key)
    {
        return insertNode(key);
    }

    bool remove(Key key)
    {
        return deleteNode(key);
    }

    bool search(Key key)
    {
        return searchNode(key) != NIL;
    }

    int countLessThan(Key key)
    {
        return countLessThanHelper(root, key);
    }
};
