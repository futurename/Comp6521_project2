import java.util.ArrayList;
import java.util.HashSet;

public class LastRoundValidCombClass {
    int curIndx;
    ArrayList<HashSet<String>> indexArray;

    LastRoundValidCombClass(int num) {
        curIndx = num;
        HashSet<String> setHead = new HashSet<>();
        indexArray.set(curIndx, setHead);
    }
}
