package edu.stanford.slac.core_build_system.utility;

/**
 * Utility class for retrying operations that may fail due to write conflicts.
 */
public class RetryableOperationUtility {
    static public <T> T retry(RetryableOperation<T> operation, int maxRetries) throws Exception {
        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                return operation.execute();
            } catch (Exception e) {
                if (e.getMessage().contains("WriteConflict")) {
                    attempt++;
                    if (attempt >= maxRetries) {
                        throw e;
                    }
                    Thread.sleep(100); // Adding a small delay before retrying
                } else {
                    throw e;
                }
            }
        }
        throw new Exception("Max retries reached");
    }

    /**
     * Functional interface for retryable operations.
     *
     * @param <T> The return type of the operation
     */
    @FunctionalInterface
    public interface RetryableOperation<T> {
        T execute() throws Exception;
    }
}