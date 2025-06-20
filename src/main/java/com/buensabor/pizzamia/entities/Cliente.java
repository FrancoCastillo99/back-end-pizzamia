package com.buensabor.pizzamia.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String nombre;
    @NotNull
    private String apellido;
    @NotNull
    private Integer telefono;

    @NotNull
    @Email
    private String email;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "rol_id")
    private Rol rol;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private Usuario user;


    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "cliente_domicilio",
            joinColumns = @JoinColumn(name = "cliente_id"),
            inverseJoinColumns = @JoinColumn(name = "domicilio_id"))
    private Set<Domicilio> domicilios = new HashSet<>();

    @NotNull
    private LocalDateTime fechaAlta;

    private LocalDateTime fechaBaja;

    @PrePersist
    public void prePersist() {
        this.fechaAlta = LocalDateTime.now();
    }
}
