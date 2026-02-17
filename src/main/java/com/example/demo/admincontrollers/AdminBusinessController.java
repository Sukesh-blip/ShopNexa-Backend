package com.example.demo.admincontrollers;

import com.example.demo.adminservices.AdminBusinessService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/business")
public class AdminBusinessController {

    private final AdminBusinessService adminBusinessService;

    public AdminBusinessController(AdminBusinessService adminBusinessService) {
        this.adminBusinessService = adminBusinessService;
    }

    @GetMapping("/monthly")
    public ResponseEntity<?> getMonthlyBusiness(@RequestParam int month, @RequestParam int year,
            jakarta.servlet.http.HttpServletRequest request) {
        try {
            com.example.demo.entity.User user = (com.example.demo.entity.User) request
                    .getAttribute("authenticatedUser");
            if (user == null || user.getRole() != com.example.demo.entity.Role.ADMIN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Admin role required");
            }
            Map<String, Object> businessReport = adminBusinessService.calculateMonthlyBusiness(month, year);
            return ResponseEntity.status(HttpStatus.OK).body(businessReport);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    @GetMapping("/daily")
    public ResponseEntity<?> getDailyBusiness(@RequestParam String date,
            jakarta.servlet.http.HttpServletRequest request) {
        try {
            com.example.demo.entity.User user = (com.example.demo.entity.User) request
                    .getAttribute("authenticatedUser");
            if (user == null || user.getRole() != com.example.demo.entity.Role.ADMIN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Admin role required");
            }
            LocalDate localDate = LocalDate.parse(date); // Parse the input date string
            Map<String, Object> businessReport = adminBusinessService.calculateDailyBusiness(localDate);
            return ResponseEntity.status(HttpStatus.OK).body(businessReport);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    @GetMapping("/yearly")
    public ResponseEntity<?> getYearlyBusiness(@RequestParam int year,
            jakarta.servlet.http.HttpServletRequest request) {
        try {
            com.example.demo.entity.User user = (com.example.demo.entity.User) request
                    .getAttribute("authenticatedUser");
            if (user == null || user.getRole() != com.example.demo.entity.Role.ADMIN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Admin role required");
            }
            Map<String, Object> businessReport = adminBusinessService.calculateYearlyBusiness(year);
            return ResponseEntity.status(HttpStatus.OK).body(businessReport);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    @GetMapping("/overall")
    public ResponseEntity<?> getOverallBusiness(jakarta.servlet.http.HttpServletRequest request) {
        try {
            com.example.demo.entity.User user = (com.example.demo.entity.User) request
                    .getAttribute("authenticatedUser");
            if (user == null || user.getRole() != com.example.demo.entity.Role.ADMIN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Admin role required");
            }
            Map<String, Object> overallBusiness = adminBusinessService.calculateOverallBusiness();
            return ResponseEntity.status(HttpStatus.OK).body(overallBusiness);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong while calculating overall business");
        }
    }

}
