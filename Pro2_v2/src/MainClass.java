import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

public class MainClass {
    static ArrayList<TreeSet<Integer>> dataTreeArray;
    static final int NUM_RANGE = 99;
    static int[] frequentNumArray = new int[NUM_RANGE];
    static final int BASKET_NUM = 50;
    static final int SUPPORT_NUM = 5;
    static final String INPUT_FILE = "input" + BASKET_NUM + "_" + SUPPORT_NUM + ".txt";
    static final String OUPUT_FLE = "output" + BASKET_NUM + "_" + SUPPORT_NUM + ".txt";
    static A

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        dataTreeArray = new ArrayList<>();
        /*
        create arrays of treeset from source data file and sort the items in each basket.
        time compexity of searching an item in a basket is O(log(n))
         */
        createTreeSet();


        //writeTreeToFile(dataTreeArray);

        long endTime = System.currentTimeMillis();
        long runningTime = endTime - startTime;
        System.out.println("Running time: " + runningTime);
    }

    private static void createTreeSet() throws IOException {
        BufferedReader bfr = new BufferedReader(new FileReader(INPUT_FILE));
        String firstLine = bfr.readLine();
        String[] firstLineStringArray = firstLine.split(" ");
        int totalBaskets = Integer.parseInt(firstLineStringArray[0]);
        int supportThreshold = Integer.parseInt(firstLineStringArray[1]);
        System.out.println("total baskets: " + totalBaskets + ", support: " + supportThreshold);

        dataTreeArray = new ArrayList<>(totalBaskets);

        for(int i = 0; i < totalBaskets; i++){
            String curLine = bfr.readLine();
            String[] curLineArray = curLine.split(",");
            TreeSet<Integer> curTreeSet = new TreeSet<>();
            for(int j = 1; j < curLineArray.length; j++){
                int curNum = Integer.parseInt(curLineArray[j]);
                curTreeSet.add(curNum);
            }
            dataTreeArray.add(curTreeSet);
        }
    }

    private static void writeTreeToFile(ArrayList<TreeSet<Integer>> tree) throws IOException {
        BufferedWriter bfw = new BufferedWriter(new FileWriter("sortedTreeSet.txt"));
        for(int i = 0; i < tree.size(); i++){
            TreeSet<Integer> curTreeSet = tree.get(i);
            Iterator<Integer> itr = curTreeSet.iterator();
            String curLine = (i+1) + ": ";
            while(itr.hasNext()){
                int curNum = itr.next();
                curLine += curNum + ",";
            }
            bfw.write(curLine);
            bfw.newLine();
        }
        bfw.close();
    }
}
