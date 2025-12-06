package fiap.presenters;

import fiap.dto.Message;

public interface EmailPresenter {

    static String formatEmailContent(Message message) {
        return String.format("Feedback recebido:\n\nID: %s\nDescrição: %s\nNota: %d",
                message.id(),
                message.descricao(),
                message.nota());
    }

}
