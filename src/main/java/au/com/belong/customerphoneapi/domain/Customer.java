package au.com.belong.customerphoneapi.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "CUSTOMERS", schema = "PUBLIC")
public class Customer {
    @Id
    private long id;
    private String name;
}
