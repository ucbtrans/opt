package opt.data;

public class CheckItem {
    boolean is_error;
    Object item;

    public CheckItem(boolean is_error, Object item) {
        this.is_error = is_error;
        this.item = item;
    }
}
