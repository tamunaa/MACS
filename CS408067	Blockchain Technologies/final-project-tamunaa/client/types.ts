import { Nft } from "@metaplex-foundation/js";
import { PublicKey } from "@solana/web3.js"
import BN from "bn.js"

export type GlobalState = {
    initializer: PublicKey,
    totalListedCountSol: BN;
    totalListedCountSpl: BN;

    totalVolumeAllTimeSol: BN;

    allTimeSaleCountSpl: BN;
    allTImeSaleCountSol: BN;
}

export type Listing = {
    global_state_address: PublicKey;
    initializer: PublicKey;
    nft_mint_address: PublicKey;
    nft_holder_address: PublicKey;
    price: BN;
    creation_time: number;
    updated_at: number;
    is_spl_listing: boolean;
    trade_spl_token_mint_address: PublicKey;
    trade_spl_token_seller_account_address: PublicKey;
};