import java.util.HashMap;

public class TreeNode {
    private Integer name;
    private Integer count;
    private TreeNode parentNode;
    private HashMap<Integer, TreeNode> childNodesMap;

    public TreeNode(Integer name, Integer count) {
        this.name = name;
        this.count = count;
        childNodesMap = new HashMap<>();
    }

    public Integer getName() {
        return this.name;
    }

    public void setName(Integer name) {
        this.name = name;
    }

    public Integer getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public TreeNode getParentNode() {
        return parentNode;
    }

    public void setParentNode(TreeNode node) {
        this.parentNode = node;
    }

    public HashMap<Integer, TreeNode> getChildNodesMap() {
        return this.childNodesMap;
    }

    public void addChildNode(TreeNode node) {
        Integer name = node.getName();
        this.childNodesMap.put(name, node);
    }

    public int compareTo(TreeNode o) {
        return o.getCount().compareTo(this.getCount());
    }

//    protected Object clone() throws CloneNotSupportedException {
//        TreeNode node = (TreeNode) super.clone();
//        if(this.getParentNode() != null){
//            node.setParentNode((TreeNode) this.getParentNode().clone());
//        }
//
//        if(this.getChildNodesList() != null){
//            node.setChildNodesList((ArrayList<TreeNode>) this.getChildNodesList().clone());
//        }
//        return node;
//    }


}
