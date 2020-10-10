import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Wallet {
	public PrivateKey privateKey;
	public PublicKey publicKey;
	//only utxos from this wallet
	public HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
	
	public Wallet() {
		generateKeyPair();
	}
	
	public void generateKeyPair() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
			SecureRandom random = SecureRandom.getInstance("Sha1Prng");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			// Initilaz and Generate KeyPair
			// Elliptic Curve KeyPair
			keyGen.initialize(ecSpec, random);
			KeyPair keyPair = keyGen.generateKeyPair();
			// Set public and private keys from KeyPair
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
			
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	//returns balance and stores UXTO
	public float getBalance() {
		float total = 0;
		for(Map.Entry<String, TransactionOutput> item: MarkChain.UTXOs.entrySet()) {
			TransactionOutput UTXO = item.getValue();
			if(UTXO.isMine(publicKey)) {
				UTXOs.put(UTXO.id,UTXO);
				total += UTXO.value;
			}
		}
		return total;
	}
	
	//generates and returns new transactions from this wallet
	public Transaction sendFunds(PublicKey _recipient,float value) {
		if(getBalance() < value) {
			System.out.println("#Not Enough funds in wallet to send transaction");
			return null;
		}
		//create array list for inputs
		ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
		
		float total = 0;
		for(Map.Entry<String, TransactionOutput> item: UTXOs.entrySet()) {
			TransactionOutput UTXO = item.getValue();
			total += UTXO.value;
			inputs.add(new TransactionInput(UTXO.id));
			if(total > value) break;			
		}
		Transaction newTransaction = new Transaction(publicKey, _recipient , value, inputs);
		newTransaction.generateSignature(privateKey);
		for(TransactionInput input : inputs) {
			UTXOs.remove(input.transactionOutputId);
		}
		return newTransaction;
	}
	
	
}
