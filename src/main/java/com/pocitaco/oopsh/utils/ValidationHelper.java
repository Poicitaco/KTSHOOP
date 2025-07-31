package com.pocitaco.oopsh.utils;

import com.pocitaco.oopsh.dao.UserDAO;
import com.pocitaco.oopsh.models.User;
import com.pocitaco.oopsh.models.Registration;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class for validation methods
 */
public class ValidationHelper {

    // Email regex pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Phone regex pattern (Vietnamese format)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(0|84)(3[2-9]|5[689]|7[06-9]|8[1-689]|9[0-46-9])[0-9]{7}$");

    // ID card regex pattern (Vietnamese format)
    private static final Pattern ID_CARD_PATTERN = Pattern.compile(
            "^[0-9]{9,12}$");

    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validate phone number format
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Validate ID card format
     */
    public static boolean isValidIdCard(String idCard) {
        if (idCard == null || idCard.trim().isEmpty()) {
            return false;
        }
        return ID_CARD_PATTERN.matcher(idCard.trim()).matches();
    }

    /**
     * Validate username (alphanumeric, 3-20 characters)
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return username.trim().length() >= 3 && username.trim().length() <= 20 &&
                username.trim().matches("^[a-zA-Z0-9_]+$");
    }

    /**
     * Validate password strength (minimum 6 characters, at least 1 letter and 1
     * number)
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        return password.matches(".*[a-zA-Z].*") && password.matches(".*[0-9].*");
    }

    /**
     * Validate full name (non-empty, reasonable length)
     */
    public static boolean isValidFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return false;
        }
        return fullName.trim().length() >= 2 && fullName.trim().length() <= 100;
    }

    /**
     * Validate exam fee (positive number)
     */
    public static boolean isValidExamFee(double fee) {
        return fee > 0;
    }

    /**
     * Validate exam duration (positive number, reasonable range)
     */
    public static boolean isValidExamDuration(int duration) {
        return duration > 0 && duration <= 480; // Max 8 hours
    }

    /**
     * Validate passing score (0-100)
     */
    public static boolean isValidPassingScore(int score) {
        return score >= 0 && score <= 100;
    }

    /**
     * Validate exam date (not in the past)
     */
    public static boolean isValidExamDate(LocalDate examDate) {
        if (examDate == null) {
            return false;
        }
        return !examDate.isBefore(LocalDate.now());
    }

    /**
     * Validate max candidates (positive number, reasonable range)
     */
    public static boolean isValidMaxCandidates(int maxCandidates) {
        return maxCandidates > 0 && maxCandidates <= 1000;
    }

    /**
     * Check if username already exists
     */
    public static boolean isUsernameExists(String username, UserDAO userDAO) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        List<User> users = userDAO.getAllUsers();
        return users.stream()
                .anyMatch(user -> user.getUsername().equalsIgnoreCase(username.trim()));
    }

    /**
     * Check if email already exists
     */
    public static boolean isEmailExists(String email, UserDAO userDAO) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        List<User> users = userDAO.getAllUsers();
        return users.stream()
                .anyMatch(user -> user.getEmail() != null &&
                        user.getEmail().equalsIgnoreCase(email.trim()));
    }

    /**
     * Check if ID card already exists
     * Note: Currently User model doesn't have idCard field, so this is a placeholder
     * implementation that can be enhanced when the User model is updated
     */
    public static boolean isIdCardExists(String idCard, UserDAO userDAO) {
        if (idCard == null || idCard.trim().isEmpty()) {
            return false;
        }
        
        // For now, return false since User model doesn't have idCard field
        // This can be implemented when User model is enhanced with idCard field
        // Example implementation:
        // List<User> users = userDAO.getAllUsers();
        // return users.stream()
        //         .anyMatch(user -> user.getIdCard() != null &&
        //                 user.getIdCard().equalsIgnoreCase(idCard.trim()));
        
        return false;
    }

    /**
     * Validate candidate registration (check if already registered for same exam)
     */
    public static boolean canRegisterForExam(int candidateId, int examTypeId,
            List<Registration> existingRegistrations) {
        return existingRegistrations.stream()
                .noneMatch(reg -> reg.getUserId() == candidateId &&
                        reg.getExamTypeId() == examTypeId &&
                        !"CANCELLED".equals(reg.getStatus().toString()));
    }

    /**
     * Validate exam schedule capacity
     */
    public static boolean hasAvailableCapacity(int scheduleId, int maxCandidates,
            int registeredCandidates) {
        return registeredCandidates < maxCandidates;
    }

    /**
     * Generate validation error message
     */
    public static String getValidationErrorMessage(String fieldName, String errorType) {
        switch (errorType.toLowerCase()) {
            case "required":
                return fieldName + " không được để trống!";
            case "invalid_format":
                return fieldName + " có định dạng không hợp lệ!";
            case "already_exists":
                return fieldName + " đã tồn tại trong hệ thống!";
            case "too_short":
                return fieldName + " quá ngắn!";
            case "too_long":
                return fieldName + " quá dài!";
            case "invalid_range":
                return fieldName + " nằm ngoài phạm vi cho phép!";
            case "past_date":
                return fieldName + " không thể là ngày trong quá khứ!";
            case "no_capacity":
                return "Lịch thi đã đầy, không thể đăng ký thêm!";
            case "already_registered":
                return "Bạn đã đăng ký cho loại thi này!";
            default:
                return fieldName + " không hợp lệ!";
        }
    }

    /**
     * Sanitize input string (remove extra spaces, trim)
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        return input.trim().replaceAll("\\s+", " ");
    }

    /**
     * Format currency for display
     */
    public static String formatCurrency(double amount) {
        return String.format("%,.0f VNĐ", amount);
    }

    /**
     * Format percentage for display
     */
    public static String formatPercentage(double percentage) {
        return String.format("%.1f%%", percentage);
    }

    /**
     * Format date for display
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}