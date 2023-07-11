function decodeMessage(message) {
    return message.split(/:(.*)/);
}

function encodeMessage(messageType, data) {
    return messageType + ':' + data;
}