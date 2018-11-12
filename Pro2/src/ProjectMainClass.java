import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class ProjectMainClass {
    static int totalNum;
    static int supportNum;
    static int ITEM_RANGE = 99;
    static ArrayList<TreesetStructClass> dataSetTreeArray;
    static final String OUPUT_FILENAME = "./output.txt";
    static ArrayList<Integer> frequentItemArray;

    public static void main(String[] args) throws IOException {
        BufferedReader bfr = new BufferedReader(new FileReader("./input1.txt"));
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
        for (int i = 0; i < totalNum; i++) {
            String curLine = bfr.readLine();
            String[] curLineArr = curLine.split(",");
            TreesetStructClass curBasket = new TreesetStructClass(i);
            for (int j = 1; j < curLineArr.length; j++) {
                int curNum = Integer.parseInt(curLineArr[j]);
                //System.out.println("j: " + j + ", curNum:" + curNum);
                itemCounter[curNum]++;
                curBasket.treeSet.add(curNum);
            }
            dataSetTreeArray.add(curBasket);
        }

        /*
            reduce data set acoording to support threshold
         */
        frequentItemArray = new ArrayList<>();
        for (int i = 0; i < ITEM_RANGE; i++) {
            if (itemCounter[i] >= supportNum) {
                frequentItemArray.add(i);
                //itemCounter[i] = -1;
                //System.out.println(i + ": " + itemCounter[i]);
            }
        }
        //System.out.println(frequentItemArray);

        int maxSizeOfItemSets = frequentItemArray.size();
        //System.out.println("Max size of item set: " + maxSizeOfItemSets);

        /*
            iterate constructing item sets from size two to the max size.
         */
        int start = 2;
        for (int i = start; i <= maxSizeOfItemSets; i++) {
            ItemCombination.lastRoundValidatedCombination = convertIntArrayToStrArray(frequentItemArray);
            System.out.println("Frequent item for this round: " + frequentItemArray);
            System.out.println("max size for this round; " + maxSizeOfItemSets);
            ItemCombination.testItemCombination(frequentItemArray, i);
            frequentItemArray = new ArrayList<>(ItemCombination.curFrequentSet);
            maxSizeOfItemSets = frequentItemArray.size();
            //System.out.println("Frequent item for next round: " + frequentItemArray);
            //System.out.println("max size for next round; " + maxSizeOfItemSets);
            System.out.println("i value: " + i);
            //resizeDataSetTreeArray(dataSetTreeArray,frequentItemArray);
            //printTreeSet(dataSetTreeArray);
            //writeToFile("resized_datatree.txt", dataSetTreeArray);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("freemem at end: " + Runtime.getRuntime().freeMemory());
        System.out.println("Time spent: " + (endTime - startTime));
    }

    private static ArrayList<String> convertIntArrayToStrArray(ArrayList<Integer> frequentItemArray) {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < frequentItemArray.size(); i++) {
            int num = frequentItemArray.get(i);
            result.add(num + "");
            //System.out.println("adding num:" + num);
        }
        return result;
    }

    private static void printTreeSet(ArrayList<TreesetStructClass> dataSetTreeArray) {
        int arrayLenth = dataSetTreeArray.size();
        System.out.printf("resized treeset: ");
        for (TreesetStructClass data : dataSetTreeArray) {
            Iterator itr = data.treeSet.iterator();
            while (itr.hasNext()) {
                System.out.printf("%d, ", itr.next());
            }
        }
        System.out.println();
    }

    private static void resizeDataSetTreeArray(ArrayList<TreesetStructClass> dataSetTreeArray, ArrayList<Integer> frequentItemArray) {
        int arrayLength = dataSetTreeArray.size();
        for (TreesetStructClass treeSet : dataSetTreeArray) {
            Iterator itr = treeSet.treeSet.iterator();
            while (itr.hasNext()) {
                int curValue = (int) itr.next();
                if (!frequentItemArray.contains(curValue)) {
                    treeSet.treeSet.remove(curValue);
                    System.out.println("remove value: " + curValue);
                }

            }
        }
    }

    static void writeToFile(String filename, ArrayList<TreesetStructClass> arrayList) throws IOException {
        BufferedWriter bfw = new BufferedWriter(new FileWriter(filename, true));
        for (int i = 0; i < arrayList.size(); i++) {
            String curLine = (i + 1) + ":";
            Iterator<Integer> itr = arrayList.get(i).treeSet.iterator();
            while (itr.hasNext()) {
                curLine += itr.next() + ",";
            }
            bfw.write(curLine + "\n");
            bfw.flush();
        }
        bfw.close();
    }


}