package pcmagas.h300s.settings_retriever.exceptions;

public class CsrfTokenNotFound extends Exception {
    public CsrfTokenNotFound(String url){
        super("The url "+url+" does not contain a csrfToken");
    }
}
