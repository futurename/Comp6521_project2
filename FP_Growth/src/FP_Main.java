
import com.sun.org.apache.regexp.internal.REUtil;

import java.io.*;
import java.util.*;

public class FP_Main {

    static int processorNum;
    static int basketNum;
    static int supportNum;
    static final int NUM_RANGE = 99;
    static final int NUM_ARRAY_SIZE = NUM_RANGE + 1;
    static final String INPUT_FILE_NAME = "./input" + 50 + "_" + 5 + ".txt";
    static final String OUTPUT_FILE_NAME = "./output" + 50 + "_" + 5 + ".txt";
    static final String INPUT_DENSE = "./Dense 25000 20000.txt";
    static final String INPUT_SPARSE = "./Sparse 25000 500.txt";
    static HashMap<Integer, Integer> freqCountMap = new HashMap<>();
    static ArrayList<Integer> freqSortedNumArray;
    static ArrayList<TreeNode>[] headerList;
    static Integer tempLastNum;

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();

        initProgram();

        TreeNode fpTree = buildFPTree();

        dataMine(fpTree);

        long endTime = System.currentTimeMillis();
        long runningTime = endTime - startTime;
        System.out.println("Running time: " + runningTime + " ms");
    }

    private static void dataMine(TreeNode root) throws IOException {
        for (int i = freqSortedNumArray.size() - 1; i >= 0; i--) {
            HashMap<ArrayList<Integer>, Integer> paths = new HashMap<>();
            ArrayList<TreeNode> lastNumNodeArray = headerList[i];
            Integer curNodeName = lastNumNodeArray.get(0).getName();
            for (int j = 0; j < lastNumNodeArray.size(); j++) {
                TreeNode curNode = lastNumNodeArray.get(j);
                int curCount = curNode.getCount();
                ArrayList<Integer> onePath = new ArrayList<>();
                onePath.add(curNodeName);

                while (curNode.getParentNode().getName() != null) {
                    TreeNode parentNode = curNode.getParentNode();
                    onePath.add(parentNode.getName());
                    curNode = curNode.getParentNode();
                    //System.out.println(onePath);
                }

                System.out.println("adding, tail num: " + curNodeName + ", one path: " + onePath);

                paths.put(onePath, curCount);
            }
            ArrayList<Integer> freqItemsForOneNum = processFreqPatterns(paths, curNodeName);
            writeAllCombOfItems(freqItemsForOneNum);


            System.out.println("\n---------------------------------> one set of freq items: " + freqItemsForOneNum + "\n");
        }
    }

    private static void writeAllCombOfItems(ArrayList<Integer> freqItemsForOneNum) throws IOException {
        ArrayList<ArrayList<Integer>> allCombList = new ArrayList<>();
        getFullPermuation(freqItemsForOneNum, allCombList);

        BufferedWriter bfw = new BufferedWriter(new FileWriter(OUTPUT_FILE_NAME, true));

        for(ArrayList<Integer> oneList: allCombList){
            String oneComb = "";
            for(Integer item: oneList){
                oneComb += item + ",";
            }

            System.out.println("oneComb: " + oneComb);

            bfw.write(oneComb);
            bfw.newLine();
        }


        bfw.close();
    }

    private static void getFullPermuation(ArrayList<Integer> freqItemsForOneNum,ArrayList<ArrayList<Integer>> allCombList) {
        if (freqItemsForOneNum.size() == 2) {
            allCombList.add(freqItemsForOneNum);
        } else {
            for (int i = 0; i < freqItemsForOneNum.size(); i++) {
                ArrayList<Integer> tempList = new ArrayList<>();
                for (int j = 0; j < freqItemsForOneNum.size(); j++) {
                    if (i != j) {
                        tempList.add(freqItemsForOneNum.get(j));
                    }
                }
                allCombList.add(tempList);

                System.out.println("one temp list: " + tempList);

                getFullPermuation(tempList, allCombList);
            }
        }
    }

    private static ArrayList<Integer> processFreqPatterns(HashMap<ArrayList<Integer>, Integer> recursivePaths,
                                                          Integer nodeName) {
        ArrayList<Integer> result = new ArrayList<>();
        result.add(nodeName);

        if (recursivePaths.size() == 1) {
            Set<ArrayList<Integer>> set = recursivePaths.keySet();
            Iterator<ArrayList<Integer>> itr = set.iterator();
            while (itr.hasNext()) {
                ArrayList<Integer> list = itr.next();
                for (Integer i : list) {
                    result.add(i);

                    System.out.println("recurse ends, add one freq item: " + result);

                }
            }
        } else {
            Set<ArrayList<Integer>> pathKeys = recursivePaths.keySet();
            Iterator<ArrayList<Integer>> itr = pathKeys.iterator();
            HashMap<ArrayList<Integer>, Integer> mapForFPTree = new HashMap<>();
            while (itr.hasNext()) {
                ArrayList<Integer> curPath = itr.next();
                Integer curPathCount = recursivePaths.get(curPath);

               // System.out.println("remove first num: " + curPath.get(0) + ", count:" + curPathCount + ", from the path: " + curPath);

                curPath.remove(0);

                //System.out.println("after removal: " + curPath + ", and add to new hashmap");

                mapForFPTree.put(curPath, curPathCount);
            }
            HashMap<ArrayList<Integer>, Integer> subTreePaths = buildRecursiveFPTreePaths(mapForFPTree);

            processFreqPatterns(subTreePaths, tempLastNum);
        }

        return result;
    }

    private static HashMap<ArrayList<Integer>, Integer> buildRecursiveFPTreePaths(HashMap<ArrayList<Integer>, Integer> mapForFPTree) {
        HashMap<ArrayList<Integer>, Integer> result;

        TreeNode subTreeRoot = new TreeNode(null, 0);
        Set<ArrayList<Integer>> keySet = mapForFPTree.keySet();
        Iterator<ArrayList<Integer>> itr = keySet.iterator();

        //System.out.println("init freq header list, size: " + headerList.length);

        ArrayList<TreeNode>[] subTreeFreqArray = new ArrayList[headerList.length];
        for (int i = 0; i < subTreeFreqArray.length; i++) {
            subTreeFreqArray[i] = new ArrayList<>();
        }
        while (itr.hasNext()) {
            ArrayList<Integer> curPath = itr.next();
            Integer curPathCount = mapForFPTree.get(curPath);

            //System.out.println("reverse curpath in building subtree: " + curPath + ", count: " + curPathCount);

            Collections.reverse(curPath);

            //System.out.println("curpath after reverse : " + curPath);

            for (int i = 0; i < curPathCount; i++) {
                addListToSubTree(curPath, subTreeRoot, subTreeFreqArray);
            }
        }

        result = getSubTreePathsMap(subTreeRoot, subTreeFreqArray);

        return result;
    }

    private static HashMap<ArrayList<Integer>, Integer> getSubTreePathsMap(TreeNode subTreeRoot, ArrayList<TreeNode>[] subTreeFreqArray) {
        HashMap<ArrayList<Integer>, Integer> result = new HashMap<>();

        for (int i = freqSortedNumArray.size() - 1; i >= 0; i--) {
            if (subTreeFreqArray[i].size() != 0) {
                tempLastNum = i;
            }
        }
        ArrayList<TreeNode> lastNumList = subTreeFreqArray[tempLastNum];

        for (TreeNode node : lastNumList) {
            ArrayList<Integer> curPath = new ArrayList<>();
            int count = node.getCount();
            curPath.add(node.getName());

            while (node.getParentNode().getName() != null) {
                node = node.getParentNode();
                curPath.add(node.getName());
            }

            result.put(curPath, count);
        }
        return result;
    }

    private static void addListToSubTree(ArrayList<Integer> curPath, TreeNode subTreeRoot, ArrayList<TreeNode>[] freqArray) {
        TreeNode curNode = subTreeRoot;
        TreeNode parentNode = subTreeRoot;

        System.out.println("inside func addListToSubTree, curPath: " + curPath);

        for (int i = 0; i < curPath.size(); i++) {
            boolean isNodeExist = false;
            Integer curNum = curPath.get(i);

            Set<Integer> childrenKeySet = curNode.getChildNodesMap().keySet();
            Iterator<Integer> itr = childrenKeySet.iterator();

            System.out.printf("cur node: %s, children: ", curNode.getName());

            while (itr.hasNext()) {
                Integer oneChildNum = itr.next();

                System.out.printf("%d, ", oneChildNum);

            }
            System.out.println();

            if (curNode.getChildNodesMap().containsKey(curNum)) {
                isNodeExist = true;
                parentNode = curNode;
                curNode = curNode.getChildNodesMap().get(curNum);
            }

            if (isNodeExist) {
                int count = curNode.getCount() + 1;
                curNode.setCount(count);
                parentNode = curNode;

                System.out.println("subtree: exist, add to num: " + curNode.getName() + "-> " + count + ", curNum: " + curNum);

            } else {
                TreeNode newNode = new TreeNode(curNum, 1);
                newNode.setParentNode(parentNode);
                parentNode.addChildNode(newNode);

                System.out.println("subtree: new node, curNum: " + curNum + ", parent: " + newNode.getParentNode().getName() + ", parent children " +
                        "size: " + newNode.getParentNode().getChildNodesMap().size());

                freqArray[curNum].add(newNode);
                //int pos = freqSortedNumArray.indexOf(curNum);

                //headerList[pos].add(newNode);

                curNode = newNode;
                parentNode = curNode;
            }
        }
    }

    private static TreeNode buildFPTree() throws IOException {
        BufferedReader bfr = new BufferedReader(new FileReader(INPUT_FILE_NAME));
        String firstLine = bfr.readLine();
        String[] firstLineStrings = firstLine.split(" ");
        basketNum = Integer.parseInt(firstLineStrings[0]);
        supportNum = Integer.parseInt(firstLineStrings[1]);

        System.out.println("File info: " + basketNum + " " + supportNum + "\n");

        int[] freqNumArray = new int[NUM_ARRAY_SIZE];
        for (int i = 0; i < basketNum; i++) {
            String curLine = bfr.readLine();
            String[] splitCurLine = curLine.split(",");

            for (int j = 1; j < splitCurLine.length; j++) {
                int curNum = Integer.parseInt(splitCurLine[j]);
                freqNumArray[curNum]++;
            }
        }

        //get freq array sorted by counts.

        for (int i = 0; i < NUM_ARRAY_SIZE; i++) {
            if (freqNumArray[i] >= supportNum) {
                freqCountMap.put(i, freqNumArray[i]);
            }
        }
        bfr.close();

        ArrayList<Integer> tempList = getSortedFreqNumArray(freqCountMap);
        int pos = tempList.size();
        for (int i = 0; i < tempList.size(); i++) {
            if (freqCountMap.get(tempList.get(i)) >= supportNum) {
                continue;
            } else {
                pos = i;
                break;
            }
        }
        //System.out.println("pos: " + pos + ", size: " + tempList.size());
        freqSortedNumArray = new ArrayList<>(tempList.subList(0, pos));
        //System.out.println(freqSortedNumArray);
        headerList = buildHeaderList();

        //printHashMap(freqCountMap);
        System.out.println("freq list: " + freqSortedNumArray + "\n");

        TreeNode root = buildMyFPTree(null, 0);

        //FPTreeTraverse(root);

        //HashMap<ArrayList<Integer>, Integer> condPatternBases = getCondPatternBases(root, 8);

        return root;
    }

    private static ArrayList<TreeNode>[] buildHeaderList() {
        ArrayList<TreeNode>[] result = new ArrayList[freqSortedNumArray.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = new ArrayList<>();
        }
        return result;
    }

    private static TreeNode buildMyFPTree(Integer name, Integer count) throws IOException {
        TreeNode result = new TreeNode(name, count);
        BufferedReader bfr = new BufferedReader(new FileReader(INPUT_FILE_NAME));
        bfr.readLine();
        for (int i = 0; i < basketNum; i++) {
            String curLine = bfr.readLine();

            //System.out.println(curLine);

            String[] splitCurLine = curLine.split(",");
            HashSet<Integer> singleLineList = new HashSet<>();
            for (int j = 1; j < splitCurLine.length; j++) {
                singleLineList.add(Integer.parseInt(splitCurLine[j]));
            }
            ArrayList<Integer> sortedSingleList = getSortedStrings(singleLineList);

            //System.out.println("Line " + (i+1) + " >> " + sortedSingleList);

            addLineToFPTree(sortedSingleList, result);
        }

        bfr.close();

        return result;


    }

    private static void addLineToFPTree(ArrayList<Integer> singleLineList, TreeNode root) {
        TreeNode curNodePointer = root;
        TreeNode parentNodePointer = root;

        for (int i = 0; i < singleLineList.size(); i++) {
            boolean isExist = false;
            Integer curNum = singleLineList.get(i);

            /*Set<Integer> keys = curNodePointer.getChildNodesMap().keySet();
            Iterator<Integer> itr = keys.iterator();
            System.out.printf("cur node: " + curNodePointer.getName() + ", its children: ");
            while(itr.hasNext()){
                System.out.printf("%d, ", itr.next());
            }
            System.out.println();*/

            if (curNodePointer.getChildNodesMap().containsKey(curNum)) {
                isExist = true;
                parentNodePointer = curNodePointer;
                curNodePointer = curNodePointer.getChildNodesMap().get(curNum);
            }

            if (isExist) {
                int count = curNodePointer.getCount() + 1;
                curNodePointer.setCount(count);
                parentNodePointer = curNodePointer;

                //System.out.println("exist, add to num: " + curNodePointer.getName() + ": " + count + ", curNum: " + curNum);

            } else {
                TreeNode curNode = new TreeNode(curNum, 1);
                curNode.setParentNode(parentNodePointer);
                parentNodePointer.addChildNode(curNode);
                int pos = freqSortedNumArray.indexOf(curNum);
                headerList[pos].add(curNode);
                curNodePointer = curNode;
                parentNodePointer = curNodePointer;
            }
        }
    }


    private static ArrayList<Integer> getSortedStrings(HashSet<Integer> singleLineList) {
        ArrayList<Integer> result = new ArrayList<>();
        for (Integer i : freqSortedNumArray) {
            if (singleLineList.contains(i)) {
                result.add(i);
            }
        }
        return result;
    }


    private static ArrayList<Integer> getSortedFreqNumArray(HashMap<Integer, Integer> freqCountMap) {
        List<Map.Entry<Integer, Integer>> list = new ArrayList<Map.Entry<Integer, Integer>>(freqCountMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        ArrayList<Integer> result = new ArrayList<>();
        for (Map.Entry<Integer, Integer> map : list) {
            result.add(map.getKey());
        }
        return result;
    }

    private static void printHashMap(HashMap<Integer, Integer> freqCountMap) {
        Set<Integer> set = freqCountMap.keySet();
        Iterator<Integer> itr = set.iterator();
        //System.out.println(set);
        while (itr.hasNext()) {
            Integer key = itr.next();
            System.out.println(key + ", " + freqCountMap.get(key));
        }
    }

    /*private static void FPTreeTraverse(TreeNode node) {
        ArrayList<ArrayList<Integer>> paths = new ArrayList<>();
        for (int i = freqSortedNumArray.size() - 1; i >= 0; i--) {
            ArrayList<TreeNode> lastNumNodeArray = headerList[i];
            int counter = paths.size();
            for (TreeNode curNode : lastNumNodeArray) {
                ArrayList<Integer> onePath = new ArrayList<>();
                onePath.add(curNode.getName());
                while (curNode.getParentNode() != null) {
                    TreeNode parentNode = curNode.getParentNode();
                    onePath.add(parentNode.getName());
                    curNode = curNode.getParentNode();
                    //System.out.println(onePath);
                }
                paths.add(onePath);
            }

            System.out.println("num: " + lastNumNodeArray.get(0).getName() + ", paths num: " + (paths.size() - counter) + ", total: " + paths.size());

        }*/

       /* for(ArrayList<Integer> itr: paths){
            System.out.println(itr);
        }*/


    private static void initProgram() {
        processorNum = Runtime.getRuntime().availableProcessors();
    }

}
