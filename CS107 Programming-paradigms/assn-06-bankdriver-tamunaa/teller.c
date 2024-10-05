#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <assert.h>
#include <inttypes.h>

#include "teller.h"
#include "account.h"
#include "error.h"
#include "debug.h"

#include "bank.h"
#include "branch.h"
/*
 * deposit money into an account
 */
int
Teller_DoDeposit(Bank *bank, AccountNumber accountNum, AccountAmount amount){
  assert(amount >= 0);


  DPRINTF('t', ("Teller_DoDeposit(account 0x%"PRIx64" amount %"PRId64")\n",
                accountNum, amount));

  Account *account = Account_LookupByNumber(bank, accountNum);
  sem_wait(&(account->acc_lock));
  auto id = AccountNum_GetBranchID(accountNum);
  sem_wait(&(bank->branches[id].branch_lock));

  if (account == NULL) {
    sem_post(&account->acc_lock);
    sem_post(&(bank->branches[id].branch_lock));
    return ERROR_ACCOUNT_NOT_FOUND;
  }


  Account_Adjust(bank,account, amount, 1);
  sem_post(&account->acc_lock);
  sem_post(&(bank->branches[id].branch_lock));
  return ERROR_SUCCESS;
}

/*
 * withdraw money from an account
 */
int Teller_DoWithdraw(Bank *bank, AccountNumber accountNum, AccountAmount amount){
 
  assert(amount >= 0);

  DPRINTF('t', ("Teller_DoWithdraw(account 0x%"PRIx64" amount %"PRId64")\n",
                accountNum, amount));

  Account *account = Account_LookupByNumber(bank, accountNum);

  sem_wait(&(account->acc_lock));
  auto id = AccountNum_GetBranchID(accountNum);
  sem_wait(&(bank->branches[id].branch_lock));  

  if (account == NULL) {
    sem_post(&(account->acc_lock));
    sem_post(&(bank->branches[id].branch_lock));
    return ERROR_ACCOUNT_NOT_FOUND;
  }

  if (amount > Account_Balance(account)) {
    sem_post (&(account->acc_lock));
    sem_post(&(bank->branches[id].branch_lock));

    return ERROR_INSUFFICIENT_FUNDS;
  }

  Account_Adjust(bank,account, -amount, 1);
  sem_post(&account->acc_lock);
  sem_post(&(bank->branches[id].branch_lock)); 
  return ERROR_SUCCESS;

}

void wait_in_order(Account* srcAccount, Account* dstAccount, 
                  AccountNumber srcAccountNum, AccountNumber dstAccountNum){

  if (srcAccountNum < dstAccountNum) {
    sem_wait(&srcAccount->acc_lock);
    sem_wait(&dstAccount->acc_lock);
  } else {
    sem_wait(&dstAccount->acc_lock);
    sem_wait(&srcAccount->acc_lock);
  }
}

void lock_branches(Bank *bank, BranchID srcId, BranchID dstId, 
                  AccountNumber srcAccountNum,AccountNumber dstAccountNum){
  if (srcAccountNum < dstAccountNum) {
    sem_wait(&(bank->branches[srcId].branch_lock)); 
    sem_wait(&(bank->branches[dstId].branch_lock)); 
  } else {
    sem_wait(&(bank->branches[dstId].branch_lock)); 
    sem_wait(&(bank->branches[srcId].branch_lock)); 
  }
}

int Teller_DoTransfer(Bank *bank, AccountNumber srcAccountNum,
                  AccountNumber dstAccountNum,
                  AccountAmount amount){
  assert(amount >= 0);

  DPRINTF('t', ("Teller_DoTransfer(src 0x%"PRIx64", dst 0x%"PRIx64
                ", amount %"PRId64")\n",
                srcAccountNum, dstAccountNum, amount));

  Account *srcAccount = Account_LookupByNumber(bank, srcAccountNum);
  Account *dstAccount = Account_LookupByNumber(bank, dstAccountNum);

  if (srcAccount == NULL || dstAccount == NULL) {
    return ERROR_ACCOUNT_NOT_FOUND;
  }

  if (srcAccountNum == dstAccountNum) return ERROR_SUCCESS;
  int same = !Account_IsSameBranch(srcAccountNum, dstAccountNum);

  wait_in_order(srcAccount, dstAccount, srcAccountNum, dstAccountNum);
  
  if (!same) {
    if (amount > Account_Balance(srcAccount)) {
      sem_post(&srcAccount->acc_lock);
      sem_post(&dstAccount->acc_lock);
      return ERROR_INSUFFICIENT_FUNDS;
    }

    
    Account_Adjust(bank, dstAccount, amount, same);
    Account_Adjust(bank, srcAccount, -amount, same);

    sem_post(&dstAccount->acc_lock);
    sem_post(&srcAccount->acc_lock);
  } else {
    auto srcId = AccountNum_GetBranchID(srcAccountNum);
    auto dstId = AccountNum_GetBranchID(dstAccountNum);

    lock_branches(bank, srcId, dstId, srcAccountNum, dstAccountNum);

    if (amount > Account_Balance(srcAccount)) {
      sem_post(&srcAccount->acc_lock);
      sem_post(&dstAccount->acc_lock);

      sem_post(&(bank->branches[dstId].branch_lock)); 
      sem_post(&(bank->branches[srcId].branch_lock)); 
      return ERROR_INSUFFICIENT_FUNDS;
    }

    sem_post(&srcAccount->acc_lock);
    sem_post(&dstAccount->acc_lock);

    sem_post(&(bank->branches[dstId].branch_lock)); 
    sem_post(&(bank->branches[srcId].branch_lock)); 
  }

  return ERROR_SUCCESS;
}