package mobile_java_project.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must contain at least one digit, one lowercase, one uppercase, one special character"
    )
    private String password;

    private String firstName;

    private String lastName;
    
    @NotBlank(message = "Full name is required")
    private String fullName;

    private Integer age;
    private String gender;
    private String medicalHistory;
    private String profilePictureUrl;
    
    // Helper method to split fullName into firstName and lastName
    public void processFullName() {
        if (fullName != null && !fullName.isEmpty()) {
            String[] nameParts = fullName.trim().split("\\s+", 2);
            this.firstName = nameParts[0];
            this.lastName = nameParts.length > 1 ? nameParts[1] : "";
        }
    }
} 