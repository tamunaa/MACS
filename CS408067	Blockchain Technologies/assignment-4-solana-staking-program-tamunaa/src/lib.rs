mod instructions;
use instructions::create_staker_account;
use solana_program::{
    account_info::{AccountInfo, next_account_info},
    entrypoint,
    entrypoint::ProgramResult,
    pubkey::Pubkey, msg, program_error::ProgramError
};
use crate::instructions::{
    initialize_staking_pool,
    stake_tokens,
    unstake_tokens,
    close_staker_account,
};

use borsh::{BorshDeserialize, BorshSerialize};


#[derive(BorshSerialize, BorshDeserialize)]
pub struct StakingPoolState {
    pub is_initialized: bool,
    pub token_mint: Pubkey,
    pub treasury_token_account: Pubkey,
    pub reward_rate: u64,
    pub max_staking_capacity: u128,
    pub total_staked_amount: u128,
    pub total_rewards_paid: u128,
    pub pool_fee_account: Pubkey,
    pub pool_fee_percent: u8,
    pub pool_fee_amount: u128,
    pub last_update_time: u64,
    pub staker_count: u64,
}
use solana_program::program_pack::Sealed;

impl Sealed for StakingPoolState {}
use solana_program::program_pack::Pack;

impl Pack for StakingPoolState {
    const LEN: usize = 186; 

    fn pack_into_slice(&self, output: &mut [u8]) {
        let encoded = self.try_to_vec().unwrap();
        output[..encoded.len()].copy_from_slice(&encoded);
    }

    fn unpack_from_slice(input: &[u8]) -> Result<Self, ProgramError> {
        let staking_pool_state = Self::try_from_slice(input).map_err(|_| ProgramError::InvalidAccountData)?;
        Ok(staking_pool_state)
    }
}

#[derive(BorshSerialize, BorshDeserialize)]
pub struct UserState {
    pub initializer: Pubkey, 
    pub pool_state_account: Pubkey, 
    pub staked_token_amount: u128,
    pub rewards_per_token_paid: u128, 
    pub rewards: u128, 
    pub last_interaction_time: u128,
}
impl Sealed for UserState {}

impl Pack for UserState {
    const LEN: usize = 128; 

    fn pack_into_slice(&self, output: &mut [u8]) {
        let encoded = self.try_to_vec().unwrap();
        output[..encoded.len()].copy_from_slice(&encoded);
    }

    fn unpack_from_slice(input: &[u8]) -> Result<Self, ProgramError> {
        let user_state = Self::try_from_slice(input).map_err(|_| ProgramError::InvalidAccountData)?;
        Ok(user_state)
    }
}

entrypoint!(process_staking_instruction);

fn process_staking_instruction(
    program_id: &Pubkey,
    accounts: &[AccountInfo],
    instruction_data: &[u8],
) -> ProgramResult {
    let staking_command = StakingCommand::try_from_slice(instruction_data)?;

    match staking_command {
        StakingCommand::InitializeStakingPool(data) => initialize_staking_pool(program_id, accounts, data),
        StakingCommand::CreateStakerAccount(data) => create_staker_account(program_id, accounts, data),
        StakingCommand::StakeTokens(data) => stake_tokens(program_id, accounts, data),
        StakingCommand::UnstakeTokens(data) => unstake_tokens(program_id, accounts, data),
        // StakingCommand::ClaimRewards(data) => claim_rewards(program_id, accounts, data),
        StakingCommand::CloseStakerAccount(data) => close_staker_account(program_id, accounts, data),
    }
}

#[derive(BorshSerialize, BorshDeserialize)]
pub enum StakingCommand {
    InitializeStakingPool(InitializeStakingPoolData),
    CreateStakerAccount(CreateStakerAccountData),
    StakeTokens(StakeTokensData),
    UnstakeTokens(UnstakeTokensData),
    // ClaimRewards(ClaimRewardsData),
    CloseStakerAccount(CloseStakerAccountData)
}


#[derive(BorshSerialize, BorshDeserialize)]
pub struct InitializeStakingPoolData {
    pub token_mint: Pubkey,
    pub treasury_token_account: Pubkey,
    pub reward_rate: u64,
    pub max_staking_capacity: u128,
    pub staking_pool_token_amount : u64,
}

#[derive(BorshDeserialize, BorshSerialize)]
pub struct LaunchStakingPoolData {
    pub token_mint: Pubkey,
    pub treasury_pda: Pubkey,
    pub treasury_token_account: Pubkey,
    pub reward_rate: u128,
    pub max_staking_capacity: u128,
}

#[derive(BorshSerialize, BorshDeserialize)]
pub struct CreateStakerAccountData {
    pub staking_pool: Pubkey,
    pub staker: Pubkey,
    pub total_staked_amount: u64,
}

#[derive(BorshSerialize, BorshDeserialize)]
pub struct StakeTokensData {
    pub amount: u128,
    pub total_staked_amount: u64,
}

#[derive(BorshSerialize, BorshDeserialize)]
pub struct UnstakeTokensData {
    pub amount: u128,
}

#[derive(BorshSerialize, BorshDeserialize)]
pub struct ClaimRewardsData {

}

#[derive(BorshSerialize, BorshDeserialize)]
pub struct CloseStakerAccountData {

}