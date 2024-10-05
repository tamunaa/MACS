use solana_program::{
    account_info::{AccountInfo}, clock::Clock, entrypoint::ProgramResult, program_error::ProgramError, program_pack::Pack, pubkey::Pubkey, stake, sysvar::Sysvar
};

use crate::{InitializeStakingPoolData, CreateStakerAccountData, StakeTokensData, UnstakeTokensData, CloseStakerAccountData};
use crate::StakingPoolState;
use borsh::BorshDeserialize;
use crate::UserState;
use solana_program::account_info::next_account_info;
use solana_program::program::invoke_signed;
use solana_program::system_instruction;
use solana_program::sysvar::rent::Rent;

pub fn initialize_staking_pool(
    program_id: &Pubkey,
    accounts: &[AccountInfo],
    data: InitializeStakingPoolData,
) -> ProgramResult {
    let account_info_iter = &mut accounts.iter();
    let staking_pool_account = next_account_info(account_info_iter)?;
    let initializer_account = next_account_info(account_info_iter)?;
    let token_mint_account = next_account_info(account_info_iter)?;
    let treasury_pda_account = next_account_info(account_info_iter)?;
    let treasury_token_account = next_account_info(account_info_iter)?;
    let reward_token_mint_account = next_account_info(account_info_iter)?;
    let pool_fee_account = next_account_info(account_info_iter)?;
    let system_program = next_account_info(account_info_iter)?;
    let token_program = next_account_info(account_info_iter)?;
    let rent = &Rent::from_account_info(next_account_info(account_info_iter)?)?;
    let clock = Clock::get()?;

    let mut staking_pool_state = StakingPoolState::try_from_slice(&staking_pool_account.data.borrow())?;
    if staking_pool_state.is_initialized {
        return Err(ProgramError::AccountAlreadyInitialized);
    }

    let create_staking_pool_account_instruction = system_instruction::create_account(
        initializer_account.key,
        staking_pool_account.key,
        rent.minimum_balance(StakingPoolState::LEN),
        StakingPoolState::LEN as u64,
        program_id,
    );


    let seeds = &[initializer_account.key.as_ref()];

    invoke_signed(
        &create_staking_pool_account_instruction,
        &[
            initializer_account.clone(),
            staking_pool_account.clone(),
            system_program.clone(),
        ],
        &[seeds],
    )?;

    let transfer_tokens_instruction = spl_token::instruction::transfer(
        token_program.key,
        treasury_token_account.key,
        initializer_account.key,
        staking_pool_account.key,
        &[&staking_pool_account.key],
        data.staking_pool_token_amount,
    )?;
    invoke_signed(
        &transfer_tokens_instruction,
        &[
            treasury_token_account.clone(),
            initializer_account.clone(),
            staking_pool_account.clone(),
            token_program.clone(),
        ],
        &[seeds],
    )?;

    let create_token_account_instruction = spl_token::instruction::initialize_account(
        token_program.key,
        treasury_token_account.key,
        token_mint_account.key,
        treasury_pda_account.key,
    )?;


    invoke_signed(
        &create_token_account_instruction,
        &[
            treasury_token_account.clone(),
            token_mint_account.clone(),
            treasury_pda_account    .clone(),
            token_program.clone(),           
        ],
        &[seeds],
    )?;

    Ok(())
}

pub fn create_staker_account(
    program_id: &Pubkey,
    accounts: &[AccountInfo],
    data: CreateStakerAccountData,
) -> ProgramResult{
    let account_info_iter = &mut accounts.iter();
    let staker_account = next_account_info(account_info_iter)?;
    let staking_pool_account = next_account_info(account_info_iter)?;
    let user_account = next_account_info(account_info_iter)?;
    let user_token_account = next_account_info(account_info_iter)?;
    let user_token_account_owner = next_account_info(account_info_iter)?;
    let system_program = next_account_info(account_info_iter)?;
    let token_program = next_account_info(account_info_iter)?;
    let rent = &Rent::from_account_info(next_account_info(account_info_iter)?)?;
    let clock = Clock::get()?;

    let mut staking_pool_state = StakingPoolState::try_from_slice(&staking_pool_account.data.borrow())?;
    if !staking_pool_state.is_initialized {
        return Err(ProgramError::UninitializedAccount);
    }

    if staking_pool_state.total_staked_amount >= staking_pool_state.max_staking_capacity {
        return Err(ProgramError::AccountDataTooSmall);
    }

    let mut user_state = UserState::try_from_slice(&user_account.data.borrow())?;
    if user_state.initializer != Pubkey::default() {
        return Err(ProgramError::AccountAlreadyInitialized);
    }

    // Transfer the user's tokens to the staking pool
    let transfer_tokens_instruction = spl_token::instruction::transfer(
        token_program.key,
        user_token_account.key,
        user_token_account_owner.key,
        staking_pool_account.key,
        &[&staking_pool_account.key],
        data.total_staked_amount,
    )?;

    let seeds = &[user_account.key.as_ref()];

    invoke_signed(
        &transfer_tokens_instruction,
        &[
            user_token_account.clone(),
            user_token_account_owner.clone(),
            staking_pool_account.clone(),
            token_program.clone(),
        ],
        &[seeds],
    )?;

    // Create the user's staker account
    let create_staker_account_instruction = system_instruction::create_account(
        user_account.key,
        staker_account.key,
        rent.minimum_balance(UserState::LEN),
        UserState::LEN as u64,
        program_id,
    );
    invoke_signed(
        &create_staker_account_instruction,
        &[
            user_account.clone(),
            staker_account.clone(),
            system_program.clone(),
        ],
        &[seeds],
    )?;

    user_state.initializer = *program_id;
    user_state.pool_state_account = *staking_pool_account.key;

    Ok(())
}


pub fn stake_tokens(
    program_id: &Pubkey,
    accounts: &[AccountInfo],
    data: StakeTokensData,
) -> ProgramResult{
    let account_info_iter = &mut accounts.iter();
    let staker_account = next_account_info(account_info_iter)?;
    let staking_pool_account = next_account_info(account_info_iter)?;
    let user_account = next_account_info(account_info_iter)?;
    let user_token_account = next_account_info(account_info_iter)?;
    let user_token_account_owner = next_account_info(account_info_iter)?;
    let system_program = next_account_info(account_info_iter)?;
    let token_program = next_account_info(account_info_iter)?;
    let rent = &Rent::from_account_info(next_account_info(account_info_iter)?)?;
    let clock = Clock::get()?;

    let mut staking_pool_state = StakingPoolState::try_from_slice(&staking_pool_account.data.borrow())?;
    if !staking_pool_state.is_initialized {
        return Err(ProgramError::UninitializedAccount);
    }

    if staking_pool_state.total_staked_amount >= staking_pool_state.max_staking_capacity {
        return Err(ProgramError::AccountDataTooSmall);
    }

    let mut user_state = UserState::try_from_slice(&user_account.data.borrow())?;
    if user_state.initializer != Pubkey::default() {
        return Err(ProgramError::AccountAlreadyInitialized);
    }

    let transfer_tokens_instruction = spl_token::instruction::transfer(
        token_program.key,
        user_token_account.key,
        user_token_account_owner.key,
        staking_pool_account.key,
        &[&staking_pool_account.key],
        data.total_staked_amount,
    )?;

    let seeds = &[user_account.key.as_ref()];

    invoke_signed(
        &transfer_tokens_instruction,
        &[
            user_token_account.clone(),
            user_token_account_owner.clone(),
            staking_pool_account.clone(),
            token_program.clone(),
        ],
        &[seeds],
    )?;

    let create_staker_account_instruction = system_instruction::create_account(
        user_account.key,
        staker_account.key,
        rent.minimum_balance(UserState::LEN),
        UserState::LEN as u64,
        program_id,
    );
    invoke_signed(
        &create_staker_account_instruction,
        &[
            user_account.clone(),
            staker_account.clone(),
            token_program.clone(),
        ],  
        &[seeds],   
    )?; 

    user_state.initializer = *program_id;
    user_state.pool_state_account = *staking_pool_account.key;


    Ok(())
}

use spl_token::instruction::transfer;
use solana_program::program::invoke;

pub fn unstake_tokens(
    program_id: &Pubkey,
    accounts: &[AccountInfo],
    data: UnstakeTokensData,
) -> ProgramResult{
    let account_info_iter = &mut accounts.iter();
    let user_account = next_account_info(account_info_iter)?;
    let staking_pool_account = next_account_info(account_info_iter)?;
    let token_program = next_account_info(account_info_iter)?;

    if program_id != user_account.owner {
        return Err(ProgramError::IncorrectProgramId);
    }

    let transfer_tokens_instruction = transfer(
        token_program.key,
        staking_pool_account.key,
        user_account.key,
        user_account.owner,
        &[],
        data.amount.try_into().unwrap(),
    )?;

    invoke(
        &transfer_tokens_instruction,
        &[
            staking_pool_account.clone(),
            user_account.clone(),
            token_program.clone(),
        ],
    )?;

    let mut user_state = UserState::unpack_from_slice(&user_account.data.borrow())?;
    if user_state.staked_token_amount < data.amount.into() {
        return Err(ProgramError::InsufficientFunds);
    }
    
    user_state.staked_token_amount -= data.amount;
    user_state.last_interaction_time = Clock::get()?.unix_timestamp as u128;

    UserState::pack_into_slice(&user_state, &mut user_account.data.borrow_mut());
    Ok(())
}


pub fn close_staker_account(
    program_id: &Pubkey,
    accounts: &[AccountInfo],
    data: CloseStakerAccountData,
) -> ProgramResult {
    let account_info_iter = &mut accounts.iter();
    let user_account = next_account_info(account_info_iter)?;
    let staking_pool_account = next_account_info(account_info_iter)?;

    if user_account.owner != program_id {
        return Err(ProgramError::IncorrectProgramId);
    }

    if user_account.lamports() == 0 {
        return Err(ProgramError::InvalidAccountData);
    }

    **staking_pool_account.try_borrow_mut_lamports()? += user_account.lamports();
    **user_account.try_borrow_mut_lamports()? = 0;

    Ok(())
}