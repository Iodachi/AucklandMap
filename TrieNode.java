
import java.util.HashMap;
import java.util.List;

public class TrieNode {
	private char c;
    public HashMap<Character, TrieNode> children = new HashMap<Character, TrieNode>();
    public boolean isLeaf;
    
    public TrieNode() {
    	
    }
    
    public TrieNode(char c){
        this.c = c;
    }
}