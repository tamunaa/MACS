extern crate crypto;
use crypto::ripemd160::Ripemd160;
use crypto::digest::Digest;
use secp256k1::{PublicKey, SecretKey, Secp256k1, Message};
use secp256k1::All;
use serde_json::Value;
use sha256;

const PRIVATE_KEY: &str = "";
const FEE_AMOUNT: u64 = 1000;

#[derive(Clone)]
#[derive(Default)]
struct RawTransaction {
    tx_version: Vec<u8>,
    input_count: Vec<u8>,
    input_list: Vec<TransactionIn>, 
    output_count: Vec<u8>,
    output_list: Vec<TransactionOut>, 
    tx_locktime: Vec<u8>
}

impl RawTransaction {
    fn new() -> Self {
        Self::default()
    }
}

#[derive(Clone)]
struct TransactionIn {
    transaction_hash: Vec<u8>,
    output_index: Vec<u8>,
    sig_script_size: Vec<u8>,
    sig_script: Vec<u8>,
    sequence: Vec<u8>,
}

#[derive(Clone)]
struct TransactionOut {
    amount: Vec<u8>,
    script_pubkey_size: Vec<u8>,
    script_pubkey: Vec<u8>,
}


fn create_script_pubkey(public_key: &str) -> Vec<u8> {
    let mut script_pubkey = vec![0x76, 0xa9, 0x14];
    script_pubkey.extend(generate_public_key_hash(public_key));
    script_pubkey.extend(vec![0x88, 0xac]);
    script_pubkey
}

fn create_transaction_input(utxo: &Value) -> TransactionIn {
    TransactionIn { 
        transaction_hash: hex::decode(reverse_order(*&utxo["txid"].as_str().unwrap())).unwrap().to_vec(), 
        output_index: (utxo["vout"].as_u64().unwrap() as u32).to_le_bytes().to_vec(),
        sig_script_size: vec![0x00], 
        sig_script: vec![], 
        sequence: vec![0xff, 0xff, 0xff, 0xff] 
    }
}

fn create_transaction_output(amount: u64, script_pubkey: Vec<u8>) -> TransactionOut {
    TransactionOut { 
        amount: amount.to_le_bytes().to_vec(), 
        script_pubkey_size: vec![script_pubkey.len() as u8], 
        script_pubkey: script_pubkey
    }
}

fn create_transaction_inputs(utxos_to_spend: &[Value]) -> Vec<TransactionIn> {
    utxos_to_spend.iter().map(|utxo| create_transaction_input(utxo)).collect()
}

fn create_transaction_outputs(transaction_amount: u64, 
    recipient_script_pub_key: Vec<u8>, 
    leftover_change: u64, 
    sender_public_key: &PublicKey) -> Vec<TransactionOut> {

    let mut transaction_outputs = vec![TransactionOut {
        amount: (transaction_amount - FEE_AMOUNT).to_le_bytes().to_vec(), 
        script_pubkey_size: vec![recipient_script_pub_key.len() as u8], 
        script_pubkey: recipient_script_pub_key
    }];

    if leftover_change > 0 {
        let sender_script_pubkey = create_script_pubkey(&sender_public_key.to_string());
        let change_output = create_transaction_output(leftover_change, sender_script_pubkey);
        transaction_outputs.push(change_output);
    }

    transaction_outputs
}

fn calculate_spendable_utxos(unspent_transactions: &Value, target_amount: u64) -> (u64, Vec<Value>) {
    let mut accumulated_amount = 0_u64;
    let mut selected_utxos: Vec<Value> = vec![];

    for utxo in unspent_transactions.as_array().unwrap() {
        if accumulated_amount >= target_amount {
            break;
        }

        let utxo_amount = (utxo["amount"].as_f64().unwrap() * 1e8) as u64;
        accumulated_amount += utxo_amount;
        selected_utxos.push(utxo.clone());
    }

    (accumulated_amount, selected_utxos)
}

fn prepare_transaction_to_sign(trans: &RawTransaction) -> Vec<u8> {
    let mut transaction_to_sign = prepare_transaction_for_signing(trans.clone());
    transaction_to_sign.extend(vec![0x01, 0x00, 0x00, 0x00]); 
    transaction_to_sign
}

fn create_hashed_transaction(transaction_to_sign: Vec<u8>) -> String {
    let hashed_transaction = sha256::digest(hex::decode(sha256::digest(transaction_to_sign)).unwrap()); // double sha256
    hashed_transaction
}

fn create_signature(transaction_hash: String, private_key: &SecretKey) -> String {
    let secp_instance = Secp256k1::new();
    let transaction_message = Message::from_digest_slice(hex::decode(transaction_hash).unwrap().as_slice()).unwrap();
    let transaction_signature = secp_instance.sign_ecdsa(&transaction_message, private_key).to_string();
    transaction_signature
}

fn create_script_signature(signature: String, public_key: &PublicKey) -> Vec<u8> {
    let mut script_signature: Vec<u8> = vec![];

    script_signature.extend(vec![((signature.len()/2) as u8)+1]);
    script_signature.extend(hex::decode(signature).unwrap().to_vec());
    script_signature.extend(vec![0x01]);
    script_signature.extend(vec![0x21]);
    script_signature.extend(hex::decode(public_key.to_string()).unwrap().to_vec());

    script_signature
}

fn initialize_transaction(tx_outs_to_spend: &[Value], remaining_balance: u64) -> Result<RawTransaction, Box<dyn std::error::Error>> {
    let mut transaction = RawTransaction::new();
    transaction.tx_version = vec![0x01, 0x00, 0x00, 0x00];
    transaction.input_count = vec![tx_outs_to_spend.len() as u8];
    transaction.output_count = if remaining_balance > 0 { vec![0x02] } else { vec![0x01] };
    transaction.tx_locktime = vec![0x00, 0x00, 0x00, 0x00];
    Ok(transaction)
}

fn create_unsigned_transaction(private_key: SecretKey, public_key: PublicKey, receiver: &str, unspent_tx_outs: &Value, transfer_amount: u64) -> Result<String, Box<dyn std::error::Error>> {
    let (available_balance, tx_outs_to_spend) = calculate_spendable_utxos(unspent_tx_outs, transfer_amount);
    let remaining_balance = available_balance - transfer_amount;

    let mut transaction = initialize_transaction(&tx_outs_to_spend, remaining_balance)?;
    transaction.input_list = create_transaction_inputs(&tx_outs_to_spend);

    let mut output_script = vec![0x76, 0xa9, 0x14];
    let receiver_pubkey_hash = extract_pubkey_hash(receiver)?;

    output_script.extend(hex::decode(receiver_pubkey_hash)?.to_vec());
    output_script.extend(vec![0x88, 0xac]);

    transaction.output_list = create_transaction_outputs(transfer_amount, output_script, remaining_balance, &public_key);

    let mut signatures: Vec<Vec<u8>> = vec![];

    for (i, tx_out) in tx_outs_to_spend.iter().enumerate() {
        let output_script = hex::decode(tx_out["scriptPubKey"].as_str().unwrap())?;
        transaction.input_list[i].sig_script_size = vec![output_script.len() as u8];
        transaction.input_list[i].sig_script = output_script;
    
        let transaction_to_sign = prepare_transaction_to_sign(&transaction);
        let hashed_transaction = create_hashed_transaction(transaction_to_sign);
    
        let signature = create_signature(hashed_transaction, &private_key);
        let script_signature : Vec<u8> = create_script_signature(signature, &public_key);
    
        signatures.push(script_signature);
    
        transaction.input_list[i].sig_script_size = vec![0x00];
        transaction.input_list[i].sig_script = vec![];
    }
    
    for (index, script_sig) in signatures.iter().enumerate() {
        transaction.input_list[index].sig_script_size = vec![script_sig.len() as u8];
        transaction.input_list[index].sig_script = script_sig.clone();
    }
   
    let raw_transaction = hex::encode(prepare_transaction_for_signing(transaction)).to_string();
    
    Ok(raw_transaction)
}

fn extend_with_input(data: &mut Vec<u8>, transaction_input: &TransactionIn) {
    for field in &[
        &transaction_input.transaction_hash, 
        &transaction_input.output_index, 
        &transaction_input.sig_script_size,
        &transaction_input.sig_script, 
        &transaction_input.sequence] {

        data.extend_from_slice(field);
    }
}

fn extend_with_output(data: &mut Vec<u8>, transaction_output: &TransactionOut) {
    for field in &[&transaction_output.amount, &transaction_output.script_pubkey_size, &transaction_output.script_pubkey] {
        data.extend_from_slice(field);
    }
}


fn prepare_transaction_for_signing(transaction: RawTransaction) -> Vec<u8> {
    let mut prepared_data: Vec<u8> = vec![];

    prepared_data.extend_from_slice(&transaction.tx_version);
    prepared_data.extend_from_slice(&transaction.input_count);


    for input in &transaction.input_list {
        extend_with_input(&mut prepared_data, input);
    }
    prepared_data.extend(transaction.output_count.clone());

    for output in &transaction.output_list {
        extend_with_output(&mut prepared_data, output);
    }

    prepared_data.extend_from_slice(&transaction.tx_locktime);

    prepared_data.clone()
}


fn create_secret_key() -> SecretKey {
    let secret_key = SecretKey::from_slice(hex::decode(PRIVATE_KEY).unwrap().as_slice())
        .expect("Invalid private key provided");
    println!("New Private Key: {:?}", secret_key.display_secret().to_string());
    secret_key
}

fn create_public_key(secp: &Secp256k1<All>, secret_key: &SecretKey) -> PublicKey {
    let public_key = PublicKey::from_secret_key(secp, secret_key);
    println!("New Public Key: {:?}", public_key.to_string());
    public_key
}

fn generate_keypair() -> (SecretKey, PublicKey) {
    let secp_instance = Secp256k1::new();
    let private_key = create_secret_key();
    let public_key = create_public_key(&secp_instance, &private_key);
    (private_key, public_key)
}

fn generate_address(public_key: PublicKey) -> String {
    let public_key_hash = generate_public_key_hash(&public_key.to_string());
    let mut bitcoin_address: Vec<u8> = std::iter::once(0x6f).chain(public_key_hash).collect();

    let first_sha256_hash = sha256::digest(&bitcoin_address);
    let second_sha256_hash = sha256::digest(&first_sha256_hash);
    let decoded_sha256_hash = hex::decode(&second_sha256_hash).unwrap();

    bitcoin_address.extend(&decoded_sha256_hash[..4]);

    bs58::encode(bitcoin_address).into_string()
}

fn create_sha256_digest(public_key: &str) -> Vec<u8> {
    let sha2 = sha256::digest(hex::decode(public_key).unwrap());
    hex::decode(sha2).unwrap()
}

fn create_ripemd160_hash(sha256_digest: &[u8]) -> Vec<u8> {
    let mut ripemd = Ripemd160::new();
    let mut hash = vec![0; ripemd.output_bytes()];
    ripemd.input(sha256_digest);
    ripemd.result(&mut hash);
    hash
}

fn generate_public_key_hash(public_key: &str) -> Vec<u8> {
    let sha256_digest = create_sha256_digest(public_key);
    create_ripemd160_hash(&sha256_digest)
}

fn reverse_order(hex: &str) -> String {
    hex::encode(&hex::decode(hex).unwrap().into_iter().rev().collect::<Vec<_>>())
}

fn extract_pubkey_hash(address: &str) -> Result<String, Box<dyn std::error::Error>> {
    let bytes = bs58::decode(address).into_vec()?;
    let pubkey_hash = &bytes[1..(bytes.len()-4)];
    Ok(hex::encode(pubkey_hash))
}

fn decode_address(address: &str) -> Option<Vec<u8>> {
    bs58::decode(address).into_vec().ok()
}

fn calculate_dsha(version_and_pubkeyhash: &[u8]) -> Vec<u8> {
    let sha256_digest = sha256::digest(version_and_pubkeyhash);
    let sha256_digest_bytes = hex::decode(sha256_digest).unwrap();
    hex::decode(sha256::digest(&sha256_digest_bytes)).unwrap()
}

fn is_bitcoin_address_valid(address: &str) -> bool {
    if !address.starts_with("m") && !address.starts_with("n") {
        return false;
    }

    let decoded = match decode_address(address) {
        Some(value) => value,
        None => return false,
    };

    let version_and_pubkeyhash = &decoded[..(decoded.len()-4)];
    let checksum = &decoded[(decoded.len()-4)..];

    let dsha = calculate_dsha(version_and_pubkeyhash);
    let first_four_bytes = &dsha[..4];

    first_four_bytes == checksum
}

async fn fetch_unspent_transactions(address: &str) 
-> Result<(Value, u64), Box<dyn std::error::Error>> {
    
    let user_pass = "rezga";
    let encoded = base64::encode(user_pass);
    let auth_token = format!("Basic {}", encoded);

    let body = format!(r#"{{"jsonrpc":"1.0","method":"listunspent","params":[1,999999,["{address}"]]}}"#);

    let client = reqwest::Client::new();
    let res = client
        .post("http://127.0.0.1:8080")
        .body(body)
        .header("content-type", "application/json")
        .header("Authorization", auth_token).send().await?;

    let response_text = res.text().await?;
    let parsed_response: Value = serde_json::from_str(&response_text)?;

    let utxos = parsed_response.get("result").ok_or("Missing 'result' field in response")?;

    let mut total_amount = 0_u64;

    for utxo in utxos.as_array().ok_or("Expected 'result' field to be an array")? {
        let amount_satoshis = (utxo.get("amount").ok_or("Missing 'amount' field in UTXO")?.as_f64().ok_or("Expected 'amount' field to be a float")? * 1e8) as u64;
        total_amount += amount_satoshis;
    }

    Ok((utxos.clone(), total_amount))
}

async fn send_transaction_via_rpc(transaction: &str) 
-> Result<String, Box<dyn std::error::Error>> {

    let user_pass = "rezga";
    let encoded = base64::encode(user_pass);
    let auth_token = format!("Basic {}", encoded);

    let body = format!(r#"{{"jsonrpc":"1.0","method":"sendrawtransaction","params":["{}"]}}"#, transaction);

    let client = reqwest::Client::new();

    let response = client
        .post("http://127.0.0.1:8080")
        .body(body)
        .header("content-type", "application/json")
        .header("Authorization", auth_token.clone())
        .send()
        .await?;

    if !response.status().is_success() {
        return Err(format!("Failed to execute RPC method 'sendrawtransaction'. HTTP status: {}", response.status()).into());
    }

    let response_text = response.text().await?;
    let parsed_response: serde_json::Value = serde_json::from_str(&response_text)?;

    let txid = parsed_response["result"].as_str().ok_or("Missing 'result' field in response")?.to_string();

    Ok(txid)
}


async fn send_bitcoin(receiver_address: &str, amount_satoshis: u64) 
-> Result<(), Box<dyn std::error::Error>> {

    if !is_bitcoin_address_valid(receiver_address) {
        return Err("Recipient address is not valid.".into());
    }

    let (secret_key, public_key) = generate_keypair();

    let my_address = generate_address(public_key);
    println!("Address: {}", my_address);

    let (utxos, total_balance) = fetch_unspent_transactions(&my_address).await?;

    println!("Balance in Satoshis: {}", total_balance);

    let total_to_be_spent = amount_satoshis + FEE_AMOUNT;

    if total_to_be_spent > total_balance {
        return Err(format!("Insufficient balance, consider a fee amount of {}.", FEE_AMOUNT).into());
    }

    let raw_transaction = create_unsigned_transaction(secret_key, public_key, receiver_address, &utxos, total_to_be_spent)?;
    let transaction_id = send_transaction_via_rpc(&raw_transaction).await?;

    println!("TXID: {}", transaction_id);

    Ok(())
}


#[tokio::main]
async fn main() {
    match send_bitcoin("", 1000).await {
        Ok(_) => println!("Transaction sent successfully."),
        Err(e) => eprintln!("Failed to send transaction: {}", e),
    }
}
