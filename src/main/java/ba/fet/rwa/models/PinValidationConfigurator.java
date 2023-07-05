package ba.fet.rwa.models;

import ba.fet.rwa.services.PinService;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

public class PinValidationConfigurator extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        String pin = request.getParameterMap().get("pin").get(0);

        boolean isValidPin = validatePin(pin);

        if (!isValidPin) {
            throw new SecurityException("Invalid PIN");
        }
    }

    private boolean validatePin(String pin) {
        return PinService.validPin(pin);
    }
}
