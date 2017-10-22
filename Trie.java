import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trie {
    private TrieNode root;
    private List<String> outputs;
 
    public Trie() {
        root = new TrieNode();
        outputs = new ArrayList<String>();
    }
 
    // Inserts a word into the trie.
    public void insert(String word) {
        HashMap<Character, TrieNode> children = root.children;
 
        for(int i=0; i<word.length(); i++){
            char c = word.charAt(i);
 
            TrieNode t;
            if(children.containsKey(c)){ //if the character is inside the tree, move to next node
                    t = children.get(c);
            }else{
                t = new TrieNode(c); //if not in the tree, add to it
                children.put(c, t);
            }
 
            children = t.children;
 
            //set leaf node
            if(i==word.length()-1)
                t.isLeaf = true;    
        }
    }
 
    public boolean startsWith(String prefix) {
        if(searchNode(prefix) == null)  //if we can find the node in tree
            return false;
        else
            return true;
    }
 
    public TrieNode searchNode(String str){
    	outputs = new ArrayList<String>();
        Map<Character, TrieNode> children = root.children; 
        TrieNode t = null;
        for(int i=0; i<str.length(); i++){
            char c = str.charAt(i);
            if(children.containsKey(c)){
                t = children.get(c);
                children = t.children;
            }else{
                return null;
            }
        }
 
        return t;
    }
    
    public void getSubNodes(TrieNode current, String output, String character){
    	if(current == null) return;
    	output+=character; //add a character to output
    	if(current.isLeaf) { //if already reach the leaf of the tree, no children left, able to add resulting whole word in output
   			if(outputs.size() < 10) outputs.add(output); //in order to output 10 values at most
   		}
   		for(char c:current.children.keySet()){
   			String newChar = Character.toString(c);
   			getSubNodes(current.children.get(c), output, newChar);
    	}
   		//fixed character output problem
   	}
    
    public List<String> getOutput (String prefix){
    	TrieNode current = searchNode(prefix);
    	getSubNodes(current, prefix, "");
    	return this.outputs;
    }
}
    