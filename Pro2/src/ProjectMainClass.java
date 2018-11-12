import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

public class ProjectMainClass {
    static int totalNum;
    static int supportNum;
    static int ITEM_RANGE = 99;
    static ArrayList<DataSetStructClass> dataSetTreeArray;
    static final String OUPUT_FILENAME = "./output.txt";

    public static void main(String[] args) throws IOException {
        BufferedReader bfr = new BufferedReader(new FileReader("./input.txt"));
        String firstLine = bfr.readLine();
        String[] firstLineArr = firstLine.split(" ");
        totalNum = Integer.parseInt(firstLineArr[0]);
        supportNum = Integer.parseInt(firstLineArr[1]);
        System.out.println("totalNum: " + totalNum + ", supportNum: " + supportNum);
        dataSetTreeArray = new ArrayList<>();
        int[] itemCounter = new int[ITEM_RANGE + 1];

        long startTime = System.currentTimeMillis();
        System.out.println("free mem at beginning: " + Runtime.getRuntime().freeMemory());

        /*
            Pass one: read data from data set and add each basket to an arraylist and set corresponding item counter
            for each item number inserted;
         */
        for(int i = 0 ; i < totalNum; i++){
            String curLine = bfr.readLine();
            String[] curLineArr = curLine.split(",");
            DataSetStructClass curBasket = new DataSetStructClass(i);
            for(int j = 1; j < curLineArr.length; j++){
                int curNum = Integer.parseInt(curLineArr[j]);
                //System.out.println("j: " + j + ", curNum:" + curNum);
                itemCounter[curNum]++;
                curBasket.treeSet.add(curNum);
            }
            dataSetTreeArray.add(curBasket);
        }
        /*for(int i = 0; i < dataSetTreeArray.size(); i++){
            TreeSet<Integer> curTreeSet = dataSetTreeArray.get(i).treeSet;
            Iterator itr = curTreeSet.iterator();
            System.out.printf("%d: ", i+1);
            while(itr.hasNext()){
                System.out.printf("%d, ",itr.next());
            }
            System.out.println();
        }*/
        //writeToFile("output.txt", dataSetTreeArray);
        //System.out.println(dataSetTreeArray);

        /*
            reduce data set acoording to support threshold
         */
        ArrayList<Integer> frequentItemArray = new ArrayList<>();
        for(int i = 0; i < ITEM_RANGE; i++){
            if(itemCounter[i] >= supportNum){
                frequentItemArray.add(i);
                //itemCounter[i] = -1;
                System.out.println(i + ": " + itemCounter[i] );
            }
        }
        //System.out.println(frequentItemArray);

        int maxSizeOfItemSets = frequentItemArray.size();
        System.out.println("Max size of item set: " + maxSizeOfItemSets);

        /*
            iterate constructing item sets from size two to the max size.
         */
        for(int i = 2; i <= maxSizeOfItemSets; i++) {
            ItemCombination.testItemCombination(frequentItemArray, i);
        }


        long endTime = System.currentTimeMillis();
        System.out.println("freemem at end: " + Runtime.getRuntime().freeMemory());
        System.out.println("Time spent: " + (endTime - startTime));
    }

    static void writeToFile(String filename, ArrayList<DataSetStructClass> arrayList) throws IOException {
        BufferedWriter bfw = new BufferedWriter(new FileWriter(filename,true));
        for(int i = 0; i < arrayList.size(); i++){
            String curLine = (i+1) + " ";
            Iterator<Integer> itr = arrayList.get(i).treeSet.iterator();
            while(itr.hasNext()){
                curLine += itr.next() + ",";
            }
            bfw.write(curLine + "\n");
            bfw.flush();
        }
        bfw.close();
    }


}