package com.jayway.restfuljersey.samples.bank.grove.resources.accounts;

import static com.jayway.forest.grove.RoleManager.role;

import com.jayway.forest.roles.DescribedResource;
import com.jayway.forest.roles.Resource;
import com.jayway.restfuljersey.samples.bank.dto.TransferToDTO;
import com.jayway.restfuljersey.samples.bank.grove.constraints.DepositAllowed;
import com.jayway.restfuljersey.samples.bank.grove.constraints.HasCredit;
import com.jayway.restfuljersey.samples.bank.grove.constraints.IsWithdrawable;
import com.jayway.restfuljersey.samples.bank.model.Account;
import com.jayway.restfuljersey.samples.bank.model.AccountManager;
import com.jayway.restfuljersey.samples.bank.model.Depositable;
import com.jayway.restfuljersey.samples.bank.model.Withdrawable;
import com.jayway.restfuljersey.samples.bank.repository.AccountRepository;

public class AccountResource implements Resource, DescribedResource {

    public void allowexceeddepositlimit( Boolean allow ) {
    	role(Account.class).setAllowExceedBalanceLimit(allow);
    }

    @DepositAllowed
    public void deposit( Integer amount ) {
        role(AccountManager.class).deposit((Depositable) role(Account.class), amount);
    }

    @HasCredit
    @IsWithdrawable
    public void withdraw( Integer amount ) {
        role(AccountManager.class).withdraw((Withdrawable) role(Account.class), amount);
    }

    @HasCredit
    @IsWithdrawable
    public void transfer( TransferToDTO transfer ) {
        Depositable depositable = role(AccountRepository.class).findWithRole(transfer.getDestinationAccount(), Depositable.class);
        Withdrawable withdrawable = (Withdrawable) role(Account.class);

        role(AccountManager.class).transfer(withdrawable, depositable, transfer.getAmount() );
    }


    @Override
    public Object description() {
        Account account = role(Account.class);
        return String.format( Account.HTML_DESCRIPTION, account.getAccountNumber(), account.getBalance(), account.isAllowExceedBalanceLimit() );
    }
}
