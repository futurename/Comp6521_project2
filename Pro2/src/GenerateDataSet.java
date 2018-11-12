
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;

public class GenerateDataSet {
    static final int BASKETS_NUM = 10000;
    static final int SUPPORT = 4;
    static final int ITEM_RANGE = 99;
    static final int MIN_ITEMS_IN_BASKETS_NUM = 3;

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        System.out.println("free mem at beginning: " + Runtime.getRuntime().freeMemory());
        BufferedWriter bfw = new BufferedWriter(new FileWriter("./input1.txt", true));
        bfw.write(BASKETS_NUM + " ");
        bfw.write(SUPPORT + "");
        bfw.write("\n");
        for(int i = 0; i < BASKETS_NUM; i++){
            bfw.write((i+1) + "");
            int numOfItems = genRandomItemNum(ITEM_RANGE) + MIN_ITEMS_IN_BASKETS_NUM;
            LinkedHashSet<Integer> curBasket = genOneBasket(numOfItems);
            Iterator<Integer> itr = curBasket.iterator();
            while(itr.hasNext()){
                int curNum = itr.next();
                bfw.write("," + curNum);
            }
            bfw.write("\n");
            bfw.flush();
        }
        bfw.close();
        long endTime = System.currentTimeMillis();
        System.out.println("freemem at end: " + Runtime.getRuntime().freeMemory());
        System.out.println("Time spent: " + (endTime - startTime));
    }

    static int genRandomItemNum(int range){
        Random rand = new Random();
        return rand.nextInt(range + 1);
    }

    static LinkedHashSet<Integer> genOneBasket(int numOfItems){
        LinkedHashSet<Integer> basketSet = new LinkedHashSet<>();
        for(int i = 0; i < numOfItems; i++){
            int curNum = genRandomItemNum(ITEM_RANGE);
            basketSet.add(curNum);
        }
        return basketSet;
    }
}
