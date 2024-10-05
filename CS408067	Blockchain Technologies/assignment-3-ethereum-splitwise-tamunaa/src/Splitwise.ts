import {MoneyRequest} from "./models";
import {Contract, JsonRpcProvider, Wallet} from "ethers";

export class Splitwise {

    private wallet: Wallet;
    private contract: Contract;

    constructor(private contractAddress: string, private signerKey: string) {
    // const provider = new JsonRpcProvider('https://rpc.notadegen.com/eth/sepolia');

    const provider = new JsonRpcProvider('https://ethereum-sepolia.publicnode.com');

    this.wallet = new Wallet(this.signerKey, provider);

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

    this.contract = new Contract(contractAddress, abi, this.wallet);
    }

    /*
        this method submits a money request, which should be saved on a blockchain.
        returns hash of submitted transaction
     */
    async submitMoneyRequest(to: string, amount: number): Promise<void> {
        try {
            await this.contract.submitMoneyRequest(to, amount);

            // cota dzviriani nawilia
            // let cycle = await this.findCycleFromNode(to);
            // while (cycle !== null) {
            //     await this.contract.resolveCycle(cycle);
            //     cycle = await this.findCycleFromNode(to);
            // }


        } catch (error) {
            console.error("Error submitting money request:", error);
            throw error;
        }
    }

    /*
        this method should return all the addresses which are in a cycle with the signer.
        If there is no cycle, method should return null.
     */
    async findCycleFromNode(node: string): Promise<string[] | null> {
        let visited = new Set<string>();
        let stack: {node: string, path: string[]}[] = [{node: node, path: []}];

        while (stack.length > 0) {
            let {node, path} = stack.pop()!;

            if (visited.has(node)) {
                return [...path, node]; 
            }

            visited.add(node);

            let neighbors = await this.contract.getNeighbors(node);
            for (let neighbor of neighbors) {
                if (!visited.has(neighbor)) {
                    stack.push({node: neighbor, path: [...path, node]});
                }
            }
        }

        return null; 
    }

    /*
        this method is similar to `submitMoneyRequest` but works on multiple addresses and total
        amount if split among those addresses.
        error should be raised if an amount is not evenly divisible on all addresses.
     */
    async splitTheBill(totalAmount: number, addresses: string[]): Promise<void> {
        try {
            const amountPerAddress = totalAmount / addresses.length;
            if (amountPerAddress !== Math.floor(amountPerAddress)) {
                throw new Error("Total amount must be evenly divisible among addresses");
            }
    
            await Promise.all(
                addresses.map(async (address) => {
                    await this.contract.submitMoneyRequest(address, amountPerAddress);
                })
            );
        } catch (error) {
            console.error("Error splitting the bill:", error);
            throw error;
        }
    }
    /*
        this method rejects a specific money request, if the request if sent to the signer.
        Money request should be removed from a storage after rejection.
        method accepts requestId as a parameter, contract should be generating unique request ID
        for each request
     */
    async rejectMoneyRequest(requestId: number): Promise<void> {
        try {
            await this.contract.rejectMoneyRequest(requestId);
        } catch (error) {
            console.error("Error rejecting money request:", error);
            throw error;
        }
    }
    /*
        this method is mean to revoke your sent money request, Revoked MoneyRequest should also
        be deleted
     */
    async cancelMoneyRequest(): Promise<void> {
        try {
            await this.contract.cancelMoneyRequest();
        } catch (error) {
            console.error("Error canceling money request:", error);
            throw error;
        }
    }
    /*
        transfers amount of wei to the requesting party, paid requests should also be deleted from a list of
        incoming and outcoming requests. You probably need to declare corresponding solidity function as payable
     */
    async payForRequestedAmount(requestId: number, value: number): Promise<void> {
        try {
            await this.contract.payForRequestedAmount(requestId, { value });
        } catch (error) {
            console.error("Error paying for requested amount:", error);
            throw error;
        }
    }

    /*
        this method pays for a money request by address. 
        if several requests are sent from that address,
        the method should pay from all of them.
     */
    async payToAddress(address: string): Promise<void> {
        try {
            await this.contract.payToAddress(address);
        } catch (error) {
            console.error("Error paying to address:", error);
            throw error;
        }
    }
    /*
        This method should pay from all the incoming requests for the signer.
     */
    async payForAllTheRequests(): Promise<void> {
        try {
            await this.contract.payForAllTheRequests();
        } catch (error) {
            console.error("Error paying for all the requests:", error);
            throw error;
        }
    }
    // queries
    
    /*
        Fetch all the addresses which received or sent money requests througout the history of a smart contract
     */
    async getParticipatingAddresses(): Promise<string[]> {
        try {
            const addresses = await this.contract.getParticipatingAddresses();
            return addresses;
        } catch (error) {
            console.error("Error getting participating addresses:", error);
            throw error;
        }
    }

    /*
        fetch requests sent by the signer
     */
    async getSentRequests(): Promise<MoneyRequest[]> {
        try {
            const sentRequests = await this.contract.getSentRequests();
            return sentRequests;
        } catch (error) {
            console.error("Error getting sent requests:", error);
            throw error;
        }
    }

    /*
        fetch requests sent to the signer by other users.
     */
    async getReceivedRequests(): Promise<MoneyRequest[]> {
        try {
            const receivedRequests = await this.contract.getReceivedRequests();
            return receivedRequests;
        } catch (error) {
            console.error("Error getting received requests:", error);
            throw error;
        }
    }


    /*
        get all the addresses who have sent money requests to the signer.
        Payed or Rejected requests should not be returned
     */
    async getAllCreditors(): Promise<string[]> {
        try {
            const creditors = await this.contract.getAllCreditors();
            return creditors;
        } catch (error) {
            console.error("Error getting all creditors:", error);
            throw error;
        }
    }

    /*
        fetch all addresses to whom signer have sent the money requests.
        This method should return only active
        requests as well.
     */
    async getAllDebtors(): Promise<string[]> {
        try {
            const debtors = await this.contract.getAllDebtors();
            return debtors;
        } catch (error) {
            console.error("Error getting all debtors:", error);
            throw error;
        }
    }

    /*
        method fetches total amount owed by combining all the incomming active requests' amounts.
     */
    async getTotalAmountOwed(): Promise<number> {
        try {
            const totalAmountOwed = await this.contract.getTotalAmountOwed();
            return totalAmountOwed;
        } catch (error) {
            console.error("Error getting total amount owed:", error);
            throw error;
        }
    }
    /*
        Fetches total amount requested by the signer from other users
     */
    async getTotalAmountRequested(): Promise<number> {
        try {
            const totalAmountRequested = await this.contract.getTotalAmountRequested();
            return totalAmountRequested;
        } catch (error) {
            console.error("Error getting total amount requested:", error);
            throw error;
        }
    }
    /*
        gets total amount owed to specific address by signer
     */
    async getAmountOwedTo(address: string): Promise<number> {
        try {
            const amountOwed = await this.contract.getAmountOwedTo(address);
            return amountOwed;
        } catch (error) {
            console.error("Error getting amount owed to address:", error);
            throw error;
        }
    }

    /*
        gets total amount which signer requested from specific address.
     */
    async getAmountRequestedFrom(address: string): Promise<number> {
        try {
            const amountRequested = await this.contract.getAmountRequestedFrom(address);
            return amountRequested;
        } catch (error) {
            console.error("Error getting amount requested from address:", error);
            throw error;
        }
    }

}