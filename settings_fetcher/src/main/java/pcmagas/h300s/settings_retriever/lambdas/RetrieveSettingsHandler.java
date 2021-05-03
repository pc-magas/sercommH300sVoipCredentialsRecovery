package pcmagas.h300s.settings_retriever.lambdas;

import pcmagas.h300s.settings_retriever.H300sVoipSettings;

public interface RetrieveSettingsHandler {
    public void retrieveSettings(H300sVoipSettings settings);
}
