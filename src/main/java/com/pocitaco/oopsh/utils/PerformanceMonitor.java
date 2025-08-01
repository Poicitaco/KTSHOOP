package com.pocitaco.oopsh.utils;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Performance Monitor - Theo dõi hiệu suất ứng dụng
 * Singleton pattern để đảm bảo một instance duy nhất
 * 
 * @author OOPSH Team
 * @version 1.0
 */
public class PerformanceMonitor {

    private static PerformanceMonitor instance;
    private final Map<String, Instant> startTimes = new ConcurrentHashMap<>();
    private final Map<String, Long> operationCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> totalDurations = new ConcurrentHashMap<>();
    private final Map<String, Long> minDurations = new ConcurrentHashMap<>();
    private final Map<String, Long> maxDurations = new ConcurrentHashMap<>();

    private final Logger logger = Logger.getInstance();

    private PerformanceMonitor() {
    }

    public static PerformanceMonitor getInstance() {
        if (instance == null) {
            instance = new PerformanceMonitor();
        }
        return instance;
    }

    /**
     * Bắt đầu đo thời gian cho một operation
     */
    public void startOperation(String operationName) {
        startTimes.put(operationName, Instant.now());
    }

    /**
     * Kết thúc đo thời gian cho một operation
     */
    public void endOperation(String operationName) {
        Instant startTime = startTimes.remove(operationName);
        if (startTime != null) {
            long duration = Duration.between(startTime, Instant.now()).toMillis();
            updateStatistics(operationName, duration);
        }
    }

    /**
     * Đo thời gian thực thi của một operation với lambda
     */
    public <T> T measureOperation(String operationName, Operation<T> operation) throws Exception {
        startOperation(operationName);
        try {
            T result = operation.execute();
            return result;
        } finally {
            endOperation(operationName);
        }
    }

    /**
     * Đo thời gian thực thi của một operation không trả về giá trị
     */
    public void measureOperation(String operationName, VoidOperation operation) throws Exception {
        startOperation(operationName);
        try {
            operation.execute();
        } finally {
            endOperation(operationName);
        }
    }

    /**
     * Cập nhật thống kê cho operation
     */
    private void updateStatistics(String operationName, long duration) {
        operationCounts.merge(operationName, 1L, Long::sum);
        totalDurations.merge(operationName, duration, Long::sum);

        minDurations.merge(operationName, duration, (oldVal, newVal) -> Math.min(oldVal, newVal));
        maxDurations.merge(operationName, duration, (oldVal, newVal) -> Math.max(oldVal, newVal));

        // Log slow operations
        if (duration > 1000) { // Log operations taking more than 1 second
            logger.warning("Slow operation detected: " + operationName + " took " + duration + "ms");
        }
    }

    /**
     * Lấy thống kê cho một operation
     */
    public OperationStats getOperationStats(String operationName) {
        long count = operationCounts.getOrDefault(operationName, 0L);
        long total = totalDurations.getOrDefault(operationName, 0L);
        long min = minDurations.getOrDefault(operationName, 0L);
        long max = maxDurations.getOrDefault(operationName, 0L);

        double average = count > 0 ? (double) total / count : 0.0;

        return new OperationStats(operationName, count, total, min, max, average);
    }

    /**
     * Lấy tất cả thống kê
     */
    public Map<String, OperationStats> getAllStats() {
        Map<String, OperationStats> stats = new HashMap<>();
        for (String operationName : operationCounts.keySet()) {
            stats.put(operationName, getOperationStats(operationName));
        }
        return stats;
    }

    /**
     * Reset tất cả thống kê
     */
    public void resetStats() {
        startTimes.clear();
        operationCounts.clear();
        totalDurations.clear();
        minDurations.clear();
        maxDurations.clear();
        logger.info("Performance statistics reset");
    }

    /**
     * In thống kê ra console
     */
    public void printStats() {
        logger.info("=== Performance Statistics ===");
        Map<String, OperationStats> stats = getAllStats();

        if (stats.isEmpty()) {
            logger.info("No performance data available");
            return;
        }

        for (OperationStats stat : stats.values()) {
            logger.info(String.format("Operation: %s", stat.operationName));
            logger.info(String.format("  Count: %d", stat.count));
            logger.info(String.format("  Average: %.2f ms", stat.average));
            logger.info(String.format("  Min: %d ms", stat.min));
            logger.info(String.format("  Max: %d ms", stat.max));
            logger.info(String.format("  Total: %d ms", stat.total));
            logger.info("---");
        }
    }

    /**
     * Lấy thống kê dưới dạng string
     */
    public String getStatsAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Performance Statistics ===\n");

        Map<String, OperationStats> stats = getAllStats();
        if (stats.isEmpty()) {
            sb.append("No performance data available\n");
            return sb.toString();
        }

        for (OperationStats stat : stats.values()) {
            sb.append(String.format("Operation: %s\n", stat.operationName));
            sb.append(String.format("  Count: %d\n", stat.count));
            sb.append(String.format("  Average: %.2f ms\n", stat.average));
            sb.append(String.format("  Min: %d ms\n", stat.min));
            sb.append(String.format("  Max: %d ms\n", stat.max));
            sb.append(String.format("  Total: %d ms\n", stat.total));
            sb.append("---\n");
        }

        return sb.toString();
    }

    /**
     * Kiểm tra xem có operation nào đang chạy không
     */
    public boolean hasActiveOperations() {
        return !startTimes.isEmpty();
    }

    /**
     * Lấy danh sách các operation đang chạy
     */
    public Map<String, Instant> getActiveOperations() {
        return new HashMap<>(startTimes);
    }

    /**
     * Class chứa thống kê của một operation
     */
    public static class OperationStats {
        public final String operationName;
        public final long count;
        public final long total;
        public final long min;
        public final long max;
        public final double average;

        public OperationStats(String operationName, long count, long total, long min, long max, double average) {
            this.operationName = operationName;
            this.count = count;
            this.total = total;
            this.min = min;
            this.max = max;
            this.average = average;
        }
    }

    /**
     * Interface cho operation có trả về giá trị
     */
    @FunctionalInterface
    public interface Operation<T> {
        T execute() throws Exception;
    }

    /**
     * Interface cho operation không trả về giá trị
     */
    @FunctionalInterface
    public interface VoidOperation {
        void execute() throws Exception;
    }
}