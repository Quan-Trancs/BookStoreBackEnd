package quantran.api.errorHandle;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import quantran.api.model.BookModel;

import javax.validation.ConstraintViolation;
import java.util.Set;

@Component
public class ErrorHandler {
    public static ResponseEntity<String> errorHandle(Set<ConstraintViolation<BookModel>> violations) {
        StringBuilder errorMessage = new StringBuilder();
        // Iterate through constraint violations and append them to the error message
        errorMessage.append(HttpStatus.BAD_REQUEST).append(": \n");
        for (ConstraintViolation<BookModel> violation : violations) {
            errorMessage.append(violation.getMessage()).append(". \n");
        }
        // Return the error message
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
    }
}
