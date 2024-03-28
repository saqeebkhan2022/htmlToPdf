package com.example.htmlToPdf.repository;

import com.example.htmlToPdf.model.ChildForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChildFormRepository extends JpaRepository<ChildForm,Long> {
}
