package com.yenzaga.msuser.validator;

import org.passay.CharacterData;
import org.passay.*;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

  @Override
  public void initialize(ValidPassword constraintAnnotation) {
  }

  @Override
  public boolean isValid(String password, ConstraintValidatorContext context) {
    PasswordValidator validator = new PasswordValidator(Arrays.asList(
        new LengthRule(6, 36),
        new CharacterRule(EnglishCharacterData.UpperCase, 1),
        new CharacterRule(EnglishCharacterData.LowerCase, 1),
        new CharacterRule(EnglishCharacterData.Digit, 1),
        new CharacterRule(new CharacterData() {
          @Override
          public String getErrorCode() {
            return "INSUFFICIENT_SPECIAL_CHARACTERS";
          }

          @Override
          public String getCharacters() {
            return "@!#$%&";
          }
        }, 1)
    ));
    RuleResult result = validator.validate(new PasswordData(password));
    if(result.isValid()) {
      return true;
    }

    List<String> messages = validator.getMessages(result);
    List<String> shortenedMessages = new ArrayList<>();
    for (String msg: messages) {
      String[] msgArray = msg.split(":");
      if(msgArray.length > 1) {
        String key = msgArray[0].replace("_", " ");
        shortenedMessages.add(key);
      }
    }
    //String messageTemplate = messages.stream().collect(Collectors.joining(", "));
    String messageTemplate = String.join(", ", shortenedMessages);
    context.buildConstraintViolationWithTemplate(messageTemplate)
        .addConstraintViolation()
        .disableDefaultConstraintViolation();
    return false;
  }
}
