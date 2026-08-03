package org.example.userstories.repository;

import org.example.userstories.model.PaymentStatus;

public interface PaymentByStatus {

    Long getCount();

    PaymentStatus getStatus();
}
