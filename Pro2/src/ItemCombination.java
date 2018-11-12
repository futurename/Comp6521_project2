import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ItemCombination {
        static LinkedHashMap<String, Integer> lastRoundHashMap;
    static void testItemCombination(ArrayList<Integer> numArray, int numForCombination) throws IOException {
        LinkedHashMap<String, Integer> validatedItemSetMap = new LinkedHashMap<>();
        int arrayLength = numArray.size();
        int[] binaryShiftArray = new int[arrayLength];
        if (arrayLength == numForCombination) {
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
        if (testIsCurCombinationPassed(firstCombinationArray)) {
            int value = getNumOfMatchedSet(firstCombinationArray);
            if(value >= ProjectMainClass.supportNum) {
                String itemSetKey = convertArrayToString(firstCombinationArray);
                validatedItemSetMap.put(itemSetKey, value);
            }
        }

        while (isContainOneZero(binaryShiftArray)) {
            transformBinaryArray(binaryShiftArray);
            //System.out.println(Arrays.toString(binaryShiftArray));
            ArrayList<Integer> curCombinatioArray = new ArrayList<>();
            for (int i = 0; i < binaryShiftArray.length; i++) {
                if (binaryShiftArray[i] == 1) {
                    curCombinatioArray.add(numArray.get(i));
                }
            }
            System.out.println("CurArray: " + curCombinatioArray);
            if (testIsCurCombinationPassed(curCombinatioArray)) {
                int value = getNumOfMatchedSet(curCombinatioArray);
                if(value >= ProjectMainClass.supportNum) {
                    String itemSetKey = convertArrayToString(curCombinatioArray);
                    validatedItemSetMap.put(itemSetKey, value);
                    //System.out.println("add set: " + itemSetKey + ", count: " + value);
                }
            }
        }
        if(testIsCurCombinationPassed(numArray) && numForCombination == arrayLength - 1){
            int value = getNumOfMatchedSet(numArray);
            if(value >= ProjectMainClass.supportNum) {
                String itemSetKey = convertArrayToString(numArray);
                validatedItemSetMap.put(itemSetKey, value);
                //System.out.println("add set: " + itemSetKey + ", count: " + value);
            }
        }
        writeOutputFile(ProjectMainClass.OUPUT_FILENAME, validatedItemSetMap);
    }

    private static void writeOutputFile(String outputFilename, LinkedHashMap<String, Integer> validatedItemSetMap) throws IOException {
        Set<String> keySet = validatedItemSetMap.keySet();
        Iterator itr = keySet.iterator();
        BufferedWriter bfw = new BufferedWriter(new FileWriter(outputFilename, true));
        while (itr.hasNext()) {
            String str = (String) itr.next();
            int value = validatedItemSetMap.get(str);
            bfw.write(str + value + "\n");
            bfw.flush();
            System.out.println(str + value);
        }
        bfw.close();
    }

    private static String convertArrayToString(ArrayList<Integer> firstCombinationArray) {
        String result = "{";
        for (int i = 0; i < firstCombinationArray.size() - 1; i++) {
            result += firstCombinationArray.get(i) + ",";
        }
        result += firstCombinationArray.get(firstCombinationArray.size() - 1) + "} - ";
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

    private static boolean testIsCurCombinationPassed(ArrayList<Integer> curCombinatioArray){
        boolean result = false;
        if(getNumOfMatchedSet(curCombinatioArray) > 0){
            result = true;
        }
        return result;
    }


    private static int getNumOfMatchedSet(ArrayList<Integer> curCombinatioArray) {
        //System.out.println("prepare to valide array: " + curCombinatioArray);
        int treeArraySize = ProjectMainClass.dataSetTreeArray.size();
        int counter = 0;
        for (int i = 0; i < treeArraySize; i++) {
            boolean isAllIncluded = true;
            TreeSet<Integer> curTreeSet = ProjectMainClass.dataSetTreeArray.get(i).treeSet;
            for (int j = 0; j < curCombinatioArray.size(); j++) {
                if (!curTreeSet.contains(curCombinatioArray.get(j))) {
                    isAllIncluded = false;
                    break;
                }
            }
            if(isAllIncluded) {
                counter++;
            }
            //System.out.println(curCombinatioArray + ", counter: " + counter);
            isAllIncluded = true;
        }
        return counter;
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
