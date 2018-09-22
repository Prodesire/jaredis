package jaredis;

class ProtocolException extends Exception {

    ProtocolException(String reason) {
        super(String.format("Protocol error: %s", reason));
    }
}
