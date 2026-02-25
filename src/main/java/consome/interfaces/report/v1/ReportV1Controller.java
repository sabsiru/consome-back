package consome.interfaces.report.v1;

import consome.application.report.ReportFacade;
import consome.interfaces.report.dto.CreateReportRequest;
import consome.interfaces.report.dto.ReportResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
public class ReportV1Controller {

    private final ReportFacade reportFacade;

    @PostMapping
    public ResponseEntity<ReportResponse> create(
            @RequestParam Long userId,
            @RequestBody @Valid CreateReportRequest request) {
        var result = reportFacade.create(request.toCommand(userId));
        return ResponseEntity.status(HttpStatus.CREATED).body(ReportResponse.from(result));
    }
}
