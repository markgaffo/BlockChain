import java.util.Date;
import java.util.ArrayList;

public class Block {
	public String hash;
	public String previousHash;
	public String merkleRoot;
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	private String data; // holds info
	private long timeStamp; // num milisecons
	private int nonce;
	
	//Constructor
	public Block(String previousHash) {
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		
		this.hash = computeHash(); //Done after setting values
	}
	
	// new hash based on whats in the blocks
	public String computeHash() {
		String computedhash = StringUtil.applySha256(
				previousHash +
				Long.toString(timeStamp)  +
				Integer.toString(nonce) +
				merkleRoot
				);
		return computedhash;
	}
	
	public void mineBlock(int level) {
		merkleRoot = StringUtil.getMerkleRoot(transactions);
		String target = StringUtil.getLevelString(level);//creates a string with a level *"0"
		while(!hash.substring(0, level).contentEquals(target)) {
			nonce ++;
			hash = computeHash();
		}
		System.out.println("Block mined! :" + hash);
	}
	
	public boolean addTransaction(Transaction transaction) {
		if(transaction == null) return false;
		if((previousHash != "0")) {
			if((transaction.processTransaction()!= true)) {
				System.out.println("Trnasaction failed process, it has been discarded");
				return false;
			}
		}
		transactions.add(transaction);
		System.out.println("Trnasaction passed process, added to block");
		return true;
		
	}
}
