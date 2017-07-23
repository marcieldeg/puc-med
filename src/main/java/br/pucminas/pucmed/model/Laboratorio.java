package br.pucminas.pucmed.model;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Laboratorio extends Usuario {

}