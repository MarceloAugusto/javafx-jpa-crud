package br.com.yaw.jfx.event;

import br.com.yaw.jfx.model.Mercadoria;

/**
 * Evento deve ser gerado durante a inclusão de uma <code>Mercadoria</code>.
 * 
 * <p>Recebe a referência da <code>Mercadoria</code> que foi incluida.</p>
 * 
 * @author YaW Tecnologia
 */
public class IncluirMercadoriaEvent extends AbstractEvent<Mercadoria> {
	
    public IncluirMercadoriaEvent(Mercadoria m) {
        super(m);
    }
    
}
