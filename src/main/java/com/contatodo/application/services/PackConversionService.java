package com.contatodo.application.services;

import com.contatodo.shared.constants.ResponseConstants;
import com.contatodo.shared.constants.ValidationConstants;
import com.contatodo.shared.exceptions.InvalidRequestException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Converts pack quantities into base units of measure.
 *
 * <p>Centralises the conversion shared by acquisitions and sales so a pack
 * quantity is always multiplied by the product pack size in the same way and
 * inventory is only ever expressed in base units.</p>
 */
@Component
public class PackConversionService {

    /**
     * Converts a quantity expressed in the selected unit into base units.
     *
     * <p>When the unit does not represent a pack the quantity is returned
     * unchanged. When it does, the quantity is multiplied by the pack size.</p>
     *
     * @param quantity Quantity registered by the caller.
     * @param isPack Whether the selected unit of measure represents a pack.
     * @param packSize Number of base units contained in one pack.
     * @param packSizeErrorMessage Error used when a pack quantity has no pack size.
     * @return Quantity expressed in base units.
     * @throws InvalidRequestException when a pack quantity cannot be converted.
     */
    public int toBaseUnits(Integer quantity, boolean isPack, Integer packSize, String packSizeErrorMessage) {
        if (quantity == null || quantity <= 0) {
            throw new InvalidRequestException(
                    ResponseConstants.VALIDATION_ERROR_MESSAGE,
                    List.of(ValidationConstants.FIELD_MUST_BE_POSITIVE)
            );
        }
        if (!isPack) {
            return quantity;
        }
        if (packSize == null || packSize <= 0) {
            throw new InvalidRequestException(
                    ResponseConstants.VALIDATION_ERROR_MESSAGE,
                    List.of(packSizeErrorMessage)
            );
        }
        long converted = (long) quantity * packSize;
        if (converted > Integer.MAX_VALUE) {
            throw new InvalidRequestException(
                    ResponseConstants.VALIDATION_ERROR_MESSAGE,
                    List.of(packSizeErrorMessage)
            );
        }
        return (int) converted;
    }
}
