package connect.go.controllers;

import connect.go.Repositories.ContestationRepository;
import connect.go.models.*;
import connect.go.models.dto.ComplaintByTypeResponse;
import connect.go.models.dto.CountReportResponse;
import connect.go.usecases.ContestationService;
import connect.go.usecases.CsvService;
import connect.go.usecases.ComplaintService;
import connect.go.usecases.UserService;
import connect.go.utils.ListaObj;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/reports")
public class CsvController {

    private final CsvService csv = new CsvService();
    private final ComplaintService complaintService;
    private final ContestationService contestationService;
    private final UserService userService;

    @GetMapping("/counts")
    public ResponseEntity<CountReportResponse> getCountsReport() {
        CountReportResponse response = new CountReportResponse(complaintService.count(),contestationService.count(),
                userService.countSearch());
        return ResponseEntity.status(200).body(response);
    }
    @GetMapping("/countsByType")
    public ResponseEntity<ComplaintByTypeResponse> getCountsByTypeReport() {
        ComplaintByTypeResponse response = complaintService.countByTypeAndGenre();
        return ResponseEntity.status(200).body(response);
    }
    @GetMapping("/{city}")
    public ResponseEntity getRelatorioComplaintByCity(@PathVariable String city) {
        List<Complaint> myArrayList = complaintService.getComplaintByCity(city).orElse(Collections.emptyList());

        if (myArrayList.isEmpty()) {
            return ResponseEntity.status(204).build();
        } else {
            ListaObj<Complaint> complaintListaObj = new ListaObj<>(myArrayList.size());
            for (Complaint complaint : myArrayList) {
                complaintListaObj.adiciona(complaint);
            }
            for (int i = 0; i < complaintListaObj.getTamanho(); i++) {
                for (int i2 = i; i2 < complaintListaObj.getTamanho(); i2++) {
                    Complaint aux = complaintListaObj.getElemento(i);
                    if (complaintListaObj.getElemento(i2).getUser().getId() < aux.getUser().getId()) {
                        complaintListaObj.alterarIndex(i, complaintListaObj.getElemento(i2));
                        complaintListaObj.alterarIndex(i2, aux);
                    }
                }
            }

            csv.gravaArquivoCsv(complaintListaObj, "posts");
            return ResponseEntity.status(200)
                    .header("content-type", "text/csv")
                    .header("content-disposition", "filename=\"posts.csv\"")
                    .body(csv.leExibeArquivoCsv("posts"));
        }
    }


    @GetMapping("/baixarTest")
    public ResponseEntity getRelatorio() {

        return ResponseEntity.status(200)
                .header("content-type", "text/csv")
                .header("content-disposition", "filename=\"posts.csv\"")
                .body(csv.leExibeArquivoCsv("posts"));
    }


    @GetMapping("/gravarTest")
    public ResponseEntity<ListaObj<Complaint>>gravarTest() {
        ListaObj<Complaint> complaintListaObj = new ListaObj<>(10);

        Driver driver = new Driver(5, "Antonio", "OMS1548");
        Address address = new Address(1, "SP", "São Paulo", "Esse ai");

        User user1 = new User(1, "Pedrão", "pedrao@alfa.com", "alfa", "total", "Alfa", "k", "d", LocalDate.now(), "ativo", 0);
        Complaint complaint1 = new Complaint(1, "decrição", "archive".getBytes(), "Verificado", null, LocalDateTime.now(),
                LocalDateTime.now(),"tipo", driver, user1, address);

        User user2 = new User(3, "Pedrão", "pedrao@alfa.com", "alfa", "total", "Alfa", "k", "d", LocalDate.now(), "ativo", 0);
        Complaint complaint2 = new Complaint(2, "decrição", "archive".getBytes(), "Verificado", null, LocalDateTime.now(),
                LocalDateTime.now(),"tipo", driver, user1, address);

        User user3 = new User(5, "Pedrão", "pedrao@alfa.com", "alfa", "total", "Alfa", "k", "d", LocalDate.now(), "ativo", 0);
        Complaint complaint3 = new Complaint(1, "decrição", "archive".getBytes(), "Verificado", null, LocalDateTime.now(),
                LocalDateTime.now(),"tipo", driver, user1, address);
        complaintListaObj.adiciona(complaint1);
        complaintListaObj.adiciona(complaint2);
        complaintListaObj.adiciona(complaint3);
        complaintListaObj.adiciona(complaint3);
        complaintListaObj.adiciona(complaint1);
        complaintListaObj.adiciona(complaint1);
        complaintListaObj.adiciona(complaint2);
        complaintListaObj.adiciona(complaint3);
        complaintListaObj.adiciona(complaint3);
        complaintListaObj.adiciona(complaint1);


        for (int i = 0; i < complaintListaObj.getTamanho(); i++) {
            for (int i2 = i; i2 < complaintListaObj.getTamanho(); i2++) {
                Complaint aux = complaintListaObj.getElemento(i);
                if (complaintListaObj.getElemento(i2).getUser().getId() < aux.getUser().getId()) {
                    complaintListaObj.alterarIndex(i, complaintListaObj.getElemento(i2));
                    complaintListaObj.alterarIndex(i2, aux);
                }
            }
        }
        csv.gravaArquivoCsv(complaintListaObj, "posts");
        return ResponseEntity.status(200).body(complaintListaObj);
    }
}
