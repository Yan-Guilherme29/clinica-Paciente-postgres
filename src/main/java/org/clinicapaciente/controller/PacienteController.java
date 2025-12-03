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
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class PacienteController implements Initializable {
    @FXML
    private TextField txtID;
    @FXML
    private TextField txtNome;
    @FXML
    private TextField txtCpf;
    @FXML
    private DatePicker txtDataNascimento;
    @FXML
    private TextField txtTelefone;
    @FXML
    private TextField txtPesquisar; // <-- CAMPO CERTO PARA A BUSCA

    @FXML
    private TableView<Paciente> tabelaDados;

    @FXML
    private TableColumn<Paciente, Integer> colID;
    @FXML
    private TableColumn<Paciente, String> colNome;
    @FXML
    private TableColumn<Paciente, String> colCpf;
    @FXML
    private TableColumn<Paciente, String> colTelefone;
    @FXML
    private TableColumn<Paciente, LocalDate> colDataNascimento;

    private PacienteDAO pacienteDAO;
    private ObservableList<Paciente> observableListPacientes;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.pacienteDAO = new PacienteDAO();
        this.observableListPacientes = FXCollections.observableArrayList();
        this.tabelaDados.setItems(this.observableListPacientes);

        vinculoComTabela();
        carregarTodosPacientes();
    }

    public void vinculoComTabela() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colDataNascimento.setCellValueFactory(new PropertyValueFactory<>("dataNascimento"));
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
    }

    private void carregarTodosPacientes() {
        try {
            List<Paciente> pacientesDoBanco = pacienteDAO.listarTodos();
            this.observableListPacientes.clear();
            this.observableListPacientes.addAll(pacientesDoBanco);
        } catch (SQLException e) {
            e.printStackTrace();
            exibirAlerta(Alert.AlertType.ERROR, "Erro de Banco de Dados", "Falha ao carregar pacientes: " + e.getMessage());
        }
    }

    private Paciente getPacienteFromForm() {
        int id = txtID.getText().isEmpty() ? 0 : Integer.parseInt(txtID.getText());

        return new Paciente(
                id,
                txtNome.getText(),
                txtCpf.getText(),
                txtDataNascimento.getValue(),
                txtTelefone.getText()
        );
    }

    @FXML
    protected void onCadastrarClick() {
        Paciente novoPaciente = getPacienteFromForm();

        try {
            pacienteDAO.salvar(novoPaciente);

            limparFormulario();
            exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Paciente salvo no banco com sucesso!");

        } catch (SQLException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro ao Salvar", "Falha ao salvar paciente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 🔥 CORRIGIDO AQUI — agora usa txtPesquisar
    @FXML
    protected void onListPacienteClick() {
        String nomeBuscado = txtPesquisar.getText(); // <-- CORRETO!

        try {
            List<Paciente> pacientesFiltrados = pacienteDAO.buscarPorNome(nomeBuscado);

            this.observableListPacientes.clear();
            this.observableListPacientes.addAll(pacientesFiltrados);

            if (pacientesFiltrados.isEmpty() && !nomeBuscado.trim().isEmpty()) {
                exibirAlerta(Alert.AlertType.INFORMATION,
                        "Busca Vazia",
                        "Nenhum paciente encontrado com o nome: " + nomeBuscado);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            exibirAlerta(Alert.AlertType.ERROR,
                    "Erro de Busca",
                    "Falha ao buscar pacientes: " + e.getMessage());
        }
    }

    @FXML
    protected void onAtualizarPacienteClick() {
        String idText = txtID.getText();
        if (idText.isEmpty()) {
            exibirAlerta(Alert.AlertType.WARNING, "ID Inválido", "Por favor, digite o ID do paciente a ser atualizado.");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idText);
            if (id <= 0) {
                exibirAlerta(Alert.AlertType.WARNING, "ID Inválido", "O ID deve ser um número positivo.");
                return;
            }
        } catch (NumberFormatException e) {
            exibirAlerta(Alert.AlertType.ERROR, "ID Inválido", "O ID deve ser um número inteiro.");
            return;
        }

        Paciente pacienteAtualizado = getPacienteFromForm();
        pacienteAtualizado.setId(id);

        try {
            int linhasAfetadas = pacienteDAO.atualizar(pacienteAtualizado);

            if (linhasAfetadas > 0) {
                carregarTodosPacientes();
                limparFormulario();
                exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Paciente atualizado com sucesso!");
            } else {
                exibirAlerta(Alert.AlertType.WARNING, "ID Não Encontrado",
                        "O ID " + id + " não foi encontrado no banco de dados.");
            }
        } catch (SQLException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro de Atualização",
                    "Falha ao atualizar paciente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    protected void onExcluirPacienteClick() {
        String idText = txtID.getText();
        if (idText.isEmpty()) {
            exibirAlerta(Alert.AlertType.WARNING, "ID Inválido", "Informe o ID para excluir.");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idText);
            if (id <= 0) {
                exibirAlerta(Alert.AlertType.WARNING, "ID Inválido", "O ID deve ser um número positivo.");
                return;
            }
        } catch (NumberFormatException e) {
            exibirAlerta(Alert.AlertType.ERROR, "ID Inválido", "O ID deve ser um número inteiro.");
            return;
        }

        try {
            int linhasAfetadas = pacienteDAO.excluir(id);

            if (linhasAfetadas > 0) {
                carregarTodosPacientes();
                limparFormulario();
                exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Paciente removido com sucesso!");
            } else {
                exibirAlerta(Alert.AlertType.WARNING, "ID Não Encontrado",
                        "Nenhum paciente com o ID " + id + " foi encontrado.");
            }
        } catch (SQLException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro de Exclusão",
                    "Falha ao remover paciente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void limparFormulario() {
        txtID.setText("");
        txtNome.setText("");
        txtCpf.setText("");
        txtTelefone.setText("");
        txtDataNascimento.setValue(null);
    }

    private void exibirAlerta(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
