package com.globits.hr.service;

import com.globits.hr.dto.function.DashboardDto;
import com.globits.hr.dto.search.DashboardSearchDto;
import org.springframework.stereotype.Service;

@Service
public interface DashboardService {
	DashboardDto getDashboard(DashboardSearchDto search);
}
