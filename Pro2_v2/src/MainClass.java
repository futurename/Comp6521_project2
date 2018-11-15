import java.io.*;
import java.util.*;

public class MainClass {
    static ArrayList<TreeSet<Integer>> dataTreeArray;
    static final int NUM_RANGE = 99;
    static int[] frequentNumArray = new int[NUM_RANGE + 1];
    static final int BASKET_NUM = 150;
    static final int SUPPORT_NUM = 30;
    static final String INPUT_FILE = "input" + BASKET_NUM + "_" + SUPPORT_NUM + ".txt";
    static final String OUTPUT_FILE = "output" + BASKET_NUM + "_" + SUPPORT_NUM + ".txt";
    static ArrayList<TreeMap<String, Integer>> freqCombArrayMap = new ArrayList<>();
    static ArrayList<Integer> curFreqItem;

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        dataTreeArray = new ArrayList<>();
        createTreeSet();
        passOneProcess();
        passTwoProcess();
        int maxSizeOfFreqItem = curFreqItem.size();
        for (int i = 3; i <= maxSizeOfFreqItem; i++) {
            multiPassProcess(i);
            //System.out.println("round: " + i + ", freqitems: " + curFreqItem);
            maxSizeOfFreqItem = curFreqItem.size();
        }
        //writeTreeToFile(dataTreeArray);

        long endTime = System.currentTimeMillis();
        long runningTime = endTime - startTime;
        System.out.println("Running time: " + runningTime + " ms");
    }

    private static void passTwoProcess() throws IOException {
        TreeMap<String, Integer> curValidComb = new TreeMap<>();
        for (int i = 0; i < curFreqItem.size(); i++) {
            int numOne = curFreqItem.get(i);
            int numTwo, counter = 0;
            for (int j = i + 1; j < curFreqItem.size(); j++) {
                numTwo = curFreqItem.get(j);
                counter = countPairsInPassTwo(numOne, numTwo);
                if (counter >= SUPPORT_NUM) {
                    ArrayList tempList = new ArrayList();
                    tempList.add(numOne);
                    tempList.add(numTwo);
                    frequentNumArray[numOne]++;
                    frequentNumArray[numTwo]++;
                    //System.out.println("one: " + numOne + ", two: " + numTwo + ", freqOne: " +
                    // frequentNumArray[numOne] + ", freqTwo: " + frequentNumArray[numTwo]);
                    String validFreqKey = genFreqKey(tempList);
                    curValidComb.put(validFreqKey, counter);
                }
            }
        }
        freqCombArrayMap.add(curValidComb);
        writeToFile(curValidComb);
        getFreqItem();
        //System.out.println("pass two freqitems: " + curFreqItem);
        frequentNumArray = new int[NUM_RANGE + 1];
    }

    private static void getFreqItem() {
        curFreqItem.clear();
        for (int i = 0; i < frequentNumArray.length; i++) {
            if (frequentNumArray[i] != 0) {
                curFreqItem.add(i);
                //System.out.println("pass two, gen curFreqItem: " + curFreqItem);
            }
        }
    }

    private static void multiPassProcess(int numOfItems) throws IOException {
        TreeSet<String> genAllCombSet = new TreeSet<>();
        TreeMap<String, Integer> previousValidMap = freqCombArrayMap.get(0);
        TreeMap<String, Integer> freqStartEndPosMap = new TreeMap<>();
        Set<String> set = previousValidMap.keySet();
        Iterator<String> itr = set.iterator();
        LinkedHashSet<String> keySet = new LinkedHashSet<>();

        while (itr.hasNext()) {
            keySet.add(itr.next());
        }
        //System.out.println("preKeySet: " + keySet);

        for (String str : keySet) {
            /*long startTime = System.currentTimeMillis();*/
            String preCombString = str;
            //System.out.println("---------curPreCombString: " + preCombString);
            String[] preNumbers = preCombString.split(",");

            int[] tempAllNumberArray = new int[NUM_RANGE + 1];
            for (int i = 0; i < preNumbers.length; i++) {
                int pos = Integer.parseInt(preNumbers[i]);
                tempAllNumberArray[pos] = -1;
            }
            for (int i = 0; i < curFreqItem.size(); i++) {
                int curNum = curFreqItem.get(i);
                // tempAllNumberArray[curNum]);
                if (tempAllNumberArray[curNum] != -1 && curNum > Integer.parseInt(preNumbers[preNumbers.length - 1])) {
                    String genNewString = preCombString + "," + curNum;
                    //System.out.println("gen one new string: " + genNewString);
                    LinkedHashSet<String> allSubSets = getAllSubsetList(genNewString);
                    Iterator<String> subSetItr = allSubSets.iterator();
                    boolean isAllContained = true;
                    while (subSetItr.hasNext()) {
                        if (!previousValidMap.containsKey(subSetItr.next())) {
                            isAllContained = false;
                            break;
                        }
                    }
                    if (isAllContained) {
                        genAllCombSet.add(genNewString);
                        tempAllNumberArray[curNum] = -1;
                        //System.out.println("adding new itemset: " + genNewString);
                    }
                }
            }

            /*long endTime = System.currentTimeMillis();
            long runningTime = endTime - startTime;
            System.out.println("Running time: " + runningTime + " ms");*/
        }
        //System.out.println("genall size:" + genAllCombSet.size() + ", curFreqsize: " + curFreqItem.size() + ", keyset size: " + keySet.size());
        ArrayList<String> genAllCombSetToArray = new ArrayList<>(genAllCombSet);
        //System.out.println("gened comb array: " + genAllCombSetToArray);
        //countSubset(genAllCombSetToArray, keySet, numOfItems, freqStartEndPosMap);
        TreeMap<String, Integer> curValidCombMap = countPrunedComb(genAllCombSetToArray);
        freqCombArrayMap.clear();
        freqCombArrayMap.add(curValidCombMap);
        genFreqItemInMultiPass();
        writeToFile(curValidCombMap);
        frequentNumArray = new int[NUM_RANGE + 1];
    }

    private static ArrayList<Integer> getListFromArray(String[] array) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            result.add(Integer.parseInt(array[i]));
        }
        return result;
    }

    private static void genFreqItemInMultiPass() {
        curFreqItem = new ArrayList<>();
        for (int i = 0; i < frequentNumArray.length; i++) {
            if (frequentNumArray[i] != 0) {
                curFreqItem.add(i);
                //System.out.println("addint to freq: " + i);
            }
        }
    }

    private static TreeMap<String, Integer> countPrunedComb(ArrayList<String> prunedGenCombArray) {
        //System.out.println("pruned list:" + prunedGenCombArray);
        long startTime = System.currentTimeMillis();
        TreeMap<String, Integer> result = new TreeMap<>();
        for (int k = 0; k < prunedGenCombArray.size(); k++) {
            String str = prunedGenCombArray.get(k);
            String[] curNumberArray = str.split(",");
            int count = 0;
            //int countIgnored = 0;
            for (int i = 0; i < dataTreeArray.size(); i++) {
                TreeSet<Integer> curTree = dataTreeArray.get(i);
                boolean isAllFound = true;
                if (curTree.size() < curNumberArray.length || curTree.first() > Integer.parseInt(curNumberArray[0])
                        || curTree.last() < Integer.parseInt(curNumberArray[curNumberArray.length - 1])) {
                    //countIgnored++;
                    //* ArrayList<Integer> temList = getListFromArray(curNumberArray);*//*
                    //*System.out.println("ignored--->" + i + ","  + curTree.size() + ", " + (curTree.first() > Integer.parseInt(curNumberArray[0]))
                    //+ ", " + (curTree.last() < Integer.parseInt(curNumberArray[curNumberArray.length -1])) + ", " + (curTree.size() < curNumberArray.length) + "," + temList)*//*;
                    //System.out.println(countIgnored);
                    continue;
                } else {
                    for (int j = 0; j < curNumberArray.length; j++) {
                        int curNum = Integer.parseInt(curNumberArray[j]);
                        if (!curTree.contains(curNum)) {
                            isAllFound = false;
                            break;
                        }
                    }
                }

                if (isAllFound) {
                    count++;
                }
            }
            if (count >= SUPPORT_NUM) {
                result.put(str, count);
                for (int m = 0; m < curNumberArray.length; m++) {
                    int pos = Integer.parseInt(curNumberArray[m]);
                    frequentNumArray[pos]++;
                }
            }
        }
        long endTime = System.currentTimeMillis();
        long runningTime = endTime - startTime;
        System.out.println("Running time: " + runningTime + " ms");
        return result;
    }

    private static LinkedHashSet<String> getAllSubsetList(String str) {
        LinkedHashSet<String> result = new LinkedHashSet<>();
        String[] strArray = str.split(",");
        for (int i = strArray.length - 1; i >= 0; i--) {
            String curStr = "";
            for (int j = 0; j < strArray.length; j++) {
                if (i == j) {
                    continue;
                } else {
                    curStr += strArray[j] + ",";
                }
            }
            curStr = curStr.substring(0, curStr.length() - 1);
            //System.out.println("gen all sub set: " + curStr);
            result.add(curStr);
        }
        return result;
    }

    private static ArrayList<Integer> genStringToList(String str) {
        ArrayList<Integer> result = new ArrayList<>();
        String[] temStrList = str.split(",");
        for (int i = 0; i < temStrList.length; i++) {
            result.add(Integer.parseInt(temStrList[i]));
        }
        return result;
    }


    private static int getLastNum(String str) {
        String[] strArray = str.split(",");
        int result = Integer.parseInt(strArray[strArray.length - 1]);
        return result;
    }

    private static void writeToFile(TreeMap<String, Integer> map) throws IOException {
        Set<String> set = map.keySet();
        Iterator<String> itr = set.iterator();
        BufferedWriter bfw = new BufferedWriter(new FileWriter(OUTPUT_FILE, true));
        while (itr.hasNext()) {
            String curLine = itr.next();
            int count = map.get(curLine);
            curLine = "{" + curLine + "} - " + count;
            bfw.write(curLine);
            bfw.newLine();
        }
        bfw.close();
    }

    private static String genFreqKey(ArrayList<Integer> validFreqValue) {
        String result = "";
        for (int i = 0; i < validFreqValue.size() - 1; i++) {
            result = validFreqValue.get(i) + ",";
        }
        result += validFreqValue.get(validFreqValue.size() - 1);
        //System.out.println("genFreqKey: " + result);
        return result;
    }

    private static int countPairsInPassTwo(int numOne, int numTwo) {
        int result = 0;
        for (int i = 0; i < dataTreeArray.size(); i++) {
            TreeSet<Integer> curTreeSet = dataTreeArray.get(i);
            if (curTreeSet.contains(numOne) && curTreeSet.contains(numTwo)) {
                result++;
            }
        }
        return result;
    }

    private static void passOneProcess() {
        for (int i = 0; i < BASKET_NUM; i++) {
            TreeSet<Integer> curBasket = dataTreeArray.get(i);
            Iterator<Integer> itr = curBasket.iterator();
            while (itr.hasNext()) {
                int curNum = itr.next();
                frequentNumArray[curNum]++;
            }
        }
        genCurFreqItem();
    }

    private static void genCurFreqItem() {
        curFreqItem = new ArrayList<>();
        for (int i = 0; i < frequentNumArray.length; i++) {
            if (frequentNumArray[i] >= SUPPORT_NUM) {
                curFreqItem.add(i);
                //System.out.println("adding: " + i);
            }
        }
        frequentNumArray = new int[NUM_RANGE + 1];
    }

    private static void createTreeSet() throws IOException {
        BufferedReader bfr = new BufferedReader(new FileReader(INPUT_FILE));
        String firstLine = bfr.readLine();
        String[] firstLineStringArray = firstLine.split(" ");
        int totalBaskets = Integer.parseInt(firstLineStringArray[0]);
        int supportThreshold = Integer.parseInt(firstLineStringArray[1]);
        System.out.println("total baskets: " + totalBaskets + ", support: " + supportThreshold);

        dataTreeArray = new ArrayList<>();

        for (int i = 0; i < totalBaskets; i++) {
            String curLine = bfr.readLine();
            String[] curLineArray = curLine.split(",");
            TreeSet<Integer> curTreeSet = new TreeSet<>();
            for (int j = 1; j < curLineArray.length; j++) {
                int curNum = Integer.parseInt(curLineArray[j]);
                curTreeSet.add(curNum);
            }
            dataTreeArray.add(curTreeSet);
        }
    }

    private static void writeTreeToFile(ArrayList<TreeSet<Integer>> tree) throws IOException {
        BufferedWriter bfw = new BufferedWriter(new FileWriter("sortedTreeSet.txt"));
        for (int i = 0; i < tree.size(); i++) {
            TreeSet<Integer> curTreeSet = tree.get(i);
            Iterator<Integer> itr = curTreeSet.iterator();
            String curLine = (i + 1) + ": ";
            while (itr.hasNext()) {
                int curNum = itr.next();
                curLine += curNum + ",";
            }
            bfw.write(curLine);
            bfw.newLine();
        }
        bfw.close();
    }
}