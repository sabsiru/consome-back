package consome.interfaces.report.v1;

import consome.application.report.ReportFacade;
import consome.infrastructure.security.CustomUserDetails;
import consome.interfaces.report.dto.CreateReportRequest;
import consome.interfaces.report.dto.ReportResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
public class ReportV1Controller {

    private final ReportFacade reportFacade;

    @PostMapping
    public ResponseEntity<ReportResponse> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid CreateReportRequest request) {
        var result = reportFacade.create(request.toCommand(userDetails.getUserId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ReportResponse.from(result));
    }

    @GetMapping("/mine")
    public ResponseEntity<Page<ReportResponse>> getMyReports(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 5) Pageable pageable) {
        var results = reportFacade.getMyReports(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(results.map(ReportResponse::from));
    }
}
