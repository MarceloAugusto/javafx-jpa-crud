package br.com.yaw.jfx.controller;

import br.com.yaw.jfx.action.AbstractAction;
import br.com.yaw.jfx.action.BooleanExpression;
import br.com.yaw.jfx.action.ConditionalAction;
import br.com.yaw.jfx.action.TransactionalAction;
import br.com.yaw.jfx.dao.MercadoriaDAO;
import br.com.yaw.jfx.dao.MercadoriaDAOJPA;
import br.com.yaw.jfx.event.DeletarMercadoriaEvent;
import br.com.yaw.jfx.event.IncluirMercadoriaEvent;
import br.com.yaw.jfx.model.Mercadoria;
import br.com.yaw.jfx.ui.Dialog;
import br.com.yaw.jfx.ui.IncluirMercadoriaView;
import br.com.yaw.jfx.validation.MercadoriaValidator;
import br.com.yaw.jfx.validation.Validator;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

/**
 * Define a <code>Controller</code> responsável por gerir a tela de inclusão/edição de <code>Mercadoria</code>.
 * 
 * @see br.com.yaw.jfx.controller.PersistenceController
 * 
 * @author YaW Tecnologia
 */
public class IncluirMercadoriaController extends PersistenceController {
    
    private IncluirMercadoriaView view;
    private Validator<Mercadoria> validador = new MercadoriaValidator();
    
    public IncluirMercadoriaController(AbstractController parent) {
        super(parent);
        
        this.view = new IncluirMercadoriaView();
        this.view.addEventHandler(WindowEvent.WINDOW_HIDDEN, new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent window) {
                IncluirMercadoriaController.this.cleanUp();
            }
        });
        
        registerAction(this.view.getCancelarButton(), new AbstractAction() {
            @Override
            protected void action() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        IncluirMercadoriaController.this.view.hide();
                    }
                });
            }
        });
        
        registerAction(this.view.getSalvarButton(), 
                ConditionalAction.build()
                    .addConditional(new BooleanExpression() {
                        @Override
                        public boolean conditional() {
                            Mercadoria m = view.getMercadoria();
                            String msg = validador.validate(m);
                            if (!"".equals(msg == null ? "" : msg)) {
                                Dialog.showInfo("Validacão", msg, view);
                                return false;
                            }
                                                
                            return true;
                        }
                    })
                    .addAction(
                        TransactionalAction.build()
                            .persistenceCtxOwner(IncluirMercadoriaController.this)
                            .addAction(new AbstractAction() {
                                private Mercadoria m;

                                @Override
                                protected void action() {
                                    m = view.getMercadoria();
                                    MercadoriaDAO dao = new MercadoriaDAOJPA(getPersistenceContext());
                                    m = dao.save(m);
                                }

                                @Override
                                protected void posAction() {
                                    view.hide();
                                    fireEvent(new IncluirMercadoriaEvent(m));
                                }
                            })));
        
        registerAction(view.getExcluirButton(), 
                TransactionalAction.build()
                    .persistenceCtxOwner(IncluirMercadoriaController.this)
                    .addAction(new AbstractAction() {
                        private Mercadoria m;
                        
                        @Override
                        protected void action() {
                            Integer id = view.getMercadoriaId();
                            if (id != null) {
                                MercadoriaDAO dao = new MercadoriaDAOJPA(getPersistenceContext());
                                m = dao.findById(id);
                                if (m != null) { 
                                    dao.remove(m);
                                }
                            }
                        }
                        
                        @Override
                        public void posAction() {
                            view.hide();
                            fireEvent(new DeletarMercadoriaEvent(m));
                        }
                    }));
    }
    
    public void show() {
        loadPersistenceContext(((PersistenceController) getParentController()).getPersistenceContext());
        view.show();
    }
    
    public void show(Mercadoria m) {
        view.setMercadoria(m);
        view.setTitle("Editar Mercadoria");
        show();
    }
    
    @Override
    protected void cleanUp() {
        view.setTitle("Incluir Mercadoria");
        view.resetForm();
        
        super.cleanUp();
    }
}
