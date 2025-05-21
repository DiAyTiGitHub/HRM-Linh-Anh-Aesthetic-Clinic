package com.globits.hr.repository;

import com.globits.hr.domain.StaffWorkingLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StaffWorkingLocationRepository extends JpaRepository<StaffWorkingLocation, UUID> {
    @Query("SELECT swl FROM StaffWorkingLocation swl " +
            "WHERE swl.staff.id = :staffId " +
            "and swl.isMainLocation = true")
    List<StaffWorkingLocation> findMainLocationByStaffId(UUID staffId);

    @Query("SELECT swl FROM StaffWorkingLocation swl " +
            "WHERE swl.staff.id = :staffId ")
    List<StaffWorkingLocation> findLocationByStaffId(UUID staffId);

    @Query("SELECT swl FROM StaffWorkingLocation swl " +
            "WHERE swl.staff.id = :staffId " +
            "AND swl.workplace.id = :workplaceId")
    List<StaffWorkingLocation> findLocationByStaffIdWorkplaceId(UUID staffId, UUID workplaceId);

    @Query(value = """
            SELECT
            	swl.staff_id AS staffId,
            	swl.id AS id,
            	workplace.name AS workingLocation,
            	swl.is_main_location AS isMainLocation
            FROM tbl_staff_working_location swl
            JOIN tbl_staff staff ON staff.id = swl.staff_id
            JOIN tbl_workplace workplace ON workplace.id = swl.workplace_id
            WHERE swl.is_main_location = TRUE AND swl.staff_id IS NOT NULL
            """, nativeQuery = true)
    List<Object[]> findAllMainWorkingLocationNative();


}
