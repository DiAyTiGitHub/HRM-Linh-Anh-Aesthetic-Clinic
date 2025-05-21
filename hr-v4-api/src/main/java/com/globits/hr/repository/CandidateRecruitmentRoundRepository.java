package com.globits.hr.repository;

import com.globits.hr.domain.Candidate;
import com.globits.hr.domain.CandidateRecruitmentRound;
import com.globits.hr.dto.CandidateDto;
import com.globits.hr.dto.CandidateRecruitmentRoundDto;
import com.globits.salary.domain.SalaryResultItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CandidateRecruitmentRoundRepository extends JpaRepository<CandidateRecruitmentRound, UUID> {
    @Query("select crr from CandidateRecruitmentRound crr " +
            "where crr.candidate.id = :candidateId " +
            "and crr.recruitmentRound.id = :recruitmentRoundId")
    List<CandidateRecruitmentRound> getByCandidateIdAndRecruitmentRoundId(@Param("candidateId") UUID candidateId,
                                                                          @Param("recruitmentRoundId") UUID recruitmentRoundId);

    @Query("select new com.globits.hr.dto.CandidateRecruitmentRoundDto(crr) from CandidateRecruitmentRound crr " +
            "where crr.recruitmentRound.recruitmentPlan.id = :planId " +
            "AND crr.recruitmentRound.id = :recruitmentRoundId")
    List<CandidateRecruitmentRoundDto> getListCandiDateByPlainAndRound(@Param("planId") UUID planId,
                                                                       @Param("recruitmentRoundId") UUID recruitmentRoundId);

    @Query("select crr from CandidateRecruitmentRound crr " +
            "where crr.recruitmentRound.id = :recruitmentRoundId")
    List<CandidateRecruitmentRound> findByRoundId(UUID recruitmentRoundId);


    @Query("select crr from CandidateRecruitmentRound crr " +
            "where crr.id IN :candidateRoundIds")
    List<CandidateRecruitmentRound> findByIdIn(List<UUID> candidateRoundIds);

    @Query("select crr from CandidateRecruitmentRound crr " +
            "where crr.recruitmentRound.id = :roundId")
    List<CandidateRecruitmentRound> findByRecruitmentRoundId(UUID roundId);

    @Query("select crr from CandidateRecruitmentRound crr " +
            "where crr.candidate.id = :candidateId AND crr.recruitmentRound.id = :roundId")
    Optional<CandidateRecruitmentRound> findTheNextRound(UUID candidateId, UUID roundId);

    List<CandidateRecruitmentRound> candidate(Candidate candidate);

    @Query("select new com.globits.hr.dto.CandidateRecruitmentRoundDto(crr) from CandidateRecruitmentRound crr " +
            "where crr.candidate.id = :candidateId " +
            "ORDER BY crr.createDate desc ")
    List<CandidateRecruitmentRoundDto> getCandidateRoundByCandidateId(UUID candidateId);

    @Query("SELECT crr FROM CandidateRecruitmentRound crr " +
            "WHERE crr.candidate.id = :candidateId " +
            "AND crr.recruitmentRound.roundOrder = (" +
            "    SELECT MAX(subCrr.recruitmentRound.roundOrder) " +
            "    FROM CandidateRecruitmentRound subCrr " +
            "    WHERE subCrr.candidate.id = :candidateId" +
            ")")
    CandidateRecruitmentRound getCurrentRoundOfCandidate(@Param("candidateId") UUID candidateId);

    @Query("SELECT crr FROM CandidateRecruitmentRound crr " +
            "WHERE crr.candidate.id IN :candidateIds " +
            "AND crr.recruitmentRound.roundOrder = (" +
            "SELECT MAX(sub.recruitmentRound.roundOrder) " +
            "FROM CandidateRecruitmentRound sub " +
            "WHERE sub.candidate.id = crr.candidate.id)")
    List<CandidateRecruitmentRound> getCurrentRoundsOfCandidates(@Param("candidateIds") List<UUID> candidateIds);


}
