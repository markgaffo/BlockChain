import java.security.PublicKey;

public class TransactionOutput {
	public String id;
	public PublicKey reciepient; // new owner of coins
	public float value; // amount coins they own
	public String parentTransactionId;// id of transaction this output was made in
	
	// con
	public TransactionOutput(PublicKey reciepient, float value, String parentTransactionId) {
		this.reciepient = reciepient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		this.id = StringUtil.applySha256(StringUtil.getStringFromKey(reciepient)+Float.toString(value)+parentTransactionId);	
	}
	
	//check if coin belongs to you
	public boolean isMine(PublicKey publicKey) {
		return (publicKey == reciepient);
	}
	
}
