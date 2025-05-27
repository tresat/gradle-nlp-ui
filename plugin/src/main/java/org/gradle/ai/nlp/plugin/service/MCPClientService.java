package org.gradle.ai.nlp.plugin.service;

import com.google.common.base.Preconditions;
import org.gradle.ai.nlp.client.MCPClient;
import org.gradle.ai.nlp.client.MCPClient;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;

public abstract class MCPClientService implements BuildService<BuildServiceParameters.None>, AutoCloseable {
    private final MCPClient client = new MCPClient();;

    public boolean isConnected() {
        return client.isConnected();
    }

    public void connect() {
        Preconditions.checkState(!isConnected(), "Already connected");
        client.connect();
    }

    public String query(String query) {
        return client.query(query);
    }

    @Override
    public void close() throws Exception {
        if (isConnected()) {
            client.close();
        }
    }
}
