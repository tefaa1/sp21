package deque;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDeque<tefa> {
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
    public boolean isEmpty(){return (size()==0);}
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
//    public boolean isEmpty(){return (size()==0);}
    public int size(){return size;}
    public void printDeque(){
        int temp=nextFirst+1;
        for(int i=0;i<size;i++,temp++){
            temp%=capacity;
            System.out.print(a[temp]+" ");
        }
        System.out.println();
    }
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
    public tefa get(int index){
        if(index>=size)return null;
        return a[(nextFirst+index+1)%capacity];
    }
}
