package br.ufc.quixada.dadm.variastelas.transactions;

import java.util.Arrays;

public class Agenda {

    String nome;
    int id;
    int idade;
    Contato[] listaTelefone;

    public Agenda(String nome, int id, Contato[] listaTelefone, int idade) {
        super();
        this.nome = nome;
        this.id = id;
        this.listaTelefone = listaTelefone;
        this.idade = idade;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getIdade() {
        return idade;
    }
    public Contato[] getListaTelefone() {
        return listaTelefone;
    }

    public void setListaTelefone(Contato[] listaTelefone) {
        this.listaTelefone = listaTelefone;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    @Override
    public String toString() {
        return "Message_ToString_[nome=" + nome + ", id=" + id + ", listaTelefone="
                + Arrays.toString(listaTelefone) + ", idade=" + idade + "]";
    }



}
