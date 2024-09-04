package deque;

import static org.junit.Assert.assertEquals;

public class LinkedListDeque<tefa> {
    private class node {
        node(){}
        node(tefa val, node nex, node pre) {
            value = val;
            next = nex;
            prev = pre;
        }

        tefa value;
        node next;
        node prev;
    }

    private node sentinel;
    private int size;

    LinkedListDeque() {
        sentinel = new node();
        sentinel.next=sentinel;
        sentinel.prev=sentinel;
        size = 0;
    }

    LinkedListDeque(tefa val) {
        size = 1;
        sentinel=new node();
        sentinel.next=sentinel.prev=new node(val,sentinel,sentinel);
    }

    public void addFirst(tefa val) {
        node first=new node(val,sentinel.next,sentinel);
        sentinel.next.prev=first;
        sentinel.next=first;
        size++;
    }

    public void addLast(tefa val) {
        node last=new node(val,sentinel,sentinel.prev);
        sentinel.prev.next=last;
        sentinel.prev=last;
        size++;
    }

//    public boolean isEmpty() {return (size() == 0);}

    public int size() {
        return size;
    }
    public void printDeque(){
        node temp=sentinel.next;
        while(temp!=sentinel){
            System.out.print(temp.value+" ");
            temp=temp.next;
        }
        System.out.println();
    }
    public boolean isEmpty(){return (size()==0);}
    public tefa removeFirst(){
        if(isEmpty())return null;
        tefa temp=sentinel.next.value;
        sentinel.next=sentinel.next.next;
        sentinel.next.prev=sentinel;
        size--;
        return temp;
    }
    public tefa removeLast(){
        if(isEmpty())return null;
        tefa temp=sentinel.prev.value;
        sentinel.prev=sentinel.prev.prev;
        sentinel.prev.next=sentinel;
        size--;
        return temp;
    }
    public tefa get(int index){
        if(index>=size)return null;
        node temp=sentinel.next;
        while(index>0){
            temp=temp.next;
            index--;
        }
        return temp.value;
    }
    /** helper method*/
    private tefa rec(int index,node temp){
        if(index==0)return temp.value;
        return rec(index-1,temp.next);
    }
    public tefa getRecursive(int index){
        if(index>=size)return null;
        return rec(index,sentinel.next);
    }
    public static void main(String []args){
    }
}
