
public class TransactionInput {
	public String transactionOutputId; // refernec to transaction outputs id
	public TransactionOutput UTXO; // contains the unspent transaction output
	
	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}
	
}
