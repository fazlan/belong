package au.com.belong.customerphoneapi.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "PHONE_NUMBERS", schema = "PUBLIC")
public class PhoneNumber {
    @Id
    @Column("id")
    private long id;
    @Column("phone_number")
    private String phoneNumber;
    @Column("is_active")
    private boolean isActive;
    @Column("customer_id")
    private long customerId;
}
