package br.com.yaw.jfx.controller;

import br.com.yaw.jfx.action.AbstractAction;
import br.com.yaw.jfx.dao.MercadoriaDAO;
import br.com.yaw.jfx.dao.MercadoriaDAOJPA;
import br.com.yaw.jfx.event.BuscarMercadoriaEvent;
import br.com.yaw.jfx.model.Mercadoria;
import br.com.yaw.jfx.ui.BuscarMercadoriaView;
import java.util.List;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

/**
 * Define a <code>Controller</code> responsável por gerir a tela de Busca de <code>Mercadoria</code> pelo campo <code>nome</code>.
 * 
 * @see br.com.yaw.jfx.controller.PersistenceController
 * 
 * @author YaW Tecnologia
 */
public class BuscarMercadoriaController extends PersistenceController {
    
    private BuscarMercadoriaView view;
    
    public BuscarMercadoriaController(ListaMercadoriaController parent) {
        super(parent);
        this.view = new BuscarMercadoriaView();
        
        this.view.addEventHandler(WindowEvent.WINDOW_HIDDEN, new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent window) {
                BuscarMercadoriaController.this.cleanUp();
            }
        });
        
        registerAction(this.view.getCancelarButton(), new AbstractAction() {
            @Override
            protected void action() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        BuscarMercadoriaController.this.view.hide();
                    }
                });
            }
        });
        
        registerAction(view.getBuscarButton(), new AbstractAction() {
            private List<Mercadoria> list;

            @Override
            protected void action() {
                if (view.getText().length() > 0) {
                    MercadoriaDAO dao = new MercadoriaDAOJPA(getPersistenceContext());
                    list = dao.getMercadoriasByNome(view.getText());
                    view.hide();
                }
            }
            
            @Override
            public void posAction() {
                cleanUp();
                fireEvent(new BuscarMercadoriaEvent(list));
                list = null;
            }
        });
    }
    
    public void show() {
        loadPersistenceContext();
        view.show();
    }

    @Override
    protected void cleanUp() {
        view.resetForm();
	super.cleanUp();
    }
}
