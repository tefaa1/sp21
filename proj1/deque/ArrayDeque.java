/**
 @Author mohamed abdellatif
 */
package deque;

import java.util.Iterator;

public class ArrayDeque<tefa> implements Deque<tefa>,Iterable<tefa>{

    private tefa []a;
    private int size;
    private int capacity;
    private int nextFirst;
    private int nextLast;
    public ArrayDeque(){
        capacity=8;
        size=0;
        a=(tefa[]) new Object[capacity];
        nextFirst=7;
        nextLast=0;
    }
    private void resize(int before_capacity){
        tefa []temp=(tefa[])new Object[capacity];
        for(int i=nextFirst+1,j=0;j<size;i++,j++){
            i%=before_capacity;
            temp[j]=a[i];
        }
        a=temp;
        nextFirst=capacity-1;
        nextLast=size;
    }
    @Override
    public void addFirst(tefa val){
        if(size==capacity) {
            capacity*=2;
            resize(capacity/2);
        }
        a[nextFirst]=val;
        nextFirst--;
        nextFirst+=capacity;
        nextFirst%=capacity;
        size++;
    }
    @Override
    public void addLast(tefa val){
        if(size==capacity){
            capacity*=2;
            resize(capacity/2);
        }
        a[nextLast]=val;
        nextLast++;
        nextLast%=capacity;
        size++;
    }
    @Override
    public int size(){return size;}
    @Override
    public void printDeque(){
        int temp=nextFirst+1;
        for(int i=0;i<size;i++,temp++){
            temp%=capacity;
            System.out.print(a[temp]+" ");
        }
        System.out.println();
    }
    @Override
    public tefa removeFirst(){
        if(size==0)return null;
        nextFirst++;
        nextFirst%=capacity;
        tefa temp=a[nextFirst];
        a[nextFirst]=null;
        size--;
        if(size*100.0/capacity<25){
            capacity/=2;
            resize(capacity*2);
        }
        return temp;
    }
    @Override
    public tefa removeLast(){
        if(size==0)return null;
        nextLast--;
        nextLast+=capacity;
        nextLast%=capacity;
        tefa temp=a[nextLast];
        a[nextLast]=null;
        size--;
        if(size*100.0/capacity<25){
            capacity/=2;
            resize(capacity*2);
        }
        return temp;
    }
    @Override
    public tefa get(int index){
        if(index>=size)return null;
        return a[(nextFirst+index+1)%capacity];
    }
    private class ArrayDequeIterator <tefa>implements Iterator<tefa>{
        private int pos=0;
        public boolean hasNext(){
            return pos<size();
        }
        public tefa next(){
            tefa x =(tefa)get(pos);
            pos++;
            return x;
        }
    }
    @Override
    public Iterator<tefa>iterator(){
        return new ArrayDequeIterator();
    }
//    @Override
//    public boolean equals(Object o){
//        if(o instanceof ArrayDeque AAD){
//            if(AAD.size()!=size())return false;
//            for(int i=0;i<size();i++){
//                if(get(i)!=AAD.get(i))return false;
//            }
//            return true;
//        }
//        return false;
//    }
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
}
