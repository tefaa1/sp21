/**
 @Author mohamed abdellatif
 */
package deque;
import java.util.Comparator;
public class MaxArrayDeque <tefa>extends ArrayDeque<tefa>{
    private Comparator<tefa>comp;
    public MaxArrayDeque(Comparator<tefa> c){comp=c;}
    public tefa max(Comparator<tefa> c) {
        if (size() == 0) return null;
        tefa mx = get(0);
        for (int i = 0; i < size(); i++) {
            if (c.compare(get(i), mx) > 0) mx = get(i);
        }
        return mx;
    }
    public tefa max() {
        return max(comp);
    }
}
