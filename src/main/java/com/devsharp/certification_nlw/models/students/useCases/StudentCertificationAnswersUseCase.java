package com.devsharp.certification_nlw.models.students.useCases;

import com.devsharp.certification_nlw.models.questions.entities.QuestionEntity;
import com.devsharp.certification_nlw.models.questions.repositories.QuestionRepository;
import com.devsharp.certification_nlw.models.students.dto.StudentCertificationAnswerDTO;
import com.devsharp.certification_nlw.models.students.dto.VerifyIfHasCertificationDTO;
import com.devsharp.certification_nlw.models.students.entities.AnswersCertificationsEntity;
import com.devsharp.certification_nlw.models.students.entities.CertificationStudentEntity;
import com.devsharp.certification_nlw.models.students.entities.StudentEntity;
import com.devsharp.certification_nlw.models.students.repositories.CertificationStudentRepository;
import com.devsharp.certification_nlw.models.students.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class StudentCertificationAnswersUseCase {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CertificationStudentRepository certificationStudentRepository;

    @Autowired
    private VerifyIfHasCertificationUseCase verifyIfHasCertificationUseCase;

    public CertificationStudentEntity execute(StudentCertificationAnswerDTO dto) throws Exception {

        var hasCertification =  this.verifyIfHasCertificationUseCase.execute(new VerifyIfHasCertificationDTO(
                dto.getEmail(), dto.getTechnology()
        ));

        if (hasCertification) {
            throw new Exception("Você já tirou sua certificação!");
        }

        List<QuestionEntity> questionsEntity = questionRepository.findByTechnology(dto.getTechnology());
        List<AnswersCertificationsEntity> answersCertifications = new ArrayList<>();

        AtomicInteger correctAnswers = new AtomicInteger(0);

        dto.getQuestionsAnswers().stream().forEach(questionAnswer -> {
           var question = questionsEntity.stream().filter(q -> q.getId().equals(questionAnswer.getQuestionID()));

           var findCorrectAlternative = question.findFirst().get().getAlternatives().stream()
                   .filter(alternative -> alternative.isCorrect()).findFirst().get();

           if (findCorrectAlternative.getId().equals(questionAnswer.getAlternativeID())) {
                questionAnswer.setCorrect(true);
                correctAnswers.incrementAndGet();
           } else {
               questionAnswer.setCorrect(false);
           }

           var answersCertificationsEntity = AnswersCertificationsEntity.builder()
                           .answerID(questionAnswer.getAlternativeID())
                           .questionID(questionAnswer.getQuestionID())
                           .isCorrect(questionAnswer.isCorrect()).build();

           answersCertifications.add(answersCertificationsEntity);
        });

        var student = studentRepository.findByEmail(dto.getEmail());
        UUID studentID;
        if (student.isEmpty()) {
            var studentCreated = StudentEntity.builder().email(dto.getEmail()).build();
            studentCreated = studentRepository.save(studentCreated);
            studentID = studentCreated.getId();
        } else {
            studentID = student.get().getId();
        }

        CertificationStudentEntity certificationStudentEntity =
                CertificationStudentEntity.builder()
                        .technology(dto.getTechnology())
                        .studentID(studentID)
                        .grade(correctAnswers.get())
                        .build();

        var certificationStudentCreated = certificationStudentRepository.save(certificationStudentEntity);

        answersCertifications.stream().forEach(answerCertification -> {
            answerCertification.setCertificationID(certificationStudentEntity.getId());
            answerCertification.setCertificationStudentEntity(certificationStudentEntity);
        });

        certificationStudentEntity.setAnswersCertificationsEntities(answersCertifications);

        certificationStudentRepository.save(certificationStudentEntity);

        return certificationStudentCreated;
    }

}
