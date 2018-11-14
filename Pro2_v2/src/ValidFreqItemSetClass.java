import java.util.ArrayList;
import java.util.TreeSet;

public class ValidFreqItemSetClass {
    ArrayList<TreeSet<Integer>> dataList;

    ValidFreqItemSetClass(){
        dataList = new ArrayList<>();

    }

    void addElement(TreeSet<Integer> treeSet){
        dataList.add(treeSet);
    }

    void removeElement(int elementNum){
        dataList.remove(elementNum);
    }
}
