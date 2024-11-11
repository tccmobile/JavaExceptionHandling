This example demonstrates all major features of Java exception handling. 

1. Custom Exception Class:
   - Shows how to create custom checked exceptions
   - Includes constructors for both message-only and message-with-cause

2. AutoCloseable Resource:
   - Demonstrates proper resource management
   - Shows how to implement the AutoCloseable interface
   - Includes deliberate failure scenarios for testing

3. Exception Handling Features:
   - Basic try-catch blocks
   - Multiple catch blocks with exception hierarchy
   - Try-with-resources statement
   - Finally blocks
   - Exception chaining
   - Suppressed exceptions

4. Different Exception Types:
   - Checked exceptions (CustomResourceException)
   - Unchecked exceptions (ArithmeticException, NullPointerException)
   - Runtime exceptions



**Try-with-resources** is a Java feature introduced in Java 7 that automatically manages resources that implement `AutoCloseable`. Here's a comprehensive explanation:

1. Basic Syntax:
```java
try (Resource resource = new Resource()) {
    resource.doSomething();
} // resource is automatically closed here
```

2. The AutoCloseable Interface:
```java
public interface AutoCloseable {
    void close() throws Exception;
}
```
Any class that implements this interface can be used in a try-with-resources statement. Common examples include:
- FileInputStream/FileOutputStream
- BufferedReader/BufferedWriter
- Socket
- Connection (JDBC)

3. Multiple Resources:
```java
try (FileInputStream fis = new FileInputStream("input.txt");
     FileOutputStream fos = new FileOutputStream("output.txt")) {
    // Work with both resources
    // Resources are closed in reverse order of declaration
} catch (IOException e) {
    // Handle exceptions
}
```

4. How Resources are Closed:
```java
class MyResource implements AutoCloseable {
    public void doSomething() throws Exception {
        throw new Exception("Operation failed");
    }
    
    @Override
    public void close() throws Exception {
        System.out.println("Closing resource");
    }
}

// Usage
try (MyResource resource = new MyResource()) {
    resource.doSomething();  // Throws exception
} catch (Exception e) {
    // Resource is closed before catch block is executed
}
```

5. Suppressed Exceptions:
```java
class FailingResource implements AutoCloseable {
    public void doSomething() throws Exception {
        throw new Exception("Operation failed");
    }
    
    @Override
    public void close() throws Exception {
        throw new Exception("Close failed");
    }
}

// Usage
try (FailingResource resource = new FailingResource()) {
    resource.doSomething();
} catch (Exception e) {
    System.out.println("Main exception: " + e.getMessage());
    // Access suppressed exceptions
    for (Throwable suppressed : e.getSuppressed()) {
        System.out.println("Suppressed: " + suppressed.getMessage());
    }
}
```

6. Custom Resource Example:
```java
class DatabaseConnection implements AutoCloseable {
    private final Connection conn;
    
    public DatabaseConnection(String url) throws SQLException {
        this.conn = DriverManager.getConnection(url);
    }
    
    public void query(String sql) throws SQLException {
        // Execute query
    }
    
    @Override
    public void close() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}

// Usage
try (DatabaseConnection db = new DatabaseConnection("jdbc:mysql://localhost/db")) {
    db.query("SELECT * FROM users");
}  // Connection automatically closed
```

7. Comparison with Traditional try-finally:
```java
// Old way
FileInputStream fis = null;
try {
    fis = new FileInputStream("file.txt");
    // Use the resource
} finally {
    if (fis != null) {
        try {
            fis.close();
        } catch (IOException e) {
            // Handle close exception
        }
    }
}

// New way with try-with-resources
try (FileInputStream fis = new FileInputStream("file.txt")) {
    // Use the resource
}  // Automatically closed
```

Key Benefits:
1. Automatic resource management
2. Cleaner code compared to try-finally
3. Proper handling of exceptions during close()
4. Multiple resources can be managed together
5. Suppressed exceptions are preserved

Important Notes:
- Resources are closed in reverse order of their creation
- The close() method is called even if an exception occurs in the try block
- If both the try block and close() throw exceptions, the try block exception is primary
- Exceptions from close() are suppressed and can be accessed via getSuppressed()
- The resource variables are effectively final within the try block
