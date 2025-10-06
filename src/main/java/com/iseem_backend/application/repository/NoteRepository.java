package com.iseem_backend.application.repository;

import com.iseem_backend.application.enums.TypeNote;
import com.iseem_backend.application.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {

    @Query("SELECT n FROM Note n WHERE n.student.userId = :studentId AND n.anneeScolaire = :anneeScolaire")
    List<Note> findByStudentAndAnneeScolaire(@Param("studentId") UUID studentId, @Param("anneeScolaire") String anneeScolaire);

    @Query("SELECT n FROM Note n WHERE n.module.idModule = :moduleId AND n.anneeScolaire = :anneeScolaire")
    List<Note> findByModuleAndAnneeScolaire(@Param("moduleId") UUID moduleId, @Param("anneeScolaire") String anneeScolaire);

    @Query("SELECT n FROM Note n WHERE n.student.userId = :studentId AND n.module.idModule = :moduleId AND n.typeNote = :typeNote AND n.anneeScolaire = :anneeScolaire")
    Optional<Note> findByStudentAndModuleAndTypeAndAnnee(@Param("studentId") UUID studentId,
                                                         @Param("moduleId") UUID moduleId,
                                                         @Param("typeNote") TypeNote typeNote,
                                                         @Param("anneeScolaire") String anneeScolaire);

    @Query("SELECT n FROM Note n JOIN FETCH n.student s JOIN FETCH n.module m " +
            "WHERE m.enseignant.enseignantId = :enseignantId AND n.anneeScolaire = :anneeScolaire")
    List<Note> findByEnseignantAndAnneeScolaire(@Param("enseignantId") UUID enseignantId,
                                                @Param("anneeScolaire") String anneeScolaire);

    @Query("SELECT n FROM Note n JOIN FETCH n.student s JOIN FETCH n.module m " +
            "WHERE n.student.userId = :studentId AND n.anneeScolaire = :anneeScolaire")
    List<Note> findByStudentWithDetails(@Param("studentId") UUID studentId,
                                        @Param("anneeScolaire") String anneeScolaire);

    @Query("SELECT n FROM Note n JOIN FETCH n.student s JOIN FETCH n.module m " +
            "WHERE m.idModule = :moduleId AND n.anneeScolaire = :anneeScolaire")
    List<Note> findByModuleWithDetails(@Param("moduleId") UUID moduleId,
                                       @Param("anneeScolaire") String anneeScolaire);

    @Query("DELETE FROM Note n WHERE n.student.userId = :studentId AND n.module.idModule = :moduleId AND n.typeNote = :typeNote AND n.anneeScolaire = :anneeScolaire")
    void deleteByStudentAndModuleAndTypeAndAnnee(@Param("studentId") UUID studentId,
                                                 @Param("moduleId") UUID moduleId,
                                                 @Param("typeNote") TypeNote typeNote,
                                                 @Param("anneeScolaire") String anneeScolaire);
}