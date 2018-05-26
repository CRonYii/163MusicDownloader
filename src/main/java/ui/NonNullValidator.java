package ui;

public class NonNullValidator extends ValidatorBaseAdvanced {

    public NonNullValidator(String message) {
        super(message);
    }

    @Override
    boolean valid(String text) {
        return text != null && !text.isEmpty();
    }

}
