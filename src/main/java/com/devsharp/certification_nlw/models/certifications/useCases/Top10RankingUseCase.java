package com.devsharp.certification_nlw.models.certifications.useCases;

import com.devsharp.certification_nlw.models.students.entities.CertificationStudentEntity;
import com.devsharp.certification_nlw.models.students.repositories.CertificationStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Top10RankingUseCase {

    @Autowired
    private CertificationStudentRepository certificationStudentRepository;

    public List<CertificationStudentEntity> execute() {
        var result = this.certificationStudentRepository.findTop10ByOrderByGradeDesc();
        return result;
    }

}
