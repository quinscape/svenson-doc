package de.quinscape.svensondoc;

public class SvensonDocGeneratorException
    extends RuntimeException
{
    private static final long serialVersionUID = -8362346279232617787L;


    public SvensonDocGeneratorException(String message)
    {
        super(message);
    }


    public SvensonDocGeneratorException(String message, Throwable cause)
    {
        super(message, cause);
    }


    public SvensonDocGeneratorException(Throwable cause)
    {
        super(cause);
    }
}
