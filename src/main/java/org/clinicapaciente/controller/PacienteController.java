package org.clinicapaciente.controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.clinicapaciente.dao.PacienteDAO;
import org.clinicapaciente.model.Paciente;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class PacienteController implements Initializable {

    @FXML private TextField txtID;
    @FXML private TextField txtNome;
    @FXML private TextField txtCpf;
    @FXML private DatePicker txtDataNascimento;
    @FXML private TextField txtTelefone;
    @FXML private TextField txtPesquisar;

    @FXML private TableView<Paciente> tabelaDados;

    @FXML private TableColumn<Paciente, Integer> colID;
    @FXML private TableColumn<Paciente, String> colNome;
    @FXML private TableColumn<Paciente, String> colCpf;
    @FXML private TableColumn<Paciente, String> colTelefone;
    @FXML private TableColumn<Paciente, LocalDate> colDataNascimento;

    private PacienteDAO pacienteDAO = new PacienteDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        vinculoComTabela();
        atualizarTableView();
    }

    public void vinculoComTabela() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        colDataNascimento.setCellValueFactory(new PropertyValueFactory<>("dataNascimento"));
    }

    public void atualizarTableView() {
        List<Paciente> pacientes = pacienteDAO.listarTodos();
        ObservableList<Paciente> lista = FXCollections.observableArrayList(pacientes);
        tabelaDados.setItems(lista);
    }

    public Paciente lerFormulario() {
        Paciente p = new Paciente();
        p.setNome(txtNome.getText());
        p.setCpf(txtCpf.getText());
        p.setTelefone(txtTelefone.getText());
        p.setDataNascimento(txtDataNascimento.getValue());
        return p;
    }

    @FXML
    protected void onCadastrarClick() {
        Paciente novo = lerFormulario();

        if (novo.getNome().isEmpty() || novo.getCpf().isEmpty()) {
            System.out.println("Nome e CPF são obrigatórios!");
            return;
        }

        pacienteDAO.salvar(novo);

        limparCampos();
        atualizarTableView();
    }

    @FXML
    protected void onListPacinteClick() {
        String nomeBuscado = txtPesquisar.getText();

        if (nomeBuscado == null || nomeBuscado.trim().isEmpty()) {
            atualizarTableView();
            return;
        }

        List<Paciente> filtrados = pacienteDAO.buscarPorNome(nomeBuscado.trim());
        tabelaDados.setItems(FXCollections.observableArrayList(filtrados));
    }

    @FXML
    protected void onAtualizarPacienteClick() {
        if (txtID.getText().isEmpty()) {
            System.out.println("Informe o ID!");
            return;
        }

        int id = Integer.parseInt(txtID.getText());

        Paciente existente = pacienteDAO.buscarPorId(id);

        if (existente == null) {
            System.out.println("Paciente não encontrado!");
            return;
        }

        Paciente dadosAtualizados = lerFormulario();

        existente.setNome(dadosAtualizados.getNome());
        existente.setCpf(dadosAtualizados.getCpf());
        existente.setTelefone(dadosAtualizados.getTelefone());
        existente.setDataNascimento(dadosAtualizados.getDataNascimento());

        pacienteDAO.atualizar(existente);

        limparCampos();
        atualizarTableView();
    }

    @FXML
    protected void onExcluirPacienteClick() {
        if (txtID.getText().isEmpty()) {
            System.out.println("Informe o ID!");
            return;
        }

        int id = Integer.parseInt(txtID.getText());

        pacienteDAO.excluir(id);

        limparCampos();
        atualizarTableView();
    }

    private void limparCampos() {
        txtID.clear();
        txtNome.clear();
        txtCpf.clear();
        txtTelefone.clear();
        txtPesquisar.clear();
        txtDataNascimento.setValue(null);
    }
}
