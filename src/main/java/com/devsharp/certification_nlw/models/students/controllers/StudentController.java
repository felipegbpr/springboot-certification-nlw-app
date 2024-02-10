package com.devsharp.certification_nlw.models.students.controllers;

import com.devsharp.certification_nlw.models.students.dto.StudentCertificationAnswerDTO;
import com.devsharp.certification_nlw.models.students.dto.VerifyIfHasCertificationDTO;
import com.devsharp.certification_nlw.models.students.entities.CertificationStudentEntity;
import com.devsharp.certification_nlw.models.students.useCases.StudentCertificationAnswersUseCase;
import com.devsharp.certification_nlw.models.students.useCases.VerifyIfHasCertificationUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/students")
public class StudentController {
    @Autowired
    private VerifyIfHasCertificationUseCase verifyIfHasCertificationUseCase;

    @Autowired
    private StudentCertificationAnswersUseCase studentCertificationAnswersUseCase;

    @PostMapping("/verifyIfHasCertification")
    public String verifyIfHasCertification(@RequestBody VerifyIfHasCertificationDTO verifyIfHasCertificationDTO) {

        var result = this.verifyIfHasCertificationUseCase.execute(verifyIfHasCertificationDTO);
        if(result) return "Usuário já fez a prova";
        return "Usuário pode fazer a prova";
    }
    @PostMapping("/certification/answer")
    public ResponseEntity certificationAnswer(
            @RequestBody StudentCertificationAnswerDTO studentCertificationAnswerDTO) throws Exception {
        try {
            var result = studentCertificationAnswersUseCase.execute(studentCertificationAnswerDTO);
            return ResponseEntity.ok().body(result);
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
