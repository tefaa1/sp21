package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    @Test
    public void testThreeAddThreeRemove(){
        AListNoResizing a=new AListNoResizing<>();
        BuggyAList b=new BuggyAList<>();
        a.addLast(1);
        a.addLast(2);
        a.addLast(3);
        b.addLast(1);
        b.addLast(2);
        b.addLast(3);
        assertEquals("error",a.removeLast(),b.removeLast());
        assertEquals("error",a.removeLast(),b.removeLast());
        assertEquals("error",a.removeLast(),b.removeLast());
    }
    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                assertEquals("error in get size ",size,B.size());
                System.out.println("size: " + size);
            }
            else if(operationNumber == 2){
                if(L.size()>0){
                    assertEquals("error in get last",L.getLast(),B.getLast());
                    System.out.println(L.getLast());
                }
            }
            else if(operationNumber == 3){
                if(L.size()>0) {
                    int ll = L.removeLast();
                    int bb = B.removeLast();
                    assertEquals("error in remove last", ll, bb);
                    System.out.println(ll);
                }
            }
            else{
                if(L.size()>0){
                    assertEquals("error in get",L.get(L.size()/2),B.get(B.size()/2));
                }
            }
        }
    }
  // YOUR TESTS HERE
}
