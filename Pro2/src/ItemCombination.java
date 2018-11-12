import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ItemCombination {
    static ArrayList<String> lastRoundValidatedCombination;
    static TreeSet<Integer> curFrequentSet;
    static int mapsetCounter = 0;

    static void testItemCombination(ArrayList<Integer> numArray, int numForCombination) throws IOException {
        curFrequentSet = new TreeSet<>();
        LinkedHashMap<Integer, ItemsetStructClass> validatedItemSetMap = new LinkedHashMap<>();
        int arrayLength = numArray.size();
        int[] binaryShiftArray = new int[arrayLength];
        if (arrayLength <= numForCombination) {
            System.out.println("num for combination is equal the size of num array! finish!");
            return;
        } else {
            for (int i = 0; i < numForCombination; i++) {
                binaryShiftArray[i] = 1;
            }
        }
        ArrayList<Integer> firstCombinationArray = new ArrayList<>();
        for (int i = 0; i < numForCombination; i++) {
            firstCombinationArray.add(numArray.get(i));
        }
        processItemSet(firstCombinationArray, validatedItemSetMap);

        while (isContainOneZero(binaryShiftArray)) {
            transformBinaryArray(binaryShiftArray);
            ArrayList<Integer> curCombinatioArray = new ArrayList<>();
            for (int i = 0; i < binaryShiftArray.length; i++) {
                if (binaryShiftArray[i] == 1) {
                    curCombinatioArray.add(numArray.get(i));
                }
            }
            //System.out.println("CurArray: " + curCombinatioArray);
            processItemSet(curCombinatioArray, validatedItemSetMap);
        }

        int value = getNumOfMatchedSet(numArray);
        if (value > 0 && numForCombination == arrayLength - 1) {
            if (value >= ProjectMainClass.supportNum) {
                processItemSet(numArray, validatedItemSetMap);
                //System.out.println("add set: " + itemSetKey + ", count: " + value);
            }
        }
        writeOutputFile(ProjectMainClass.OUPUT_FILENAME, validatedItemSetMap);
    }

    private static void addCombToRefArray(ArrayList<Integer> arrayList) {
        String result = "";
        for(int num: arrayList){
            result += num;
        }
        lastRoundValidatedCombination.add(result);
    }

    private static void addToValidatedMap(ArrayList<Integer> arrayList, int value,LinkedHashMap<Integer,
            ItemsetStructClass> map) {
        ItemsetStructClass curItemStruct = new ItemsetStructClass();
        for(int num : arrayList){
            curItemStruct.itemArray.add(num);
            //System.out.println("adding num: " + num);
        }
        curItemStruct.count = value;
        map.put(++mapsetCounter, curItemStruct);
        System.out.println("adding itemset - counter: " + mapsetCounter + ", itemset: " + curItemStruct.itemArray +
                ", count: " +curItemStruct.count);

    }

    private static void processItemSet(ArrayList<Integer> arrayList, LinkedHashMap<Integer, ItemsetStructClass> map) {
            int value = getNumOfMatchedSet(arrayList);
            if (value >= ProjectMainClass.supportNum) {
                addToValidatedMap(arrayList, value, map);
                addToFrequentSet(arrayList);
                //System.out.println("adding: " + arrayList + ", count: " + value);
            }
        }

    private static void addToFrequentSet(ArrayList<Integer> arrayList) {
        for (int i : arrayList) {
            curFrequentSet.add(i);
        }
    }

    private static void writeOutputFile(String outputFilename, LinkedHashMap<Integer, ItemsetStructClass> validatedItemSetMap) throws IOException {
        Set<Integer> keySet = validatedItemSetMap.keySet();
        Iterator itr = keySet.iterator();
        BufferedWriter bfw = new BufferedWriter(new FileWriter(outputFilename, true));
        while (itr.hasNext()) {
            ItemsetStructClass curItemResult = validatedItemSetMap.get(itr.next());
            ArrayList<Integer> curArrayList = curItemResult.itemArray;
            int countValue = curItemResult.count;
            //System.out.println("Writing array: " + curArrayList + ", count: " + countValue);
            String str = convertArrayToString(curArrayList);
            str += countValue;
            bfw.write(str + "\n");
            bfw.flush();
            //System.out.println(str + value);
        }
        bfw.close();
    }

    private static String convertArrayToString(ArrayList<Integer> arrayList) {
        String result = "{";
        for (int i = 0; i < arrayList.size() - 1; i++) {
            result += arrayList.get(i) + ",";
        }
        result += arrayList.get(arrayList.size() - 1) + "} - ";
        return result;
    }

    private static void transformBinaryArray(int[] binaryShiftArray) {
        for (int i = 0; i < binaryShiftArray.length - 1; i++) {
            if (binaryShiftArray[i] == 1 && binaryShiftArray[i + 1] == 0) {
                swap(binaryShiftArray, i);
                int n = getNumOfOnes(binaryShiftArray, i);
                for (int j = 0; j < i; j++) {
                    if (j <= n - 1) {
                        binaryShiftArray[j] = 1;
                    } else {
                        binaryShiftArray[j] = 0;
                    }
                }
                return;
            }
        }
    }

    private static int getNumOfOnes(int[] binaryShiftArray, int i) {
        int result = 0;
        for (int j = 0; j < i; j++) {
            if (binaryShiftArray[j] == 1) {
                result++;
            }
        }
        return result;
    }

    private static void swap(int[] binaryShiftArray, int i) {
        int temp = binaryShiftArray[i];
        binaryShiftArray[i] = binaryShiftArray[i + 1];
        binaryShiftArray[i + 1] = temp;
    }

    private static int getNumOfMatchedSet(ArrayList<Integer> curCombinatioArray) {
        int counter = 0;
        //System.out.println("prepare to valide array: " + curCombinatioArray);
        if(compareArrayWithLastRound(curCombinatioArray)) {
            int treeArraySize = ProjectMainClass.dataSetTreeArray.size();
            for (int i = 0; i < treeArraySize; i++) {
                boolean isAllIncluded = true;
                TreeSet<Integer> curTreeSet = ProjectMainClass.dataSetTreeArray.get(i).treeSet;
                if (curCombinatioArray.size() > curTreeSet.size()) {
                    continue;
                } else {
                    for (int j = 0; j < curCombinatioArray.size(); j++) {
                        if (!curTreeSet.contains(curCombinatioArray.get(j))) {
                            isAllIncluded = false;
                            break;
                        }
                    }
                }
                if (isAllIncluded) {
                    counter++;
                }
                //System.out.println(curCombinatioArray + ", counter: " + counter);
                isAllIncluded = true;
            }
        }
        return counter;
    }

    private static boolean compareArrayWithLastRound(ArrayList<Integer> curCombinatioArray) {
        boolean result = false;
        String curString = "";
        for(int num: curCombinatioArray){
            curString += num;
        }
        for(int i = 0; i < lastRoundValidatedCombination.size(); i++){
            String strInLastRound = lastRoundValidatedCombination.get(i);
            //System.out.println("str in last round: " + strInLastRound + ", curString: " + curString);
            if(curString.contains(strInLastRound)){
                result = true;
                break;
            }
        }
        return result;
    }

    private static boolean isContainOneZero(int[] binaryShiftArray) {
        boolean result = false;
        for (int i = 0; i < binaryShiftArray.length - 1; i++) {
            if (binaryShiftArray[i] == 1 && binaryShiftArray[i + 1] == 0) {
                result = true;
                break;
            }
        }
        return result;
    }
}
