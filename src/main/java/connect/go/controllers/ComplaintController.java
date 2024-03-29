package connect.go.controllers;

import connect.go.exceptions.UserNotFoundException;
import connect.go.models.*;
import connect.go.models.dto.ComplaintRegistration;
import connect.go.usecases.AddressService;
import connect.go.usecases.ComplaintService;
import connect.go.usecases.DriverService;
import connect.go.usecases.NotificationService;
import connect.go.usecases.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping("/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    private final AddressService addressService;

    private final UserService userService;

    private final DriverService driverService;

    private final NotificationService notificationService;


    @GetMapping("/city")
    public ResponseEntity<List<Complaint>> getComplaintByCity(@RequestHeader String city) {
        return ResponseEntity.of(complaintService.getComplaintByCity(city));
    }

    @GetMapping("/district")
    public ResponseEntity<List<Complaint>> getComplaintByDistrict(@RequestHeader String district) {
        return ResponseEntity.of(complaintService.getComplaintByDistrict(district));
    }

    @GetMapping("/state")
    public ResponseEntity<List<Complaint>> getComplaintByState(@RequestHeader String state) {
        return ResponseEntity.of(complaintService.getComplaintByState(state));
    }

    @GetMapping("/id/{complaintId}")
    public ResponseEntity<Complaint> getComplaintById(@PathVariable Integer complaintId) {
        Complaint complaint = complaintService.getComplaintById(complaintId);
        return ResponseEntity.status(200).body(complaint);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Complaint>> getComplaintByUserId(@PathVariable Integer userId) {
        return ResponseEntity.of(complaintService.getComplaintByUserId(userId));
    }

    @GetMapping("/license/{license}")
    public ResponseEntity<List<Complaint>> getComplaintByLicense(@PathVariable String license) {
        return ResponseEntity.of(complaintService.getComplaintByLicense(license));
    }

    @GetMapping("/{license}/{name}")
    public ResponseEntity<List<Complaint>> getComplaintByLicense(@PathVariable String license,
                                                                 @PathVariable String name) {
        return ResponseEntity.of(complaintService.getComplaintByLicenseAndName(license, name));
    }

    @PostMapping("/{userId}")
    public ResponseEntity<Complaint> createComplaint(@PathVariable Integer userId, @RequestBody ComplaintRegistration complaintRegistration) {
        Address address = addressService.register(new Address(complaintRegistration.getState(), complaintRegistration.getCity(), complaintRegistration.getDistrict()));
        User user = userService.getById(userId);
        Driver driver = driverService.register(new Driver(complaintRegistration.getDriverName(), complaintRegistration.getLicensePlate()));
        Complaint complaint = new Complaint();
        if (Objects.nonNull(complaintRegistration.getBo())) {
            complaint.setBo(complaintRegistration.getBo());
        }
        if (Objects.nonNull(complaintRegistration.getDescription())) {
            complaint.setDescription(complaintRegistration.getDescription());
        }
        if (Objects.nonNull(complaintRegistration.getType())) {
            complaint.setType(complaintRegistration.getType());
        }
        if (Objects.isNull(complaintRegistration.getDateTimeComplaint())) {
            complaint.setDateTimeComplaint(LocalDateTime.now());
        } else {
            complaint.setDateTimeComplaint(complaintRegistration.getDateTimeComplaint().atStartOfDay());
        }
        complaint.setAddress(address);
        complaint.setUser(user);
        complaint.setDriver(driver);
        complaint.setDateTimePost(LocalDateTime.now());
        complaint.setStatus("valido");
        complaintService.register(complaint);
        notificationService.register(userId,
                "Sua denúncia foi publicada",
                "Sua denúncia pode ser visualizada pelo feed ou você pode consultar na página minhas denúncias");
        return ResponseEntity.status(201).body(complaint);
    }

    @DeleteMapping("{complaintId}")
    public ResponseEntity<Void> deleteComplaintById(@PathVariable Integer complaintId) {
        Complaint complaint = complaintService.getComplaintById(complaintId);
        complaintService.setStatus(complaintId, "Inativo");
        notificationService.register(complaint.getUser().getId(),
                "Sua denúncia foi excluída",
                "Sua denúncia não vai aparecer mais no feed e sairá da página minhas denúncias");
        return ResponseEntity.status(200).build();
    }

    @PutMapping("/{userId}/{complaintId}")
    public ResponseEntity<Complaint> updateComplaint(@PathVariable Integer userId, @PathVariable Integer complaintId,
                                                     @RequestBody ComplaintRegistration complaintRegistration) {
        Address address = addressService.register(new Address(complaintRegistration.getState(), complaintRegistration.getCity(), complaintRegistration.getDistrict()));
        Driver driver = driverService.register(new Driver(complaintRegistration.getDriverName(), complaintRegistration.getLicensePlate()));
        Complaint complaint = complaintService.getComplaintById(complaintId);
        if (complaint.getUser().getId().equals(userId)) {
            if (Objects.nonNull(complaintRegistration.getBo())) {
                complaint.setBo(complaintRegistration.getBo());
            }
            if (Objects.nonNull(complaintRegistration.getDescription())) {
                complaint.setDescription(complaintRegistration.getDescription());
            }
            if (Objects.nonNull(complaintRegistration.getType())) {
                complaint.setType(complaintRegistration.getType());
            }
            if (Objects.isNull(complaintRegistration.getDateTimeComplaint())) {
                complaint.setDateTimeComplaint(LocalDateTime.now());
            } else {
                complaint.setDateTimeComplaint(complaintRegistration.getDateTimeComplaint().atStartOfDay());
            }
            complaint.setDateTimePost(LocalDateTime.now());
            complaint.setStatus("valido");
            complaint.setAddress(address);
            complaint.setDriver(driver);
            complaint = complaintService.register(complaint);
            notificationService.register(userId,
                    "Sua denúncia foi atualizada",
                    "Você já pode visualizá-la em minhas denúncias e verificar se foi corrigido");
            return ResponseEntity.status(200).body(complaint);
        } else {
            throw new UserNotFoundException();
        }
    }

    @PutMapping("/archive/{userId}/{complaintId}")
    public ResponseEntity<Complaint> addArchiveComplaint(@PathVariable Integer userId,
                                                         @PathVariable Integer complaintId,
                                                         @RequestBody MultipartFile file)

            throws IOException {

        Complaint complaint = complaintService.getComplaintById(complaintId);
        if (complaint.getUser().getId().equals(userId)) {
            byte[] bytes = file.getBytes();
            complaint.setArchive(bytes);
            complaint = complaintService.register(complaint);
            return ResponseEntity.status(201).body(complaint);
        } else {
            throw new UserNotFoundException();
        }
    }

    @GetMapping(value = "/archive/{complaintId}", produces = "image/png")
    public ResponseEntity getArchive(@PathVariable Integer complaintId) {
        Complaint complaint = complaintService.getComplaintById(complaintId);

        return ResponseEntity.status(200)
                .body(complaint.getArchive());
    }

}
