package net.specialattack.forge.core.client.shader;

public class ShaderException extends Exception {

    private static final long serialVersionUID = 6774681842476536666L;

    public ShaderException() {
        super();
    }

    public ShaderException(String message) {
        super(message);
    }

    public ShaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShaderException(Throwable cause) {
        super(cause);
    }

}
