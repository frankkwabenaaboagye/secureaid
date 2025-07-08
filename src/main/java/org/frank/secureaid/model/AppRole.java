package org.frank.secureaid.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import static jakarta.persistence.EnumType.STRING;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@Entity
@Table(name = "app_roles")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AppRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(STRING)
    @Column(name = "role_name", length = 50, nullable = false, unique = true)
    private RoleName roleName;

    public enum RoleName {
        ADMIN,
        STAFF,
        AUDITOR;

        public String getSpringSecurityRole(){
            return "ROLE_" + this.name();
        }
    }
}
