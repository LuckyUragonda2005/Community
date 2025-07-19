package CommunityApplication.ExceptionHandler;

public class  NoMatchFoundException  extends RuntimeException{
    public NoMatchFoundException(String message) {
        super(message);
    }

}
