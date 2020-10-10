import java.security.Security;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

import com.google.gson.GsonBuilder;

public class MarkChain {
	
	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();//lists unspent transactions
	
	public static int level = 5;
	public static float minimumTransaction = 0.1f;
	public static Wallet walletA;
	public static Wallet walletB;

	public static void main(String[] args) {
		//bouncey castle for security
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		//create wallet
		walletA = new Wallet();
		walletB = new Wallet();
		//test keys
		System.out.println("Private and Public Keys: ");
		System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
		System.out.println(StringUtil.getStringFromKey(walletA.publicKey));
		//test transaction
		Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
		transaction.generateSignature(walletA.privateKey);
		//verify sign works with public key
		System.out.println("is signature verified: ");
		System.out.println(transaction.verifiySignature());
		
	}
	
	public static Boolean isChainValid() {
		Block currentBlock;
		Block previousBlock;
		String hashTarget = new String(new char[level]).replace('\0', '0');
		
		//loop through the blockchain and check the hashes
		for(int i=1; i<blockchain.size();i++) {
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			
			//comparing the registered hash and the calculated hash
			if(!currentBlock.hash.contentEquals(currentBlock.computeHash()) ) {
				System.out.println("Current hashes is no equal");
				return false;
			}
			
			//compare previous hash and the registered previous hash
			if(!previousBlock.hash.contentEquals(currentBlock.previousHash) ) {
				System.out.println("Previous hashes are not equal");
				return false;
			}
			
			//check if the hash is solved
			if(!currentBlock.hash.substring(0, level).contentEquals(hashTarget)) {
				System.out.println("This Block hasnt been mined");
				return false;
			}
		}
		
		return true;
	}

}
