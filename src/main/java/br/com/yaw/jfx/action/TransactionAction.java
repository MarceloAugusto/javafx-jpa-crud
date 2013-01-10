package br.com.yaw.jfx.action;

import javax.persistence.EntityManager;

/**
 * Ação para operações de persistencia que manipulam dados.
 * 
 * <p>
 *  Adiciona o <strong>escopo transacional</strong> para o método <code>action</code>.
 *  Os métodos <code>preAction</code>, <code>posAction</code> e <code>actionFailure()</code> continuam sem transação.
 * </p>
 * 
 * @author YaW Tecnologia
 */
public abstract class TransactionAction extends AbstractAction {
    
    private EntityManager em;

    /**
     * Recebe o componente <code>EntityManager</code>, utilizado para controlar a transacão.
     * @param em referência para o <code>EntityManager</code>.
     */
    public TransactionAction() {
    }
    
    /**
     * Adiciona escopo transacional ao chamar método <code>action</code>.
     */
    @Override
    public void actionPerformed() {
        if (em == null) {
            throw new IllegalArgumentException("Informe o gerenciador de persistencia");
        }
        try {
            preAction();
            em.getTransaction().begin();
            action();
            em.getTransaction().commit();
            posAction();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            actionFailure();
            ex.printStackTrace();//TODO log
            throw new RuntimeException(ex);
        }
    }

    public void setPersistenceContext(EntityManager em) {
        this.em = em;
    }
    
}
