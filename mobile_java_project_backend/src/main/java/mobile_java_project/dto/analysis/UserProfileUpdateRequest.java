package mobile_java_project.dto.analysis;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileUpdateRequest {
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @Min(value = 0, message = "Age must be a positive number")
    private Integer age;

    @Size(max = 10, message = "Gender must not exceed 10 characters")
    private String gender;

    @Size(max = 1000, message = "Medical history must not exceed 1000 characters")
    private String medicalHistory;

    private String profilePictureUrl;
}