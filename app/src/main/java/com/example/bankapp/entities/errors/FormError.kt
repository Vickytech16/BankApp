package com.example.bankapp.entities.errors

import com.example.bankapp.R

sealed class FormError(override val message: Int): UiError {

    object InvalidData:
            FormError(R.string.enter_valid_data_error)

    class EmptyData(val fieldNameRes: Int):
            FormError(R.string.empty_field_error)

    class TooLongData(val fieldNameRes: Int, val characterLimitRes: Int):
            FormError(R.string.data_too_long_error)

    class LimitExceeded(val data: String):
            FormError(R.string.limit_exceeded_error)

    object UserNameCannotStartWithNumber:
            FormError(R.string.username_cannot_start_with_number)


    class LimitExceededDeposit(val data: String):
        FormError(R.string.limit_exceeded_deposit_error)

    class TooShortData(val fieldNameRes: Int, val characterLimitRes: Int):
        FormError(R.string.data_too_short_error)

    class BeneficiaryLimitReached(val limit: Int):
        FormError(R.string.beneficary_limit_reached)

    object InvalidEmailFormat:
            FormError(R.string.invalid_email_format_error)

    class InvalidNumericalFIeld(val fieldNameRes: Int):
            FormError(R.string.invalid_numerical_field_error)

    object InvalidAmount:
            FormError(R.string.amount_data_only)

    object InvalidUsername:
            FormError(R.string.invalid_username_error)

    class UserAlreadyExists(val fieldNameRes: Int):
            FormError(R.string.user_already_exists_error)

    object YouAreTheUser:
            FormError(R.string.your_own_details_error)

    object AlreadyYourFriendError:
            FormError(R.string.already_your_friend)


    object InvalidCredentials:
            FormError(R.string.invalid_credentials)

    object PasswordDoesntMatch:
            FormError(R.string.password_doesnt_match)

    object NegativeAmount:
            FormError(R.string.amount_negative_error)

    object AllFieldsAreRequired:
            FormError(R.string.all_fields_required_error)

    object UnknownError:
            FormError(R.string.something_wrong_happened)

    object OtpDoesNotMatch:
            FormError(R.string.otp_doesnt_match)

    object OtpExpired:
            FormError(R.string.otp_expired)

    object SamePassword:
            FormError(R.string.same_password)

    object InvalidDepositAmountFormat:
            FormError(R.string.invalid_deposit_amount_format_error)

    object InvalidCashTransferAmountFormat:
            FormError(R.string.invalid_cash_transfer_amount_format_error)

    object UserDoesNotHaveAccountError:
            FormError(R.string.user_doesnt_have_account_error)

    object InvalidPhoneNumber:
            FormError(R.string.invalid_phone_number)

    object RecoveryKeyMustBe10DigitsLong:
            FormError(R.string.recovery_key_expectaton)

    object RecoveryKeyDoesNotMatch:
            FormError(R.string.recovery_key_no_match)


}