/**
 @Author mohamed abdellatif
 */
package deque;

import java.util.Iterator;


public class LinkedListDeque<tefa> implements Deque<tefa>,Iterable<tefa>{
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

    public LinkedListDeque() {
        sentinel = new node();
        sentinel.next=sentinel;
        sentinel.prev=sentinel;
        size = 0;
    }
    @Override
    public void addFirst(tefa val) {
        node first=new node(val,sentinel.next,sentinel);
        sentinel.next.prev=first;
        sentinel.next=first;
        size++;
    }
    @Override
    public void addLast(tefa val) {
        node last=new node(val,sentinel,sentinel.prev);
        sentinel.prev.next=last;
        sentinel.prev=last;
        size++;
    }

    @Override
    public int size() {
        return size;
    }
    @Override
    public void printDeque(){
        node temp=sentinel.next;
        while(temp!=sentinel){
            System.out.print(temp.value+" ");
            temp=temp.next;
        }
        System.out.println();
    }
    @Override
    public tefa removeFirst(){
        if(isEmpty())return null;
        tefa temp=sentinel.next.value;
        sentinel.next=sentinel.next.next;
        sentinel.next.prev=sentinel;
        size--;
        return temp;
    }
    @Override
    public tefa removeLast(){
        if(isEmpty())return null;
        tefa temp=sentinel.prev.value;
        sentinel.prev=sentinel.prev.prev;
        sentinel.prev.next=sentinel;
        size--;
        return temp;
    }
    @Override
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
    private class LinkedListDequeIterator <tefa> implements Iterator<tefa> {
        int pos;
        public LinkedListDequeIterator(){
            pos=0;
        }
        public boolean hasNext(){
            return pos<size();
        }
        public tefa next(){
            tefa x=(tefa)get(pos);
            pos++;
            return x;
        }
    }
    @Override
    public Iterator<tefa>iterator(){
        return new LinkedListDequeIterator();
    }
    @Override
    public boolean equals(Object o){
        if(o instanceof LinkedListDeque AAD){
            if(AAD.size()!=size())return false;
            for(int i=0;i<size();i++){
                if(get(i)!=AAD.get(i))return false;
            }
            return true;
        }
        return false;
    }
    @Override
    public String toString(){
        if(size()==0)return "{}";
        StringBuilder s=new StringBuilder("{");
        int cnt=1;
        for(tefa it:this){
            s.append(it);
            if(cnt<size())
                s.append(", ");
            cnt++;
        }
        s.append("}");
        return s.toString();
    }
    public static void main(String []args){
        LinkedListDeque<Integer>d=new LinkedListDeque<>();
        d.addFirst(5);
        for(int a:d) System.out.println(a);
    }
}
