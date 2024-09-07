/**
 @Author mohamed abdellatif
 */
package deque;

import java.util.Iterator;

public interface Deque<tefa>{
    public void addFirst(tefa item);
    public void addLast(tefa item);
    default boolean isEmpty(){
        return size()==0;
    }
    public int size();
    public void printDeque();
    public tefa removeFirst();
    public tefa removeLast();
    public tefa get(int index);
}
