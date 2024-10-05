use anchor_lang::prelude::*;
use mpl_token_metadata::state::*;
use anchor_spl::token::{self, Mint, Token, TokenAccount, Transfer};

use anchor_lang::solana_program::{
    system_instruction,
    pubkey::Pubkey,
    program::invoke_signed,
};


use anchor_lang::prelude::AccountInfo;

// Rest of your code...

declare_id!("FxHLWgvCiF8PxSJ94E1kckuA6RkFYa3N1MhBTJaQEjn4");



#[program]
pub mod nft_marketplace {
    use super::*;

    pub fn initialize_marketplace(ctx: Context<InitializeMarketplace>) -> Result<()> {
        let global_state = &mut ctx.accounts.global_state;
        global_state.initializer = *ctx.accounts.initializer.key;
        global_state.marketplace_fee_percentage = 7;
        global_state.total_listed_count_sol = 0;
        global_state.total_listed_count_spl = 0;
        global_state.total_volume_all_time_sol = 0;
        global_state.all_time_sale_count_spl = 0;
        global_state.all_time_sale_count_sol = 0;

        let counter = &mut ctx.accounts.counter;
        counter.count += 1;

        // Create a derived address for the global state
        let base = ctx.accounts.initializer.key;
        let counter_str = counter.count.to_string();
        let program_id = ctx.program_id;

        let global_state_address = Pubkey::create_with_seed(base, &counter_str, program_id)
            .map_err(|_| ProgramError::InvalidSeeds)?;


        let global_state_info: AccountInfo = ctx.accounts.global_state.to_account_info();


        // Create an account at the derived address
        let lamports = global_state_info.lamports();
        let space = global_state_info.data_len();
        let owner = global_state_info.owner;
        let signer_seeds = &[base.as_ref(), counter_str.as_bytes()];
        let instruction = system_instruction::create_account_with_seed(
            ctx.accounts.initializer.key, // From
            &global_state_address, // To
            base, // Base
            &counter_str, // Seed
            lamports,
            space as u64,
            owner,
        );

        let accounts = [
            ctx.accounts.global_state.to_account_info(),
            ctx.accounts.initializer.to_account_info(),
            ctx.accounts.system_program.to_account_info(),
            ctx.accounts.counter.to_account_info(),
        ];

        invoke_signed(&instruction, &accounts, &[signer_seeds])?;

        Ok(())
    }

    pub fn list_nft(ctx: Context<AddListing>, price: u64) -> Result<()> {
        let listing = &mut ctx.accounts.listing;
        listing.initializer = *ctx.accounts.initializer.key;
        listing.nft_mint_address = ctx.accounts.nft.mint;
        listing.price = price;
        listing.creation_time = Clock::get()?.unix_timestamp;
        listing.updated_at = Clock::get()?.unix_timestamp;

        // Increment the SOL listing count in GlobalState
        let global_state = &mut ctx.accounts.global_state;
        global_state.total_listed_count_sol += 1;

        let cpi_accounts = Transfer {
            from: ctx.accounts.nft.to_account_info(),
            to: ctx.accounts.listing.to_account_info(),
            authority: ctx.accounts.initializer.to_account_info(),
        };
        let cpi_program = ctx.accounts.token_program.to_account_info();
        let cpi_ctx = CpiContext::new(cpi_program, cpi_accounts);
        token::transfer(cpi_ctx, 1)?;

        // Add the new listing's address to the Listings account
        let listings = &mut ctx.accounts.listings;
        let listing_address = ctx.accounts.listing.to_account_info().key;
        if listings.listing_addresses.len() >= MAX_LISTINGS {
            return Err(ProgramError::Custom(ErrorCode::TooManyListings as u32).into());
        }
        listings.listing_addresses.push(*listing_address);

        Ok(())
    }

    use anchor_spl::token::{self, Transfer};

    pub fn list_nft_in_spl(ctx: Context<ListNftInSpl>, price: u64, trade_spl_token_mint_address: Pubkey) -> Result<()> {
        let listing = &mut ctx.accounts.listing;
        listing.initializer = *ctx.accounts.initializer.key;
        listing.nft_mint_address = ctx.accounts.nft_to_list.mint;
        listing.price = price;
        listing.creation_time = Clock::get()?.unix_timestamp;
        listing.updated_at = Clock::get()?.unix_timestamp;
        listing.is_spl_listing = true;
        listing.trade_spl_token_mint_address = trade_spl_token_mint_address;

        // Transfer NFT to the escrow account
        let cpi_accounts = Transfer {
            from: ctx.accounts.nft_to_list.to_account_info(),
            to: ctx.accounts.escrow_nft_account.to_account_info(),
            authority: ctx.accounts.initializer.to_account_info(),
        };
        let cpi_program = ctx.accounts.token_program.to_account_info();
        let cpi_ctx = CpiContext::new(cpi_program, cpi_accounts);
        token::transfer(cpi_ctx, 1)?;

        ctx.accounts.global_state.total_listed_count_spl += 1;
        Ok(())
    }


    pub fn update_price(ctx: Context<UpdatePrice>, new_price: u64) -> Result<()> {
        let listing = &mut ctx.accounts.listing;
        // Update the listing's price
        listing.price = new_price;
        listing.updated_at = Clock::get()?.unix_timestamp;

        Ok(())
    }

    pub fn cancel_listing(ctx: Context<CancelListing>) -> Result<()> {
        // Transfer the NFT back to the initializer.
        let cpi_accounts = Transfer {
            from: ctx.accounts.escrow_nft_account.to_account_info(),
            to: ctx.accounts.initializer.to_account_info(),
            authority: ctx.accounts.nft_holder_pda.clone(),
        };
        let cpi_program = ctx.accounts.token_program.to_account_info();
        let seeds = &[
            b"escrow".as_ref(),
            ctx.accounts.listing.nft_mint_address.as_ref(),
            &[ctx.accounts.listing.nft_holder_bump],
        ];
        let signer = &[&seeds[..]];
        let cpi_ctx = CpiContext::new_with_signer(cpi_program, cpi_accounts, signer);
        token::transfer(cpi_ctx, 1)?;

        // Decrement the global state counters as appropriate.
        // This could be conditioned on whether the listing was in SOL or an SPL token.
        if ctx.accounts.listing.is_spl_listing {
            ctx.accounts.global_state.total_listed_count_spl -= 1;
        } else {
            ctx.accounts.global_state.total_listed_count_sol -= 1;
        }

        Ok(())
    }

    pub fn buy_nft(ctx: Context<BuyNft>) -> Result<()> {
        let listing = &ctx.accounts.listing;

        // Transfer SOL from Buyer to Seller
        **ctx.accounts.seller.try_borrow_mut_lamports()? = ctx.accounts.seller.lamports()
            .checked_add(listing.price)
            .ok_or(ErrorCode::Overflow.to_program_error())?;

        **ctx.accounts.buyer.try_borrow_mut_lamports()? = ctx.accounts.buyer.lamports()
            .checked_sub(listing.price)
            .ok_or(ProgramError::InsufficientFunds)?;

        let global_state = &mut ctx.accounts.global_state;
        global_state.total_volume_all_time_sol = global_state
            .total_volume_all_time_sol
            .checked_add(listing.price as u128)
            .ok_or(ErrorCode::Overflow.to_program_error())?;
        global_state.all_time_sale_count_sol += 1;

        let marketplace_fee = listing.price.checked_mul(listing.marketplace_fee_percentage as u64).ok_or(ErrorCode::Overflow.to_program_error())? / 100;
        **ctx.accounts.marketplace_owner.try_borrow_mut_lamports()? = ctx.accounts.marketplace_owner.lamports()
            .checked_add(marketplace_fee)
            .ok_or(ErrorCode::Overflow.to_program_error())?;

        **ctx.accounts.seller.try_borrow_mut_lamports()? = ctx.accounts.seller.lamports()
            .checked_sub(marketplace_fee)
            .ok_or(ErrorCode::Overflow.to_program_error())?;

        token::transfer(ctx.accounts.into_transfer_to_buyer_context(), 1)?;

        Ok(())
    }

    pub fn buy_nft_with_spl(ctx: Context<BuyNftInSpl>, amount: u64) -> Result<()> {
        // Transfer SPL tokens from the buyer to the seller
        let transfer_to_seller_cpi_accounts = Transfer {
            from: ctx.accounts.spl_token_account_buyer.to_account_info(),
            to: ctx.accounts.spl_token_account_seller.to_account_info(),
            authority: ctx.accounts.buyer.to_account_info(),
        };
        let transfer_to_seller_cpi_ctx = CpiContext::new(ctx.accounts.token_program.to_account_info(), transfer_to_seller_cpi_accounts);
        token::transfer(transfer_to_seller_cpi_ctx, ctx.accounts.listing.price)?;

        let marketplace_fee = ctx.accounts.listing.price / 100;
        let transfer_fee_cpi_accounts = Transfer {
            from: ctx.accounts.spl_token_account_buyer.to_account_info(),
            to: ctx.accounts.marketplace_fee_account.to_account_info(),
            authority: ctx.accounts.buyer.to_account_info(),
        };
        let transfer_fee_cpi_ctx = CpiContext::new(ctx.accounts.token_program.to_account_info(), transfer_fee_cpi_accounts);
        token::transfer(transfer_fee_cpi_ctx, marketplace_fee)?;

        let global_state = &mut ctx.accounts.global_state;
        global_state.all_time_sale_count_spl += 1;

        // Transfer the NFT from the seller to the buyer
        let (escrow_pda, bump_seed) = Pubkey::find_program_address(
            &[b"escrow", ctx.accounts.listing.nft_mint_address.as_ref()],
            ctx.program_id,
        );

        let seeds = &[&b"escrow"[..], ctx.accounts.listing.nft_mint_address.as_ref(), &[bump_seed]];
        let signer = &[&seeds[..]];

        let transfer_nft_cpi_accounts = Transfer {
            from: ctx.accounts.nft_escrow_account.to_account_info(),
            to: ctx.accounts.buyer.to_account_info(),
            authority: ctx.accounts.nft_escrow_account.to_account_info(),
        };
        let transfer_nft_cpi_ctx = CpiContext::new_with_signer(ctx.accounts.token_program.to_account_info(), transfer_nft_cpi_accounts, signer);
        token::transfer(transfer_nft_cpi_ctx, 1)?;

        Ok(())
    }
}

pub fn query_user_listings_from_program(
    user_pubkey: Pubkey,
    listings: Vec<Account<Listing>>,
) -> Vec<Account<Listing>> {
    listings.into_iter()
        .filter(|listing| listing.initializer == user_pubkey)
        .collect()
}


//adding account struct which will keep track of
//how many Marketplaces are created so that
//we have some unique identifier for each marketplace
//and can generate PDA accounts for each marketplace even if they have same initializer
#[account]
pub struct MarketplaceCounter {
    pub count: u64,
}

#[account]
#[derive(Default)]
pub struct GlobalState {
    pub initializer: Pubkey,
    pub total_listed_count_sol: u32,
    pub total_listed_count_spl: u32,

    pub total_volume_all_time_sol: u128,

    pub all_time_sale_count_spl: u64,
    pub all_time_sale_count_sol: u64,
    pub marketplace_fee_percentage: u64,
}

#[derive(Accounts)]
pub struct InitializeMarketplace<'info> {
    #[account(init, payer = initializer, space = 8 + 40)]
    pub global_state: Account<'info, GlobalState>,
    #[account(mut)]
    pub initializer: Signer<'info>,
    pub system_program: Program<'info, System>,

    #[account(mut)]
    pub counter: Account<'info, MarketplaceCounter>,
}

#[derive(Accounts)]
pub struct ListNftInSpl<'info> {
    #[account(mut)]
    pub initializer: Signer<'info>,
    #[account(
    mut,
    constraint = nft_to_list.owner == *initializer.key,
    constraint = nft_to_list.mint == nft_mint.key(),
    )]
    pub nft_to_list: Account<'info, TokenAccount>,
    #[account(
    init,
    payer = initializer,
    space = 8 + 256, // Adjust the space as per your Listing struct
    seeds = [b"listing", nft_to_list.mint.as_ref(), initializer.key().as_ref()],
    bump,
    )]
    pub listing: Account<'info, Listing>,
    #[account(
    init,
    payer = initializer,
    token::mint = nft_mint,
    token::authority = listing,
    seeds = [b"escrow", nft_to_list.mint.as_ref()],
    bump,
    )]
    pub escrow_nft_account: Account<'info, TokenAccount>, // Escrow account for NFT
    pub nft_mint: Account<'info, Mint>, // Mint of the NFT
    #[account(mut)]
    pub global_state: Account<'info, GlobalState>, // Global state of the marketplace
    pub token_program: Program<'info, Token>, // SPL Token program
    pub system_program: Program<'info, System>, // System program
    pub rent: Sysvar<'info, Rent>, // Rent sysvar for account creation
}


#[derive(Accounts)]
pub struct BuyNftInSpl<'info> {
    #[account(mut)]
    pub buyer: Signer<'info>,
    #[account(mut)]
    pub seller: AccountInfo<'info>,
    #[account(mut)]
    pub nft_escrow_account: Account<'info, TokenAccount>,
    #[account(mut)]
    pub listing: Account<'info, Listing>,
    #[account(mut)]
    pub spl_token_account_buyer: Account<'info, TokenAccount>,
    #[account(mut)]
    pub spl_token_account_seller: Account<'info, TokenAccount>,
    pub spl_token_mint: Account<'info, Mint>,
    #[account(mut)]
    pub marketplace_fee_account: Account<'info, TokenAccount>,
    pub token_program: Program<'info, Token>,
    pub global_state: Account<'info, GlobalState>,
}

#[derive(Accounts)]
pub struct UpdatePrice<'info> {
    #[account(
    mut,
    has_one = initializer,
    constraint = listing.nft_holder_address != Pubkey::default(),
    )]
    pub listing: Account<'info, Listing>,
    pub initializer: Signer<'info>,
    pub global_state: Account<'info, GlobalState>,
}

#[derive(Accounts)]
pub struct CancelListing<'info> {
    #[account(
    mut,
    has_one = initializer, // Ensures only the listing initializer can cancel the listing.
    close = initializer // Returns the remaining balance of the listing account to the initializer.
    )]
    pub listing: Account<'info, Listing>,
    #[account(mut)]
    pub initializer: Signer<'info>,
    #[account(
    mut,
    constraint = escrow_nft_account.mint == listing.nft_mint_address, // Ensures the NFT account matches the listing.
    constraint = escrow_nft_account.owner == listing.nft_holder_address // Ensures the NFT is held by the correct PDA.
    )]
    pub escrow_nft_account: Account<'info, TokenAccount>,
    /// The program-derived address (PDA) that currently holds the NFT.
    #[account(mut)]
    pub nft_holder_pda: AccountInfo<'info>,
    pub token_program: Program<'info, Token>,
    #[account(mut)]
    pub global_state: Account<'info, GlobalState>,
}


//******************************FOR BUY NFT******************************//
#[derive(Accounts)]
pub struct BuyNft<'info> {
    #[account(mut)]
    pub buyer: Signer<'info>,
    #[account(mut)]
    pub seller: AccountInfo<'info>,
    #[account(
    mut,
    constraint = listing.nft_holder_address == escrow_nft_account.key(),
    close = marketplace_owner
    )]
    pub listing: Account<'info, Listing>,
    /// CHECK: This is safe because we're only transferring tokens from it after checks.
    #[account(mut)]
    pub escrow_nft_account: Account<'info, TokenAccount>,
    #[account(mut)]
    pub marketplace_owner: AccountInfo<'info>,
    #[account(address = token::ID)]
    pub token_program: Program<'info, Token>,
    pub system_program: Program<'info, System>,
    pub global_state: Account<'info, GlobalState>,
}

impl<'info> BuyNft<'info> {
    fn into_transfer_to_buyer_context(&self) -> CpiContext<'_, '_, '_, 'info, Transfer<'info>> {
        let cpi_accounts = Transfer {
            from: self.escrow_nft_account.to_account_info(),
            to: self.buyer.to_account_info(),
            authority: self.listing.to_account_info(),
        };
        CpiContext::new(self.token_program.to_account_info(), cpi_accounts)
    }
}

//*********************************************************************//

#[account]
#[derive(Default)]
pub struct Listing {
    // Marketplace instance global state address
    pub global_state_address: Pubkey,
    pub marketplace_fee_percentage: u64,

    pub initializer: Pubkey,
    pub nft_mint_address: Pubkey,
    pub nft_holder_address: Pubkey,
    pub price: u64,

    pub creation_time: i64,
    pub updated_at: i64,

    // if trade payment is in spl token currency
    pub is_spl_listing: bool,
    // trade spl token address
    pub trade_spl_token_mint_address: Pubkey,
    pub trade_spl_token_seller_account_address: Pubkey,
    pub nft_holder_bump: u8,
}

pub fn add_listing(ctx: Context<AddListing>) -> Result<()> {
    let listings = &mut ctx.accounts.listings;
    let listing_address = ctx.accounts.listing.to_account_info().key;
    if listings.listing_addresses.len() >= MAX_LISTINGS {
        return Err(ProgramError::Custom(ErrorCode::TooManyListings as u32).into());
    }
    listings.listing_addresses.push(*listing_address);
    Ok(())
}

// impl Listing {
//     pub fn deserialize_from_slice(data: &[u8]) -> Result<Self, ProgramError> {
//         Self::deserialize(&mut data)
//             .map_err(|_| ProgramError::InvalidAccountData)
//     }
// }

#[derive(Accounts)]
pub struct ListNft<'info> {
    #[account(mut)]
    pub initializer: Signer<'info>,
    #[account(mut, constraint = nft_to_list.owner == * initializer.key)]
    pub nft_to_list: Account<'info, TokenAccount>,
    #[account(
    init,
    payer = initializer,
    space = 8 + 256,
    seeds = [b"listing", nft_to_list.mint.as_ref()],
    bump
    )]
    pub listing: Account<'info, Listing>,
    #[account(
    init,
    payer = initializer,
    token::mint = nft_mint,
    token::authority = listing,
    seeds = [b"escrow", nft_to_list.mint.as_ref()],
    bump
    )]
    pub escrow_nft_account: Account<'info, TokenAccount>,
    pub nft_mint: Account<'info, Mint>,
    #[account(address = token::ID)]
    pub token_program: Program<'info, Token>,
    pub system_program: Program<'info, System>,
    pub rent: Sysvar<'info, Rent>,
}

#[account]
pub struct Nft {
    pub mint: Pubkey,
    pub owner: Pubkey,
    pub metadata_uri: String,
    pub is_for_sale: bool,
    pub price: u64,
}

#[account]
pub struct Listings {
    pub listing_addresses: Vec<Pubkey>,
}

const MAX_LISTINGS: usize = 100000;

#[derive(Accounts)]
pub struct AddListing<'info> {
    pub listings: Account<'info, Listings>,
    pub listing: Account<'info, Listing>,
    #[account(mut)]
    pub initializer: Signer<'info>,
    #[account(mut)]
    pub nft: Account<'info, Nft>,
    pub token_program: AccountInfo<'info>,
    pub global_state: Account<'info, GlobalState>,
}

impl<'info> ListNft<'info> {
    fn into_transfer_to_escrow_context(&self) -> CpiContext<'_, '_, '_, 'info, Transfer<'info>> {
        let cpi_accounts = Transfer {
            from: self.nft_to_list.to_account_info(),
            to: self.escrow_nft_account.to_account_info(),
            authority: self.initializer.to_account_info(),
        };
        CpiContext::new(self.token_program.to_account_info(), cpi_accounts)
    }
}


//******************************FOR ERRORS******************************//
use std::fmt;

#[derive(Clone, Debug, AnchorSerialize, AnchorDeserialize)]
pub enum ErrorCode {
    TooManyListings,
    IncorrectPaymentAmount,
    Overflow,
}

impl From<ErrorCode> for u32 {
    fn from(error: ErrorCode) -> Self {
        match error {
            ErrorCode::IncorrectPaymentAmount => 1,
            ErrorCode::TooManyListings => 2,
            ErrorCode::Overflow => 3,
        }
    }
}

impl ErrorCode {
    fn name(&self) -> &str {
        match self {
            ErrorCode::IncorrectPaymentAmount => "IncorrectPaymentAmount",
            ErrorCode::TooManyListings => "TooManyListings",
            ErrorCode::Overflow => "Overflow",
        }
    }
}

impl ErrorCode {
    fn to_program_error(&self) -> ProgramError {
        match self {
            ErrorCode::IncorrectPaymentAmount => ProgramError::Custom(1),
            ErrorCode::TooManyListings => ProgramError::Custom(2),
            ErrorCode::Overflow => ProgramError::Custom(3),
        }
    }
}

impl fmt::Display for ErrorCode {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        match self {
            ErrorCode::IncorrectPaymentAmount => write!(f, "Incorrect payment amount"),
            ErrorCode::TooManyListings => write!(f, "Too many listings"),
            ErrorCode::Overflow => write!(f, "Overflow"),
        }
    }
}

impl std::error::Error for ErrorCode {}
