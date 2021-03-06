package com.jayway.restfuljersey.samples.bank.grove.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jayway.forest.constraint.Constraint;
import com.jayway.forest.constraint.ConstraintEvaluator;
import com.jayway.forest.grove.RoleManager;
import com.jayway.forest.roles.Resource;
import com.jayway.restfuljersey.samples.bank.model.Account;
import com.jayway.restfuljersey.samples.bank.model.Withdrawable;

/**
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(IsWithdrawable.Evaluator.class)
public @interface IsWithdrawable {

    class Evaluator implements ConstraintEvaluator<IsWithdrawable, Resource> {

        public boolean isValid( IsWithdrawable role, Resource resource ) {
            return RoleManager.role(Account.class) instanceof Withdrawable;
        }

    }

}
