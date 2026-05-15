package com.librosphere.lending.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class LendingUtils {
    public static BigDecimal calculatePenalty(LocalDateTime dueDate, LocalDateTime returnDate, BigDecimal dailyRate) {
        if (returnDate.isAfter(dueDate)) {
            long daysOverdue = ChronoUnit.DAYS.between(dueDate, returnDate);
            return daysOverdue > 0 ? dailyRate.multiply(BigDecimal.valueOf(daysOverdue)) : BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }
}
