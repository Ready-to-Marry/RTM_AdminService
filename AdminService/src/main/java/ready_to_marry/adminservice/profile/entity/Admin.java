package ready_to_marry.adminservice.profile.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admin", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "department", "phone"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long adminId;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "department", nullable = false, length = 20)
    private String department;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;
}
