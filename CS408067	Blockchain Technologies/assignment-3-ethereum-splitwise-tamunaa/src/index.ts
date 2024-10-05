import {Splitwise} from "./Splitwise";
import {Contract, JsonRpcProvider, Wallet} from "ethers";


async function main() {

    const private_key = "0x31496f7609f3c3792185627c7ea4f9242be11357a2350f9fd8424db04a036e3b";
    const wallet_address = "0x44942B099cEff5ba9d26038a0f1AB491D54378c6";
    const contract_address = "0x2c1a5f9d13d36356a76b59698c48f2049833951a";

    // const contract_address = "0x5cd86d27093f6264f8c2b88cbbe491a007e5fa6d";
    // const contract_address = "0xe8358310001845858a1Ab0ed2Effb3949167EaAb";
    const abi = [
        {
            "inputs": [],
            "stateMutability": "payable",
            "type": "constructor"
        },
        {
            "anonymous": false,
            "inputs": [
                {
                    "indexed": false,
                    "internalType": "uint256",
                    "name": "requestId",
                    "type": "uint256"
                },
                {
                    "indexed": false,
                    "internalType": "address",
                    "name": "requester",
                    "type": "address"
                },
                {
                    "indexed": false,
                    "internalType": "address",
                    "name": "debtor",
                    "type": "address"
                },
                {
                    "indexed": false,
                    "internalType": "uint256",
                    "name": "amount",
                    "type": "uint256"
                }
            ],
            "name": "MoneyRequestAdded",
            "type": "event"
        },
        {
            "anonymous": false,
            "inputs": [
                {
                    "indexed": false,
                    "internalType": "uint256",
                    "name": "requestId",
                    "type": "uint256"
                },
                {
                    "indexed": false,
                    "internalType": "address",
                    "name": "payer",
                    "type": "address"
                },
                {
                    "indexed": false,
                    "internalType": "address",
                    "name": "debtor",
                    "type": "address"
                },
                {
                    "indexed": false,
                    "internalType": "uint256",
                    "name": "amount",
                    "type": "uint256"
                }
            ],
            "name": "MoneyRequestPaid",
            "type": "event"
        },
        {
            "anonymous": false,
            "inputs": [
                {
                    "indexed": false,
                    "internalType": "uint256",
                    "name": "requestId",
                    "type": "uint256"
                },
                {
                    "indexed": false,
                    "internalType": "address",
                    "name": "requester",
                    "type": "address"
                },
                {
                    "indexed": false,
                    "internalType": "address",
                    "name": "debtor",
                    "type": "address"
                },
                {
                    "indexed": false,
                    "internalType": "uint256",
                    "name": "amount",
                    "type": "uint256"
                }
            ],
            "name": "MoneyRequestRejected",
            "type": "event"
        },
        {
            "inputs": [],
            "name": "cancelMoneyRequest",
            "outputs": [],
            "stateMutability": "nonpayable",
            "type": "function"
        },
        {
            "inputs": [
                {
                    "internalType": "address",
                    "name": "",
                    "type": "address"
                },
                {
                    "internalType": "address",
                    "name": "",
                    "type": "address"
                }
            ],
            "name": "debts",
            "outputs": [
                {
                    "internalType": "uint256",
                    "name": "",
                    "type": "uint256"
                }
            ],
            "stateMutability": "view",
            "type": "function"
        },
        {
            "inputs": [],
            "name": "getAllCreditors",
            "outputs": [
                {
                    "internalType": "address[]",
                    "name": "",
                    "type": "address[]"
                }
            ],
            "stateMutability": "view",
            "type": "function"
        },
        {
            "inputs": [],
            "name": "getAllCreditors2",
            "outputs": [
                {
                    "internalType": "address[]",
                    "name": "",
                    "type": "address[]"
                }
            ],
            "stateMutability": "view",
            "type": "function"
        },
        {
            "inputs": [],
            "name": "getAllDebtors",
            "outputs": [
                {
                    "internalType": "address[]",
                    "name": "",
                    "type": "address[]"
                }
            ],
            "stateMutability": "view",
            "type": "function"
        },
        {
            "inputs": [
                {
                    "internalType": "address",
                    "name": "debtor",
                    "type": "address"
                }
            ],
            "name": "getAmountOwedTo",
            "outputs": [
                {
                    "internalType": "uint256",
                    "name": "",
                    "type": "uint256"
                }
            ],
            "stateMutability": "view",
            "type": "function"
        },
        {
            "inputs": [
                {
                    "internalType": "address",
                    "name": "requestor",
                    "type": "address"
                }
            ],
            "name": "getAmountRequestedFrom",
            "outputs": [
                {
                    "internalType": "uint256",
                    "name": "",
                    "type": "uint256"
                }
            ],
            "stateMutability": "view",
            "type": "function"
        },
        {
            "inputs": [
                {
                    "internalType": "address",
                    "name": "node",
                    "type": "address"
                }
            ],
            "name": "getNeighbors",
            "outputs": [
                {
                    "internalType": "address[]",
                    "name": "",
                    "type": "address[]"
                }
            ],
            "stateMutability": "view",
            "type": "function"
        },
        {
            "inputs": [],
            "name": "getNodes",
            "outputs": [
                {
                    "internalType": "address[]",
                    "name": "",
                    "type": "address[]"
                }
            ],
            "stateMutability": "view",
            "type": "function"
        },
        {
            "inputs": [],
            "name": "getParticipatingAddresses",
            "outputs": [
                {
                    "internalType": "address[]",
                    "name": "",
                    "type": "address[]"
                }
            ],
            "stateMutability": "view",
            "type": "function"
        },
        {
            "inputs": [],
            "name": "getReceivedRequests",
            "outputs": [
                {
                    "components": [
                        {
                            "internalType": "uint256",
                            "name": "requestId",
                            "type": "uint256"
                        },
                        {
                            "internalType": "address",
                            "name": "requester",
                            "type": "address"
                        },
                        {
                            "internalType": "address",
                            "name": "debtor",
                            "type": "address"
                        },
                        {
                            "internalType": "uint256",
                            "name": "amount",
                            "type": "uint256"
                        },
                        {
                            "internalType": "bool",
                            "name": "isPaid",
                            "type": "bool"
                        },
                        {
                            "internalType": "bool",
                            "name": "isRejected",
                            "type": "bool"
                        }
                    ],
                    "internalType": "struct Splitwise.MoneyRequest[]",
                    "name": "",
                    "type": "tuple[]"
                }
            ],
            "stateMutability": "view",
            "type": "function"
        },
        {
            "inputs": [],
            "name": "getSentRequests",
            "outputs": [
                {
                    "components": [
                        {
                            "internalType": "uint256",
                            "name": "requestId",
                            "type": "uint256"
                        },
                        {
                            "internalType": "address",
                            "name": "requester",
                            "type": "address"
                        },
                        {
                            "internalType": "address",
                            "name": "debtor",
                            "type": "address"
                        },
                        {
                            "internalType": "uint256",
                            "name": "amount",
                            "type": "uint256"
                        },
                        {
                            "internalType": "bool",
                            "name": "isPaid",
                            "type": "bool"
                        },
                        {
                            "internalType": "bool",
                            "name": "isRejected",
                            "type": "bool"
                        }
                    ],
                    "internalType": "struct Splitwise.MoneyRequest[]",
                    "name": "",
                    "type": "tuple[]"
                }
            ],
            "stateMutability": "view",
            "type": "function"
        },
        {
            "inputs": [],
            "name": "getTotalAmountOwed",
            "outputs": [
                {
                    "internalType": "uint256",
                    "name": "",
                    "type": "uint256"
                }
            ],
            "stateMutability": "view",
            "type": "function"
        },
        {
            "inputs": [],
            "name": "getTotalAmountRequested",
            "outputs": [
                {
                    "internalType": "uint256",
                    "name": "",
                    "type": "uint256"
                }
            ],
            "stateMutability": "view",
            "type": "function"
        },
        {
            "inputs": [
                {
                    "internalType": "uint256",
                    "name": "",
                    "type": "uint256"
                }
            ],
            "name": "moneyRequests",
            "outputs": [
                {
                    "internalType": "uint256",
                    "name": "requestId",
                    "type": "uint256"
                },
                {
                    "internalType": "address",
                    "name": "requester",
                    "type": "address"
                },
                {
                    "internalType": "address",
                    "name": "debtor",
                    "type": "address"
                },
                {
                    "internalType": "uint256",
                    "name": "amount",
                    "type": "uint256"
                },
                {
                    "internalType": "bool",
                    "name": "isPaid",
                    "type": "bool"
                },
                {
                    "internalType": "bool",
                    "name": "isRejected",
                    "type": "bool"
                }
            ],
            "stateMutability": "view",
            "type": "function"
        },
        {
            "inputs": [],
            "name": "nxtRequestId",
            "outputs": [
                {
                    "internalType": "uint256",
                    "name": "",
                    "type": "uint256"
                }
            ],
            "stateMutability": "view",
            "type": "function"
        },
        {
            "inputs": [],
            "name": "payForAllTheRequests",
            "outputs": [],
            "stateMutability": "payable",
            "type": "function"
        },
        {
            "inputs": [
                {
                    "internalType": "uint256",
                    "name": "requestId",
                    "type": "uint256"
                }
            ],
            "name": "payForRequestedAmount",
            "outputs": [],
            "stateMutability": "payable",
            "type": "function"
        },
        {
            "inputs": [
                {
                    "internalType": "address",
                    "name": "person",
                    "type": "address"
                }
            ],
            "name": "payToAddress",
            "outputs": [],
            "stateMutability": "payable",
            "type": "function"
        },
        {
            "inputs": [
                {
                    "internalType": "address[]",
                    "name": "cyclePath",
                    "type": "address[]"
                }
            ],
            "name": "processCycle",
            "outputs": [],
            "stateMutability": "nonpayable",
            "type": "function"
        },
        {
            "inputs": [
                {
                    "internalType": "address",
                    "name": "",
                    "type": "address"
                },
                {
                    "internalType": "uint256",
                    "name": "",
                    "type": "uint256"
                }
            ],
            "name": "receivedRequests",
            "outputs": [
                {
                    "internalType": "bool",
                    "name": "",
                    "type": "bool"
                }
            ],
            "stateMutability": "view",
            "type": "function"
        },
        {
            "inputs": [
                {
                    "internalType": "uint256",
                    "name": "requestId",
                    "type": "uint256"
                }
            ],
            "name": "rejectMoneyRequest",
            "outputs": [],
            "stateMutability": "nonpayable",
            "type": "function"
        },
        {
            "inputs": [
                {
                    "internalType": "address[]",
                    "name": "cyclePath",
                    "type": "address[]"
                }
            ],
            "name": "resolveCycle",
            "outputs": [],
            "stateMutability": "nonpayable",
            "type": "function"
        },
        {
            "inputs": [
                {
                    "internalType": "address",
                    "name": "",
                    "type": "address"
                },
                {
                    "internalType": "uint256",
                    "name": "",
                    "type": "uint256"
                }
            ],
            "name": "sentRequests",
            "outputs": [
                {
                    "internalType": "bool",
                    "name": "",
                    "type": "bool"
                }
            ],
            "stateMutability": "view",
            "type": "function"
        },
        {
            "inputs": [
                {
                    "internalType": "address",
                    "name": "debtor",
                    "type": "address"
                },
                {
                    "internalType": "uint256",
                    "name": "amount",
                    "type": "uint256"
                }
            ],
            "name": "submitMoneyRequest",
            "outputs": [],
            "stateMutability": "nonpayable",
            "type": "function"
        },
        {
            "inputs": [
                {
                    "internalType": "uint256",
                    "name": "",
                    "type": "uint256"
                }
            ],
            "name": "users",
            "outputs": [
                {
                    "internalType": "address",
                    "name": "",
                    "type": "address"
                }
            ],
            "stateMutability": "view",
            "type": "function"
        },
        {
            "stateMutability": "payable",
            "type": "receive"
        }
    ];

    const splitwise = new Splitwise(contract_address, private_key);
    // splitwise.submitMoneyRequest("0x2004519FAE763b3C98c4f74121749EeA1d5f156B", 1).then((requestId) => {
    //     console.log(requestId);
    // });

    // splitwise.getParticipatingAddresses().then((addresses) => {
    //     console.log(addresses);
    // });


    splitwise.getTotalAmountOwed().then((amount) => {  
        console.log(amount);
    });

    splitwise.getTotalAmountRequested().then((amount) => {
        console.log(amount);
    });


    splitwise.getSentRequests().then((requests) => {
        console.log(requests);
    });

    // 0x44942B099cEff5ba9d26038a0f1AB491D54378c6
    // 0x2004519FAE763b3C98c4f74121749EeA1d5f156B
    // 0x7E9eb9667cec70E1adE01682E101198B31B5f526
    // testing accounts addresses
}

main();