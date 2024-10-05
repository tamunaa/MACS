import { AnchorProvider } from "@coral-xyz/anchor";
import { Connection, Keypair, clusterApiUrl, PublicKey } from "@solana/web3.js";
import NodeWallet from "@coral-xyz/anchor/dist/cjs/nodewallet";
import { GlobalState, Listing } from "./types";
import { Nft } from "@metaplex-foundation/js";


import { SystemProgram, Transaction } from "@solana/web3.js";
import { AccountLayout } from "@solana/spl-token";

import BN from 'bn.js';


const secretKey = [118,254,117,44,72,206,230,128,149,155,192,26,167,102,95,220,119,214,253,64,56,63,228,89,52,241,69,37,230,123,91,18,218,198,92,90,173,10,89,103,59,72,13,110,239,103,137,204,104,60,58,42,189,225,231,235,159,123,247,155,3,33,103,147];

// define your private key here
const keypair = Keypair.fromSecretKey(new Uint8Array(secretKey));

export class MarketplaceClient{
    connection: Connection;
    provider: AnchorProvider;
    counterPublicKey: PublicKey | null = null;

    /**
     *
     */
    constructor() {
        console.log("keypair", keypair);
        this.connection = new Connection(clusterApiUrl("testnet"));
        this.provider = new AnchorProvider(this.connection, new NodeWallet(keypair), {});

        // Initialize the counter.
        this.initializeCounter().then(counterPublicKey => {
            this.counterPublicKey = counterPublicKey;
            console.log("Counter account public key:", counterPublicKey.toString());
        }).catch(error => {
            console.error("Failed to initialize counter:", error);
        });

    }

    public async initializeCounter(): Promise<PublicKey> {
        // Generate a new public key for the counter account.
        const counterAccount = Keypair.generate();
    
        // Create the transaction to initialize the counter account.
        const transaction = new Transaction().add(
            SystemProgram.createAccount({
                fromPubkey: this.provider.wallet.publicKey,
                newAccountPubkey: counterAccount.publicKey,
                space: AccountLayout.span, // The size of the MarketplaceCounter account in bytes.
                lamports: await this.connection.getMinimumBalanceForRentExemption(AccountLayout.span),
                programId: new PublicKey('Fj1JwjGrEt8bagLDCZ5G8hptcfcq3fZg2ahvBwrtcNXC'), 
            })
        );
    
        const signedTransaction = await this.provider.wallet.signTransaction(transaction);
        
        const serializedTransaction = signedTransaction.serialize();
        
        const transactionId = await this.connection.sendRawTransaction(serializedTransaction);
        
        await this.connection.confirmTransaction(transactionId);
        
        return counterAccount.publicKey || void 0;
    }

    public async initializeMarketplace() {
        const instructionData = Buffer.alloc(1);
        instructionData[0] = 0; 
        // Create the transaction to initialize the marketplace.
        const transaction = new Transaction().add({
            keys: [{ pubkey: this.provider.wallet.publicKey, isSigner: true, isWritable: true }],
            programId: new PublicKey('Fj1JwjGrEt8bagLDCZ5G8hptcfcq3fZg2ahvBwrtcNXC'), // The program ID of your Solana program.
            data: instructionData,
        });

        // Sign and send the transaction.
        const signedTransaction = await this.provider.wallet.signTransaction(transaction);
        const serializedTransaction = signedTransaction.serialize();
        const transactionId = await this.connection.sendRawTransaction(serializedTransaction);
        await this.connection.confirmTransaction(transactionId);
    }

    private async signAndSendTransaction(transaction: Transaction): Promise<string> {
        const signedTransaction = await this.provider.wallet.signTransaction(transaction);
        const serializedTransaction = signedTransaction.serialize();
        const transactionId = await this.connection.sendRawTransaction(serializedTransaction, { skipPreflight: true });
        await this.connection.confirmTransaction(transactionId, 'finalized');
        return transactionId;
    }
    

    public async listNft(nftPublicKey: PublicKey, price: number) {
        const instructionData = Buffer.alloc(1 + 32 + 8); // 1 byte for the instruction index, 32 bytes for the NFT public key, and 8 bytes for the price.
        instructionData[0] = 1; // The index of the list_nft instruction in the program.
        nftPublicKey.toBuffer().copy(instructionData, 1); // The public key of the NFT to list.
        new DataView(instructionData.buffer).setFloat64(33, price, true); // The price to list the NFT at.

        const transaction = new Transaction().add({
            keys: [
                { pubkey: this.provider.wallet.publicKey, isSigner: true, isWritable: true },
                { pubkey: nftPublicKey, isSigner: false, isWritable: true },
            ],
            programId: new PublicKey('Fj1JwjGrEt8bagLDCZ5G8hptcfcq3fZg2ahvBwrtcNXC'), 
            data: instructionData,
        });

        const signedTransaction = await this.provider.wallet.signTransaction(transaction);
        const serializedTransaction = signedTransaction.serialize();
        const transactionId = await this.connection.sendRawTransaction(serializedTransaction);
        await this.connection.confirmTransaction(transactionId);
    }

    public async listNftInSpl(nftPublicKey: PublicKey, price: number, splTokenMint: PublicKey) {
        const instructionData = Buffer.alloc(1 + 32 + 8 + 32); // Instruction index + NFT PK + Price + SPL Token Mint PK
        instructionData[0] = 2; // Suppose the index for listNftInSpl is 2
        nftPublicKey.toBuffer().copy(instructionData, 1);
        new DataView(instructionData.buffer).setFloat64(33, price, true); 
        splTokenMint.toBuffer().copy(instructionData, 41);
    
        const transaction = new Transaction().add({
            keys: [
                { pubkey: this.provider.wallet.publicKey, isSigner: true, isWritable: true },
                { pubkey: nftPublicKey, isSigner: false, isWritable: true },
                { pubkey: splTokenMint, isSigner: false, isWritable: false }, 
            ],
            programId: new PublicKey('Fj1JwjGrEt8bagLDCZ5G8hptcfcq3fZg2ahvBwrtcNXC'),
            data: instructionData,
        });
    
        await this.signAndSendTransaction(transaction);
    }
    
    

    public async updatePrice(listingPublicKey: PublicKey, newPrice: number) {
        const instructionData = Buffer.alloc(1 + 32 + 8); 
        listingPublicKey.toBuffer().copy(instructionData, 1);
        new DataView(instructionData.buffer).setFloat64(33, newPrice, true);
    
        const transaction = new Transaction().add({
            keys: [
                { pubkey: this.provider.wallet.publicKey, isSigner: true, isWritable: true },
                { pubkey: listingPublicKey, isSigner: false, isWritable: true },
            ],
            programId: new PublicKey(''),
            data: instructionData,
        });
    
        await this.signAndSendTransaction(transaction);
    }
    

    public async cancelListing(listingPublicKey: PublicKey) {
        const instructionData = Buffer.alloc(1 + 32); // Instruction index + Listing PK
        instructionData[0] = 4; 
        listingPublicKey.toBuffer().copy(instructionData, 1);
    
        const transaction = new Transaction().add({
            keys: [
                { pubkey: this.provider.wallet.publicKey, isSigner: true, isWritable: true },
                { pubkey: listingPublicKey, isSigner: false, isWritable: true },
            ],
            programId: new PublicKey(''),
            data: instructionData,
        });
    
        await this.signAndSendTransaction(transaction);
    }
    

    public async buyNft(listingPublicKey: PublicKey, buyerPublicKey: PublicKey): Promise<void> {
        const instructionData = Buffer.alloc(1 + 32 + 32);
        instructionData[0] = 1;
    
        const transaction = new Transaction().add({
            keys: [
                { pubkey: buyerPublicKey, isSigner: true, isWritable: false },
                { pubkey: listingPublicKey, isSigner: false, isWritable: true },
            ],
            programId: new PublicKey('Fj1JwjGrEt8bagLDCZ5G8hptcfcq3fZg2ahvBwrtcNXC'),
            data: instructionData,
        });
    
        await this.signAndSendTransaction(transaction);
    }
    

    public async buyNftWithSpl(listingPublicKey: PublicKey, buyerPublicKey: PublicKey, amount: number): Promise<void> {
        const instructionData = Buffer.alloc(1 + 32 + 8); 
        instructionData[0] = 0;
    
        const transaction = new Transaction()
            .add({
                keys: [
                    { pubkey: buyerPublicKey, isSigner: true, isWritable: false },
                    { pubkey: listingPublicKey, isSigner: false, isWritable: true },
                ],
                programId: new PublicKey('Fj1JwjGrEt8bagLDCZ5G8hptcfcq3fZg2ahvBwrtcNXC'),
                data: instructionData,
            });
    
        await this.signAndSendTransaction(transaction);
    }
    

    public async getMarketplaceMetadata(): Promise<GlobalState> {
        const metadataAccountPublicKey = new PublicKey('Fj1JwjGrEt8bagLDCZ5G8hptcfcq3fZg2ahvBwrtcNXC');
    
        const accountInfo = await this.connection.getAccountInfo(metadataAccountPublicKey);
        if (!accountInfo) throw new Error("Failed to find marketplace metadata account.");
    
        const metadata: GlobalState = this.deserializeMetadata(accountInfo.data);
    
        return metadata;
    }
    
    private deserializeMetadata(data: Buffer): GlobalState {
        const decodedData: GlobalState = JSON.parse(data.toString());
        return decodedData;
    }
    

    public async getUserListings(connection: Connection, programId: PublicKey, userPublicKey: PublicKey): Promise<Listing[]> {
        const allListingAccounts = await connection.getProgramAccounts(programId, {
            filters: [
            ],
        });
    
        const userListings: Listing[] = allListingAccounts
            .map(({ account }) => {
                return {} as Listing;
            })
            .filter(listing => listing.initializer.equals(userPublicKey));
        return userListings;
    }

    public async getAllListings(): Promise<Listing[]> {
        const connection = new Connection('https://api.mainnet-beta.solana.com');
        const programId = new PublicKey('Fj1JwjGrEt8bagLDCZ5G8hptcfcq3fZg2ahvBwrtcNXC'); 
        const accounts = await connection.getProgramAccounts(programId);
        
        const listings: Listing[] = accounts.map(account => {
            const data = new Uint8Array(account.account.data);
            // Parse the data based on your program's specific data layout
            const listing: Listing = {
                global_state_address: new PublicKey(data.slice(0, 32)),
                initializer: new PublicKey(data.slice(32, 64)),
                nft_mint_address: new PublicKey(data.slice(64, 96)),
                nft_holder_address: new PublicKey(data.slice(96, 128)),
                price: new BN(data.slice(128, 136), 10, 'le'),
                creation_time: new BN(data.slice(136, 144), 10, 'le').toNumber(),
                updated_at: new BN(data.slice(144, 152), 10, 'le').toNumber(),
                is_spl_listing: !!data[152],
                trade_spl_token_mint_address: new PublicKey(data.slice(153, 185)),
                trade_spl_token_seller_account_address: new PublicKey(data.slice(185, 217)),
            };
            return listing;
        });

        return listings;
    }
    
    
    public async getUserNfts(address: string): Promise<Nft[]> {
        const userPublicKey = new PublicKey(address);
        const connection = this.connection; 

        const tokenAccounts = await connection.getParsedTokenAccountsByOwner(userPublicKey, {
            programId: new PublicKey(""),
        });

        const nftAccounts = tokenAccounts.value.filter((account) => {
            const amount = account.account.data.parsed.info.tokenAmount.uiAmount;
            const decimals = account.account.data.parsed.info.tokenAmount.decimals;
            return amount === 1 && decimals === 0;
        });

        let Metadata;
        const nfts = await Promise.all(nftAccounts.map(async (account) => {
            const mintAddress = account.account.data.parsed.info.mint;
            const metadataPDA = await Metadata.getPDA(new PublicKey(mintAddress));
            const metadataAccount = await Metadata.load(connection, metadataPDA);
            return {
                mint: mintAddress,
                metadataUri: metadataAccount.data.data.uri,
                name: metadataAccount.data.data.name,
                symbol: metadataAccount.data.data.symbol,
            };
        }));
        
        return [];
    }
}