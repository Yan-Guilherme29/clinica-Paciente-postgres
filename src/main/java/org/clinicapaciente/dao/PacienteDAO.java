package org.clinicapaciente.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.clinicapaciente.model.Paciente;

import java.util.List;

public class PacienteDAO {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("clinicaPacientePU");

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    // Salvar (CREATE)
    public void salvar(Paciente paciente) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(paciente);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    // Listar todos (READ)
    public List<Paciente> listarTodos() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("FROM Paciente", Paciente.class).getResultList();
        } finally {
            em.close();
        }
    }

    // Buscar por nome
    public List<Paciente> buscarPorNome(String nome) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("FROM Paciente p WHERE LOWER(p.nome) LIKE LOWER(:nome)", Paciente.class)
                    .setParameter("nome", "%" + nome + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // Buscar por ID
    public Paciente buscarPorId(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Paciente.class, id);
        } finally {
            em.close();
        }
    }

    // Atualizar (UPDATE)
    public void atualizar(Paciente paciente) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(paciente);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    // Excluir por ID (DELETE)
    public void excluir(int id) {
        EntityManager em = getEntityManager();
        try {
            Paciente paciente = em.find(Paciente.class, id);
            if (paciente != null) {
                em.getTransaction().begin();
                em.remove(paciente);
                em.getTransaction().commit();
            }
        } finally {
            em.close();
        }
    }
}
