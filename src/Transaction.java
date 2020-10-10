import java.security.*;
import java.util.ArrayList;

public class Transaction {
		
	public String transactionId;
	public PublicKey sender;
	public PublicKey reciepient;
	public float value;
	public byte[] signature;
	
	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
	
	private static int sequence = 0;
	
	//con
	public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
		this.sender = from;
		this.reciepient = to;
		this.value = value;
		this.inputs = inputs;
		
	}
	
	//calculates trans hash
	private String calulateHash() {
		sequence++;
		return StringUtil.applySha256(
				StringUtil.getStringFromKey(sender) +
				StringUtil.getStringFromKey(reciepient) +
				Float.toString(value) + sequence
				);
	}
	
	//signs all data
	public void generateSignature(PrivateKey privateKey) {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value);
		signature = StringUtil.applyECDSASig(privateKey,data);
	}
	//verifies data we signed
	public boolean verifiySignature() {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value);
		return StringUtil.verifyECDSASig(sender, data, signature);
	}
	
	public boolean processTransaction() {
		
		if(verifiySignature() == false) {
			System.out.println("#Transaction Signature failed verification");
			return false;
		}
		
		//transaction inputs to make sure they are not spent
		for(TransactionInput i : inputs) {
			i.UTXO = MarkChain.UTXOs.get(i.transactionOutputId);
		}
		//check if valid
		if(getInputsValue() < MarkChain.minimumTransaction) {
			System.out.println("#Transaction Input small: " + getInputsValue());
			return false;
		}
		//gen transaction outputs
		float leftOver = getInputsValue() - value;
		transactionId = calulateHash();
		outputs.add(new TransactionOutput(this.reciepient, value,transactionId));
		outputs.add(new TransactionOutput(this.sender, leftOver,transactionId));
		
		//add outputs to unspent
		for(TransactionOutput o : outputs) {
			MarkChain.UTXOs.put(o.id, o);
		}
		
		//remove transaction inputs from utxo list as spent
		for(TransactionInput i : inputs) {
			if(i.UTXO == null) continue;
			MarkChain.UTXOs.remove(i.UTXO.id);
		}
		
		return true;
	}
	//sum of inputs
	public float getInputsValue() {
		float total = 0;
		for(TransactionInput i : inputs) {
			if(i.UTXO == null) continue;
			total += i.UTXO.value;
		}
		return total;
	}
	// sum of outputs
	public float getOutputsValue() {
		float total = 0;
		for(TransactionOutput o : outputs) {
			total += o.value;
		}
		return total;
	}
}
