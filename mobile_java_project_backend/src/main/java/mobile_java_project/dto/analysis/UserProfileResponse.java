package mobile_java_project.dto.analysis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String email;
    private String fullName;
    private Integer age;
    private String gender;
    private String medicalHistory;
    private String profilePictureUrl;
    private Integer totalRecordings;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

