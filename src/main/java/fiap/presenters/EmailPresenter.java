package fiap.presenters;

import fiap.dto.Message;

public interface EmailPresenter {

    static String formatEmailContent(Message message) {
        return String.format("Feedback recebido:\n\nID: %s\nDescrição: %s\nNota: %d",
                message.id(),
                message.descricao(),
                message.nota());
    }

    static String formatEmailHtmlContent(Message message) {
        return String.format(
                "<html>" +
                        "<body>" +
                        "<div style='background-color: #f0f0f0; padding: 20px; border-left: 5px solid #9933ff;'>" +
                        "<h1>Feedback recebido:</h1>" +
                        "<p><strong>ID:</strong> %s</p>" +
                        "<p><strong>Descrição:</strong> %s</p>" +
                        "<p><strong>Nota:</strong> <span style='color: #9933ff;'><strong>%d</strong></span></p>" +
                        "</div>" +
                        "</body>" +
                        "</html>",
                message.id(),
                message.descricao(),
                message.nota());
    }

}
