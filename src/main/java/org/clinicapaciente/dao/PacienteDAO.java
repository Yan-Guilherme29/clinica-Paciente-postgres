package org.clinicapaciente.dao;


import org.clinicapaciente.model.Paciente;
import org.clinicapaciente.utils.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO {


        /**
         * Busca pacientes no banco de dados, opcionalmente filtrando por nome.
         * @param nome O nome completo ou parcial a ser buscado. Se vazio/null, lista todos.
         */
        public List<Paciente> buscarPorNome(String nome) throws SQLException {
            List<Paciente> pacientes = new ArrayList<>();

            // Se o nome for vazio, buscamos todos os registros
            boolean buscarTodos = (nome == null || nome.trim().isEmpty());

            // SQL BÁSICO: Busca todos os campos e ordena.
            String sql = "SELECT id, nome, cpf, dataNascimento, telefone FROM pacientes ";

            // Adiciona a cláusula WHERE para filtragem parcial (LIKE)
            if (!buscarTodos) {
                // Usamos ILIKE no PostgreSQL para busca case-insensitive (não diferencia maiúsculas/minúsculas)
                sql += "WHERE nome ILIKE ? ";
            }

            sql += "ORDER BY nome";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                if (!buscarTodos) {
                    // Adiciona o caractere curinga '%' para busca parcial
                    stmt.setString(1, "%" + nome.trim() + "%");
                }

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String nomePaciente = rs.getString("nome");
                        String cpf = rs.getString("cpf");
                        LocalDate dataNascimento = rs.getDate("dataNascimento").toLocalDate();
                        String telefone = rs.getString("telefone");

                        pacientes.add(new Paciente(id, nomePaciente, cpf, dataNascimento, telefone));
                    }
                }
            }
            return pacientes;
        }

        // O método salvar agora insere dados no PostgreSQL
        public void salvar(Paciente p) throws SQLException {
            // Usamos ON CONFLICT DO NOTHING para evitar erro de CPF duplicado,
            // ou você pode usar INSERT para criar um novo registro.
            String sql = "INSERT INTO pacientes (nome, cpf, dataNascimento, telefone) VALUES (?, ?, ?, ?)";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, p.getNome());
                stmt.setString(2, p.getCpf());
                stmt.setDate(3, Date.valueOf(p.getDataNascimento()));
                stmt.setString(4, p.getTelefone());
                stmt.executeUpdate();
            }
        }

        // O método listarTodos agora consulta o PostgreSQL
        public List<Paciente> listarTodos() throws SQLException {
            List<Paciente> pacientes = new ArrayList<>();
            String sql = "SELECT id, nome, cpf, dataNascimento, telefone FROM pacientes ORDER BY nome";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nome = rs.getString("nome");
                    String cpf = rs.getString("cpf");
                    LocalDate dataNascimento = rs.getDate("dataNascimento").toLocalDate();
                    String telefone = rs.getString("telefone");

                    pacientes.add(new Paciente(id, nome, cpf, dataNascimento, telefone));
                }
            }
            return pacientes;
        }

        // Método para buscar por ID no PostgreSQL
        public Paciente buscarPorId(int id) throws SQLException {
            String sql = "SELECT id, nome, cpf, dataNascimento, telefone FROM pacientes WHERE id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        LocalDate dataNascimento = rs.getDate("dataNascimento").toLocalDate();
                        return new Paciente(
                                rs.getInt("id"),
                                rs.getString("nome"),
                                rs.getString("cpf"),
                                dataNascimento,
                                rs.getString("telefone")
                        );
                    }
                }
            }
            return null; // Retorna null se não encontrar
        }

        // Método para atualizar no PostgreSQL
        /**
         * Atualiza um paciente existente no PostgreSQL.
         * @return O número de linhas afetadas (0 se o ID não for encontrado).
         */
        public int atualizar(Paciente atualizado) throws SQLException {
            String sql = "UPDATE pacientes SET nome=?, cpf=?, dataNascimento=?, telefone=? WHERE id=?";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, atualizado.getNome());
                stmt.setString(2, atualizado.getCpf());
                stmt.setDate(3, Date.valueOf(atualizado.getDataNascimento()));
                stmt.setString(4, atualizado.getTelefone());
                stmt.setInt(5, atualizado.getId());

                // RETORNA O NÚMERO DE LINHAS AFETADAS
                return stmt.executeUpdate();
            }
        }

        /**
         * Remove um paciente pelo ID no PostgreSQL.
         * @return O número de linhas afetadas (0 se o ID não for encontrado).
         */
        public int excluir(int id) throws SQLException {
            String sql = "DELETE FROM pacientes WHERE id=?";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, id);
                // RETORNA O NÚMERO DE LINHAS AFETADAS
                return stmt.executeUpdate();

            }
        }
    }