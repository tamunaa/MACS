[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-24ddc0f5d75046c5622901739e7c5dd533143b0c8e959d652212380cedb1ea36.svg)](https://classroom.github.com/a/6OHo_R7o)
# Assignment 2: Building a Bitcoin Transaction Serializer
In first few lectures we talked about building blocks of Bitcoin: proof of work, the blockchain, hashes, transaction outputs, assymetric key cryptography, 
Keys, addresses and wallet technologies, bitcoin clients, nodes and network participants. This Assignment is kind 
of a summarization of what we learnt about Bitcoin and after completing it you will have a better grasp of the
concepts we have covered so far.

The Concept of the assignment is really simple: you just have to send an amount of bitcoin from one
address to another. But of course there are few details and restrictions which we will talk about later, 
At first, lets view the starter code:

```rs
const PRIVATE_KEY: &str = "";

#[tokio::main]
async fn main() {
    send("<P2PKH testnet address>", 1000).await;
}

async fn send(receiver_address: &str, amount_satoshis: u64) {
    todo!("Just make this function work")
}

```

The one and only task in this assignment is to make `send` function work. during the assignment grading I
will put my private key in `PRIVATE_KEY` constant and try to send bitcoins from my address. the format of a constant 
variable should be a hexadecimal string, as We already know bitcoin private keys are 32 bytes long, so
our `PRIVATE_KEY` will be 64 character long hex string. You should put your private key payload in there during development, 
but don't forget to remove it before committing an assignment :D. FYI it's an awful practice to hardcode the private key in
your code like that, Don't follow my example :D. if you come up with some better practice, I will reward you with bonus points for this assignment 
(you can change the code, just don't make it unreadable).

Okay we looked through the starting code, now let's get down to business and talk about what will you need during the development process.

## Connecting To The Chain
It's obvious that in order to send transactions to the blockchain you will need to somehow connect to it's 
RPC endpoints. While other blockchains have very convenient and easily accessible public rpc endpoints, in bitcoin world it's
really hard to find one with necessary functionality. So we will do what any bitcoin developer would have done: 

### spin up our own bitcoin node!
_Warning: This might not be the most interesting and productive part of an assignment, but Deal with it!_

Process of running our bitcoin node consists of several steps:
#### Step 1: Download the necessary software
The program we want to run is called **bitcoind**, it stands for bitcoin daemon, and appears 
as subproject of **Bitcoin Core**. You can of course install the GUI version, but I recommend intalling and using
it from Terminal.
#### For Linux users (or those who use WSL):
from terminal run:
```shell
> sudo add-apt-repository ppa:luke-jr/bitcoincore
> sudo apt-get update
> sudo apt-get install bitcoind
```
after these steps, `bitcoind` should be installed, you can open new terminal window and type:
```shell
bitcoind -version
```
the output should indicate that it's `Bitcoin Core version v23.0.0` somewhere along the lines.
here's the [full instruction list](https://gist.github.com/rjmacarthy/b56497a81a6497bfabb1) if something went wrong and 
installation failed.

#### For macOS users

[brew install bitcoin](https://formulae.brew.sh/formula/bitcoin)

#### step 2: configure the daemon.
Before spinning up the node instance, we need to write a few lines in the configuration file.\
For linux the **default location** fo a file is `$HOME/.bitcoin/`\
For macOS it's `~/Library/Application Support/Bitcoin`
In The specified location according to your OS, create (if it's not already created) file named
`bitcoin.conf`:
```shell
touch bitcoin.conf
```
then open the file with text editor of your choice and type:
```conf
testnet=1
prune=4000
rpcuser=rezga
rpcpassword=rezga
rpcallowip=0.0.0.0/0
```
let's go through the config file line by line:\
`testnet=1` - this one is for specifying that your node will be connecting to the testnet network, when it is started.
all development of this assignment will be done in testnet, so that we don't need to spend real bitcoins.\
`prune=4000` - this line tells bitcoind how much disk space will be used for storing the actual blockchain.
in this example it's 4 GB, so the node will be storing the most recent blocks, which size is not exceeding 4GB.
you can increase or decrease the pruning value, but if you don't specify it, the whole blockchain will be stored on 
your hard drive. You can read further about pruning [here](https://bitcoincore.org/en/releases/0.11.0/#:~:text=Notable%20changes-,Block%20file%20pruning,-This%20release%20supports)
`rpcuser` and `rpcpassword` fields are for JSON RPC authorization, we will get to that part later. you can set any arbitrary texts here
`rpcallowip` is just to make it possible to connect to the node within localhost!
#### step 3: The hard part
Now all we need to do is run the node, with the command:
```shell
bitcoind -server
```
this should start your own bitcoin node, but not so fast! it still needs to sync with the network,
as of today bitcoin testnet has 2,533,405 blocks, so you node needs to download and validate all of them,
it will store only recent 4GB of blocks through(depending on the amount you are pruning from config).
This process will several hours, so you can relax until then :D.
The syncing process should look something like this:
![syncing.png](images/syncing.png)
notice the progress field among those lines? it shows what portion of the data is synced and validated.
you will be good to go when that number reaches `1.00000`

### Interacting with the running node:

You can interact with a node by two ways: using http requests from your code or using bitcoin-cli 
(which is also a wrapper around those http requests).
let's showcase bitcoin-cli a bit:\
The most basic request you can send to a node is fetching the block count:
```
> bitcoin-cli getblockcount
2533408
```
Or you can fetch the block by specific ID:
```
> bitcoin-cli getblock 000000000000002a4e335c39352d79524e332e020d006ea0adc82cb2525937b5
{
  "hash": "000000000000002a4e335c39352d79524e332e020d006ea0adc82cb2525937b5",
  "confirmations": 2,
  "height": 2533407,
  "version": 580149248,
  "versionHex": "22946000",
  "merkleroot": "4b54b975e4c1a66ab58c8ec5868fa9c3a4742f163a5b6859438ec2b50861008b",
  "time": 1697465454,
  "mediantime": 1697463560,
  "nonce": 1494757753,
  "bits": "196792e0",
  "difficulty": 41467095.3779718,
  "chainwork": "000000000000000000000000000000000000000000000ba47edfcdd99f7e9f7b",
  "nTx": 40,
  "previousblockhash": "000000000000001778ad75f62120264c052b73b18065da1dd681944db69a9a11",
  "nextblockhash": "0000000000000013a97bdbfd6f8d947f091a1c8f7ec1598ff2cbc20397a5d385",
  "strippedsize": 8365,
  "size": 16098,
  "weight": 41193,
  "tx": [
    "c9ee353b83c3da7a84514c0e34bc4047b5ba50354b48dc679791054827685ab6",
    "51508c7d7bb0d6855894ec97fb6b6518a4c4da8a3ea848ae80275509ae7ef816",
    "bf16e24de06afe6ca5ebb14584d926574434fc5c4113f71fbfdd8af1dc6659c0",
    "e12013468195302602bff2e0bd7cc37ebf4a2d90a1495b9b88582c70b7f01fca",
    "844259631cb3244f09b9adf263aae0c334f72c86cba10802f47c4583a2a66ce7",
    "6ab8747d32abb840a6ff9558b7d00e5ec89aca4086ed36ff2600eaf49da3aef2",
    "c9cd517124fd86a725a3cc5433b669b936bfdb78f857dd815d97725fc03e18fd",
    "8430a206c028bf4704e90d4f8082d5ed4a70fd3acf12e7f215e51e17e5c8ca45",
    "e7df2fa9896cf55f5bdf617c21bb75e12cba1ee56545a8897d10cc4c49856c36",
    "8b7ca116e7b63947584f97f98ac29e8d7a3855d5a8c999e16ccdc527b22fd992",
    "96255b599efc0a26cad138c09a9a40903c140baf0e8df8bb2f0ccf779b40b51b",
    "bfe81fa4c4d1e42c27c6f2f830774cb0df30828f610b1b963ebe13fe2ed50b23",
    "b0c15dbc61ce6ab4667ab2bfb98469247ca20152312ec2b5ac4b84ca39205240",
    "d14fe474b98380cd7524ddfc953c531cfa074416d108dc1670a3553cda497d91",
    "37557e1747d6bc892ca287c8ca41f8818f5a323b5a7ec6059f29ec5293961edb",
    "e718361b85998318f989606670a7d3d844f097ceb09aa7d07db9d57cb7f83cb4",
    "3ac2f3bb3489936ff23c249c59387e74216aa3c0f76ab584f63cf7f0444c2249",
    "5a0ff76c35d67126ac4a6893cea1e61674c4af5d8999feedb5467f22fd54fb80",
    "fddc38456ec58b8d67551f4668fe1bafb7dbde9e8bbde39d0b093703285c4692",
    "29ddd6a87c0baefe94daff19fde925900c7db79c1b86710e7af80eafaa8ff694",
    "bfa71c266d3fca9387f43ab26cf24825e49e7cd11de45af27b17f8edce8d8039",
    "ecce020ac79dbaa1585006a02d61ab31669e06d60be6a41997de99eb1664e52e",
    "8e2dc0e20988cf491074cca9db2a976e3fa6cacc89f0f951fdad7e6ea0a36edd",
    "7a55e117726705b21871b5ce3fe25416c564fd58d8b8af47bf330ad0479711b0",
    "0af68fc4f81faced4baae4b0ff520685136ec3b3dd2639e96977a2372b43eb79",
    "91c0638e087b95f810e22909937f490e69290a198ecb14c572a0f5e30d53119c",
    "33bbd946a9b9a052fa1e8edf8cd4d187cef03579fa2e51db41c7ed8b8014f123",
    "ee02dedede1609485f1c23792138a33825ee264de69a941e653b4dcc7c969652",
    "f6f153954a2c1ddae2325969dca9799e091cb998a9f48728c3f07320b4b4361c",
    "efdce71b41d82d3bb5aba4cf1a2ebf93dad9ec9c1b6996c139423bc665f024c5",
    "f8a38e53fc2475574c69880f91482d4857a353ef4c337a6d4477e8273e87b1e4",
    "09f44cfc4b882be91a699438cedb019e9eb984f3e932c8d2644a6dbb34a3a208",
    "02c05884b6ef494bae23c19e693e2a4926636fea82a88cc05c5b34ae39f85f0a",
    "589e859b23ba1bc9c23e180e78b49eb06064c587974af760cbfa87a498fa2518",
    "b8e1a81adcce8222d5f4e9d927af526751e538fcdef13571346b2160a5d7a346",
    "2fc70a270583bc6f816f02fa1362ccf69a1fa4d8a68a06cb8700de6a8fdb3c4c",
    "7af81b40330cd4d1ee8c5de85927c88cfd3c2f8c64dcd01796fa75e40bd30a6d",
    "bea87313fb2b35d05f8db2de9f787673f1d57fb41dfbfe7808be906e656e7672",
    "5b230cf3d2150f7d342f392aa9cdf55be38ef14e7c858e5f1743e848338468c5",
    "ea4795e1927146c42b374d94c5eebe4f7cddb73c704fb8d8840548555d0caecf"
  ]
}
```
These methods are part of the bitcoin RPC API interface. Almost all public blockchains have sort of RPC API and usually
they are based on http! [In the RPC documentation](https://developer.bitcoin.org/reference/rpc/) you can see all the rpc methods, but
we will be showcasing once you need for an assignment in this document.

### Funding your wallet and sending transactions from bitcoin-cli
bitcoin-cli enables users to store and manage their wallets. We will generate an example wallet and 
fund it from a common faucet to show how all the tools work:
first of all we need to tell `bitcoin-cli` to create a new wallet for us:
```shell
> bitcoin-cli createwallet my_new_wallet
```
If the command is successful you should be able to see newly created wallet after typing:
```shell
> bitcoin-cli listwallets
[
  "my_new_wallet"
]
```
`my_new_wallet` is the name for our wallet in this case, we might need to create new wallet for different purposes in the future!\
your wallet might not be loaded when the node starts up, so sometimes you will need to also run
```
> bitcoin-cli loadwallet my_new_wallet
```
to load your wallet in the node.

#### funding the wallet
We created our wallet, now we need to fund it! As you remember from the lectures, wallets are just the collections
of the public/private keypairs, connected in some deterministic way. To fund the wallet we need to get one public key (or address)
from all those keypairs:
```
> bitcoin-cli getnewaddress
tb1qt4s2ch5qgy2pmc4v59n0zdd29vrnyw6vd924qd
```
this command will pick new bitcoin address, whose public key is held by the wallet we created earlier.
Notice that this address starts with tb1 - that means it's special kind of address: It's called native SegWit address,
which uses different type of scripts than `traditional` P2PKH address (which we are going to crate from our rust code!)
But only the `SegWit` addresses can be funded from the faucet, as it's the default standard for now.

[The Faucet](https://bitcoinfaucet.uo1.net/)

you can view transaction data on [Mempool Space](https://mempool.space/), just don't forget to change
network to testnet.

I encourage you to play with the node before moving to the assignment and explore what capabilities RPC endpoints give you. for example,
you can fetch transactions with `gettransaction` rpc call and see what fields it returns. 

## An Assignment
After making sure that we can connect to the node and interact with it, we can move to the second part:
implementing a transaction serializer.

### First of all: generating a keypair
We will be needing private key to sign our serialized transaction, there are a lot of ways you can go to get your private key
On lectures, we explained what Bitcoin public/private keypairs really are and how you can generate them, 
After all, a private key is just `256 bit` sequence, which can be even generated on a piece of paper, If you still don't understand it
I highly encourage you to get back to the corresponding resources I provided in the classroom.
To summarize: 
* private key is a sequence of 256 bits
* public key can be derived from private key

Fortunately we have a rust library that serves as an abstraction layer over elliptic key cryptography, and can
wrap our keypairs in user-friendly structs. to install the library:
```shell
> cargo add secp256k1
```
you can view [the secp256k1 library](https://crates.io/crates/secp256k1) on cartes.io as well.

The first part of the assignment will be to derive P2PKH(Pay to Public Key Hash) address from a private key.
We covered how they are derived on a lectures, but here's a quick summary of Base58Check encoding:
![img.png](./images/img.png)
The payload being public key.
Of course, you will need libraries for base58 encoding as well as SHA256 digest calculation.
recommended crates:
* [bs58](https://crates.io/crates/bs58)
* [sha256](https://crates.io/crates/sha256)

you are free to use any crate or implementation for these task trough, you can even use your own implementation from
previous assignment (might even get bonus points for that deed :d).

So to summarize here are the steps for the first part of the assignment:
* Generate Private Key according to Secp256k1 algorythm
* Derive P2PKH testnet address from that private key
* Send some satoshis to that derived address using `bitcoin-cli`

example:
```shell
> bitcoin-cli send '{"muChWjVzywjKHNLMKiGiHWiZjSM5qjFgBW": 0.00001}'
```
[example transaction](https://mempool.space/testnet/tx/508f2b648b4ee4c361c8756213b63b7078ab9d318665f4eec9667267cebcda63)

Once our newly generate wallet has some UTXOs to spare, we can begin designing a transaction serializer.

### Serializing a Bitcoin transaction
The transaction payload should be serialized and passed to the chain in a hexadecimal encoding.
I would love to tell you how to fill up the transaction fields, but there is already a very comprehensive
article about this process and that is where I learnt it from, so I'll just link the article, and you can take up from there:

[Manually Creating and Signing a Bitcoin Transaction](https://medium.com/@ottosch/manually-creating-and-signing-a-bitcoin-transaction-87fbbfe46032)

Here's another useful article about transaction data: [Deconstructing Transactions](https://royalforkblog.github.io/2014/11/20/txn-demo/)

I will just list all the crates that might come in handy in creating and signing the transactions:

* `hex` - for hexadecimal encoding/decoding
* `ripemd` - for ripemd160 hash calculation
* `reqwest` and `serde-json` for working with Bitcoins JSON RPC endpoints

for transaction signing use `secp256k1` crate, and see go through its documentation, generating the transaction signature given a proper input
is really a one line code!

### RPC requests you will need to use

Besides serializing and signing the transaction, there is one last important thing. For your `send`
function to work properly, it should be able to select sufficient UTXOs to include as inputs, the user should not
be providing which outputs he or she wishes to spend, our program should select outputs automatically.

here's how transaction outputs are fetched from bitcoin-cli:

```shell
> bitcoin-cli listunspend
```
result:
```json
[
  {
    "txid": "508f2b648b4ee4c361c8756213b63b7078ab9d318665f4eec9667267cebcda63",
    "vout": 0,
    "address": "muChWjVzywjKHNLMKiGiHWiZjSM5qjFgBW",
    "label": "",
    "scriptPubKey": "76a914961def4dc143793ee502377079d8278f3ef0529088ac",
    "amount": 0.00001000,
    "confirmations": 122,
    "spendable": true,
    "solvable": false,
    "parent_descs": [
      "addr(muChWjVzywjKHNLMKiGiHWiZjSM5qjFgBW)#4uzvk0tq"
    ],
    "safe": true
  },
  {
    "txid": "d92209617677b86eab985541650d2b17acd050505b63e7806499cdc30a20e895",
    "vout": 1,
    "address": "muChWjVzywjKHNLMKiGiHWiZjSM5qjFgBW",
    "label": "",
    "scriptPubKey": "76a914961def4dc143793ee502377079d8278f3ef0529088ac",
    "amount": 0.00003500,
    "confirmations": 420,
    "spendable": true,
    "solvable": false,
    "parent_descs": [
      "addr(muChWjVzywjKHNLMKiGiHWiZjSM5qjFgBW)#4uzvk0tq"
    ],
    "safe": true
  },
  {
    "txid": "d83576f942f5ed0e895ded70ca401005f1efe800e9032da932e85fcaee694e2a",
    "vout": 0,
    "address": "muChWjVzywjKHNLMKiGiHWiZjSM5qjFgBW",
    "label": "",
    "scriptPubKey": "76a914961def4dc143793ee502377079d8278f3ef0529088ac",
    "amount": 0.00005000,
    "confirmations": 995,
    "spendable": true,
    "solvable": false,
    "parent_descs": [
      "addr(muChWjVzywjKHNLMKiGiHWiZjSM5qjFgBW)#4uzvk0tq"
    ],
    "safe": true
  }
]
```
As you can see, address `muChWjVzywjKHNLMKiGiHWiZjSM5qjFgBW` has 3 unspent outputs here: containing
1000,3500 and 5000 Satoshis. (remember 1 BTC = 1e8 Satoshis).
Before serializing the transaction you have to fetch this information from the blockchain and include
number of outputs that's sufficient for the amount we need to send.\
For example, if you wanted to send 500 satoshis, you just need to select first UTXO (containing 1000 sats) as input,
But if you wanted to send 1000 Sats, only first input will not be sufficient, because we also need to account for the fees.
You can set default amount on fees on 400 or 500 sats, that should be enough to not cause transaction to be stuck in a mempool

But `list unspent` method lists all the outputs contained in a wallet, you need to somehow filter those outputs
to only include those whcih are owned by the address you are signing the transaction with.

An example request in rust fetching unspent outputs for an address:

```rs
let user_pass = b"rezga:rezga";
let encoded: String = general_purpose::STANDARD.encode(user_pass);
let token = format!("Basic {encoded}");

let client = reqwest::Client::new();
let res = client
    .post("http://127.0.0.1:18332")
    .body(r#"{"jsonrpc":"1.0","method":"listunspent","params":[2,999999,["muChWjVzywjKHNLMKiGiHWiZjSM5qjFgBW"]]}"#)
    .header("content-type", "application/json")
    .header("Authorization", token).send().await.unwrap();
    
let text: String = res.text().await.unwrap();
```

As a result variable `text` will contain the body json as a string. First unusual thing that comes in mind
looking at this code is the first line: `let user_pass = b"rezga:rezga";`; It's called [Basic Access Authorization Scheme](https://en.wikipedia.org/wiki/Basic_access_authentication),
`rezga:rezga` in this case is the username and password we set in the config of our bitcoin node, it should contain
username and password set by you when you was configuring the node. `b""` notation in rust means to translate a string into array of bytes.
then we are encoding this byte array in Base64 format and append it to the string `Basic `. An example authorization header should look something like this:
`Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==`
or if you are using json:
```json
{
  "Authorization": "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ=="
}
```
Without the authorization token node can not be accesses.
The request body contains json data indicating which RPC call are we trying to invoke:
```json
{
  "jsonrpc":"1.0",
  "method":"listunspent",
  "params":[2, 999999, ["muChWjVzywjKHNLMKiGiHWiZjSM5qjFgBW"]]
}
```
the `jsonrpc` field indicates version number of a rpc, and it should always be 1.0
`method` in this case will be `listunspent`
and the 3 parameters are minimum and maximum block confirmations for an UTXO, and an address
by which we are filtering the UTXOs.
The response format is the same as for `bitcoin-cli listundpent` request specified above^.
You can read further documentation for listunspent method [here](https://developer.bitcoin.org/reference/rpc/listunspent.html)

#### important!
By default, your list unspent method only lists the unspent outputs belonging to the wallet
we created for our node. We need to make our node also track our externally created address.
For this we first need to unload already created wallets, by calling `unloadwallet` method:
```shell
> bitcoin-cli unloadwallet rezga
```
after no more wallets are loaded, we can create new one for tracking our new address:
```
> bitcoin-cli createwallet wallet_no_pk true
```
parameter `true` here indicates that this new wallet has no private keys, and it will only be used for tracking and aggregated
data on a blockchain, not signing transactions.

now we need to add our address descriptor to newly created wallet:

```shell
> bitcoin-cli importdescriptors '[{ "desc": "addr(muChWjVzywjKHNLMKiGiHWiZjSM5qjFgBW)#4uzvk0tq", "timestamp":1692004057 }]'
```
notice that `desc` parameter doesn't just contain the address we want to insert,
but also the `#4uzvk0tq` checksum, the checksum is required to protect us from mechanical errors.
You can get your checksum by calling:
```
bitcoin-cli getdescriptorinfo "addr(muChWjVzywjKHNLMKiGiHWiZjSM5qjFgBW)"
```
Put your generated address in `addr()` field.

### Sending the transaction data
For sending a raw transaction you can use `sendrawtransaction` method. For additional information how to
use this call consult the [Docs](https://developer.bitcoin.org/reference/rpc/sendrawtransaction.html)


## Finishing Up
This Document covers most of the parts needed to complete the assignment, I left some details unexplained in order
to give you more freedom to explore Bitcoin details more Extensively. I will add more explanations to the document if some
topics comes out to be hard to understend for the majority of the class.

### Additional Considerations For The Assignment
* After sending the transaction it's TXID should be Printed in the program output
* Verify a sufficient balance before generating and sending a transaction, If I request to send
more satoshis than the sum of my UTXOs combined, print the appropriate error message.
* Verify The correctness of the address I am requesting to send funds to:
  * Verify that it is indeed a P2PKH address
  * Verify that it's Testnet Bitcoin address
  * Verify that checksum matches the payload as by Base58CHeck standard
* Please Try to make the code as much readable as possible, as I will manually grade an assignment if
The send function fails.

### Bonus Points
You will receive about 50% bonus for this assignment if you also implement `SegWit` type of addresses
and transactions. You can read further on segwit update on [wikipedia](https://en.wikipedia.org/wiki/SegWit).



