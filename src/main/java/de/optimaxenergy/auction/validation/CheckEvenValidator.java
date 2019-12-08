package de.optimaxenergy.auction.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckEvenValidator implements ConstraintValidator<Even, Integer> {
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return (value % 2) == 0;
    }
}
