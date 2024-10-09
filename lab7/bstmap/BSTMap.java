package bstmap;

import javax.swing.*;
import java.util.Iterator;
import java.util.Set;

public class BSTMap <K extends Comparable<K>,V> implements Map61B<K,V>{
    private Node sentinel;
    private int size;
    private class Node {
        K key;
        V value;
        Node left,right;
        Node(K key, V value,Node right,Node left) {
            this.key = key;
            this.value = value;
            this.left=left;
            this.right=right;
        }
    }
    public BSTMap() {
        clear();
    }
    @Override
    public void clear(){
        sentinel=new Node(null,null,null,null);
        sentinel.right=sentinel;
        sentinel.left=sentinel;
        size=0;
    }
    /* Returns true if this map contains a mapping for the specified key. */
    @Override
    public boolean containsKey(K key) {
        Node temp = sentinel.right;
        while (temp != sentinel) {
            if (key.compareTo(temp.key) == 0) {
                return true;
            }
            if (key.compareTo(temp.key) > 0) {
                temp = temp.right;
            } else {
                temp = temp.left;
            }
        }
        return false;
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key){
        Node temp = sentinel.right;
        while (temp != sentinel) {
            if (key.compareTo(temp.key) == 0) {
                return temp.value;
            }
            if (key.compareTo(temp.key) > 0) {
                temp = temp.right;
            } else {
                temp = temp.left;
            }
        }
        return null;
    }
    /* Returns the number of key-value mappings in this map. */
    @Override
    public int size(){
        return size;
    }

    /* Associates the specified value with the specified key in this map. */
    @Override
    public void put(K key, V value){
        Node temp = sentinel.right;
        if(temp==sentinel){
            sentinel.right=new Node(key,value,sentinel,sentinel);
            size++;
            return;
        }
        while (true) {
            if (key.compareTo(temp.key) == 0) {
                temp.value=value;
                return;
            }
            if (key.compareTo(temp.key) > 0) {
                if(temp.right==sentinel){
                    temp.right=new Node(key,value,sentinel,sentinel);
                    size++;
                    return;
                }
                else {
                    temp = temp.right;
                }
            } else {
                if(temp.left==sentinel){
                    temp.left=new Node(key,value,sentinel,sentinel);
                    size++;
                    return;
                }
                else {
                    temp = temp.left;
                }
            }
        }
    }

    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    @Override
    public Set<K> keySet(){
        throw new UnsupportedOperationException();
    }

    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    @Override
    public V remove(K key){
        throw new UnsupportedOperationException();
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    @Override
    public V remove(K key, V value){
        throw new UnsupportedOperationException();
    }
    @Override
    public Iterator<K> iterator(){
        throw new UnsupportedOperationException();
    }
}
