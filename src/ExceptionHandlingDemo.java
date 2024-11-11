// Custom exception class
class CustomResourceException extends Exception {
    public CustomResourceException(String message) {
        super(message);
    }

    public CustomResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Resource class that implements AutoCloseable for try-with-resources
class Resource implements AutoCloseable {
    private final String name;
    private boolean isOpen;

    public Resource(String name) {
        this.name = name;
        this.isOpen = true;
        System.out.println("Resource " + name + " opened");
    }

    public void performOperation() throws CustomResourceException {
        if (!isOpen) {
            throw new CustomResourceException("Resource " + name + " is closed");
        }
        if (name.equals("faulty")) {
            throw new CustomResourceException("Resource " + name + " is faulty");
        }
        System.out.println("Operation performed on resource " + name);
    }

    @Override
    public void close() throws CustomResourceException {
        isOpen = false;
        System.out.println("Resource " + name + " closed");
        if (name.equals("failing")) {
            throw new CustomResourceException("Failed to close resource " + name);
        }
    }
}

public class ExceptionHandlingDemo {
    // Method that declares it throws exceptions
    public static void demonstrateCheckedExceptions() throws CustomResourceException {
        throw new CustomResourceException("Demonstrating checked exceptions");
    }

    // Method demonstrating exception chaining
    public static void demonstrateExceptionChaining() throws CustomResourceException {
        try {
            int[] arr = new int[5];
            arr[10] = 50; // Will throw ArrayIndexOutOfBoundsException
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new CustomResourceException("Error accessing array", e);
        }
    }

    public static void main(String[] args) {
        // 1. Basic try-catch
        try {
            int result = 10 / 0;
        } catch (ArithmeticException e) {
            System.out.println("Caught arithmetic exception: " + e.getMessage());
        }

        // 2. Multiple catch blocks
        try {
            String str = null;
            str.length();
        } catch (NullPointerException e) {
            System.out.println("Caught null pointer exception: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Caught general exception: " + e.getMessage());
        }

        // 3. Try-with-resources statement
        try (Resource r1 = new Resource("r1");
             Resource r2 = new Resource("r2")) {
            r1.performOperation();
            r2.performOperation();
        } catch (CustomResourceException e) {
            System.out.println("Resource operation failed: " + e.getMessage());
        }

        // 4. Try-catch-finally
        try {
            demonstrateCheckedExceptions();
        } catch (CustomResourceException e) {
            System.out.println("Caught custom exception: " + e.getMessage());
        } finally {
            System.out.println("Finally block executed");
        }

        // 5. Exception chaining
        try {
            demonstrateExceptionChaining();
        } catch (CustomResourceException e) {
            System.out.println("Main cause: " + e.getMessage());
            System.out.println("Original cause: " + e.getCause().getMessage());
        }

        // 6. Try-with-resources with multiple exceptions
        try (Resource r3 = new Resource("faulty");
             Resource r4 = new Resource("failing")) {
            r3.performOperation();
            r4.performOperation();
        } catch (CustomResourceException e) {
            System.out.println("Operation failed: " + e.getMessage());
            // Print suppressed exceptions (from close methods)
            if (e.getSuppressed().length > 0) {
                System.out.println("Suppressed exceptions:");
                for (Throwable suppressed : e.getSuppressed()) {
                    System.out.println("- " + suppressed.getMessage());
                }
            }
        }
    }
}