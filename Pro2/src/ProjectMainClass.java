import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class ProjectMainClass {
    static int totalNum;
    static int supportNum;
    static int ITEM_RANGE = 99;

    public static void main(String[] args) throws IOException {
        BufferedReader bfr = new BufferedReader(new FileReader("./input1.txt"));
        String firstLine = bfr.readLine();
        String[] firstLineArr = firstLine.split(" ");
        totalNum = Integer.parseInt(firstLineArr[0]);
        supportNum = Integer.parseInt(firstLineArr[1]);
        ArrayList<DataSetStructClass> DataSetArray = new ArrayList<>();
        int[] itemCounter = new int[ITEM_RANGE];

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
                itemCounter[curNum]++;
                curBasket.treeSet.add(curNum);
            }
            DataSetArray.add(curBasket);
        }

        /*
            reduce data set acoording to support threshold
         */
        ArrayList<Integer> frequentItemArray = new ArrayList<>();
        for(int i = 0; i < ITEM_RANGE; i++){
            if(itemCounter[i] >= supportNum){
                frequentItemArray.add(i);
                itemCounter[i] = -1;
            }
        }

        int maxSizeOfItemSets = frequentItemArray.size();

        /*
            iterate constructing item sets from size two to the max size.
         */
        for(int i = 2; i <= maxSizeOfItemSets; i++){
            int[] itemSetOfItrArray = new int[i];

        }


        //writeToFile("output1.txt", DataSetArray);
        long endTime = System.currentTimeMillis();
        System.out.println("freemem at end: " + Runtime.getRuntime().freeMemory());
        System.out.println("Time spent: " + (endTime - startTime));
    }

    static void writeToFile(String filename, ArrayList<DataSetStructClass> arrayList) throws IOException {
        BufferedWriter bfw = new BufferedWriter(new FileWriter(filename,true));
        for(int i = 0; i < arrayList.size(); i++){
            String curLine = arrayList.get(i).basketSeq + " ";
            Iterator<Integer> itr = arrayList.get(i).treeSet.iterator();
            while(itr.hasNext()){
                curLine += itr.next() + ",";
            }
            bfw.write(curLine + "\n");
            bfw.flush();
        }
        bfw.close();
    }

    static ArrayList<Integer> recursiveRetriveItemCombination(ArrayList<Integer> dataArray, int numOfItems){
        int dataLenth = dataArray.size();
        int firstIndex = 0;
        int secondIndex = numOfItems - 2;
        int lastIndex = numOfItems - 1;
        ArrayList<Integer> curItemCombination = new ArrayList<>();
        while(firstIndex <= (dataLenth - numOfItems)){

        }

    }
}