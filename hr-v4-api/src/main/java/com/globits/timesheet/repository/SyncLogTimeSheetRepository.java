package com.globits.timesheet.repository;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.globits.timesheet.domain.SyncLogTimeSheet;

@Repository
public interface SyncLogTimeSheetRepository extends JpaRepository<SyncLogTimeSheet, UUID> {
	 // Tạo phương thức kiểm tra sự tồn tại của log với ngày đồng bộ và trạng thái
	boolean existsByDateSyncAndStatus(Date dateSync, String status);
}
