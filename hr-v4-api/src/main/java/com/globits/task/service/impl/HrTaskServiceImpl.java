package com.globits.task.service.impl;

import com.globits.hr.domain.*;
import com.globits.hr.dto.*;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchTaskDto;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.repository.WorkingStatusRepository;
import com.globits.hr.service.*;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.hr.utils.MailUtil;
import com.globits.security.dto.RoleDto;
import com.globits.security.dto.UserDto;
import com.globits.task.domain.*;
import com.globits.task.dto.HrSubTaskDto;
import com.globits.task.dto.HrSubTaskItemDto;
import com.globits.task.dto.HrTaskDto;
import com.globits.task.dto.KanbanDto;
import com.globits.task.repository.*;
import com.globits.task.service.HrTaskService;
import com.globits.timesheet.domain.Label;
import com.globits.timesheet.domain.Project;
import com.globits.timesheet.domain.ProjectActivity;
import com.globits.timesheet.dto.*;
import com.globits.timesheet.dto.search.SearchTimeSheetDto;
import com.globits.timesheet.repository.ProjectActivityRepository;
import com.globits.timesheet.repository.ProjectRepository;
import com.globits.timesheet.repository.TimeSheetDetailRepository;
import com.globits.timesheet.service.LabelService;
import com.globits.timesheet.service.ProjectActivityService;
import com.globits.timesheet.service.TimeSheetDetailService;
import com.globits.timesheet.service.TimeSheetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.util.*;

import org.thymeleaf.context.Context;

@Service
public class HrTaskServiceImpl implements HrTaskService {

	private static final Logger logger = LoggerFactory.getLogger(HrTaskServiceImpl.class);
	@PersistenceContext
	EntityManager manager;
	@Autowired
	HrTaskRepository hrTaskRepository;
	@Autowired
	HrTaskStaffRepository hrTaskStaffRepository;
	@Autowired
	HrSubTaskRepository hrSubTaskRepository;
	@Autowired
	HrSubTaskItemRepository hrSubTaskItemRepository;
	@Autowired
	HrSubTaskItemStaffRepository hrSubTaskItemStaffRepository;
	@Autowired
	ProjectActivityRepository projectActivityRepository;
	@Autowired
	ProjectRepository projectRepository;
	@Autowired
	StaffRepository staffRepository;
	@Autowired
	WorkingStatusRepository workingStatusRepository;
	@Autowired
	UserExtService userExtService;
	@Autowired
	TimeSheetService timeSheetService;
	@Autowired
	private TimeSheetDetailRepository timeSheetDetailRepository;
	@Autowired
	private TimeSheetDetailService timeSheetDetailService;
	@Autowired
	private WorkingStatusService workingStatusService;
	@Autowired
	private HrGlobalPropertyService globalPropertyService;
	@Autowired
	private LabelService labelService;
	@Autowired
	private HrTaskHistoryService hrTaskHistoryService;

	@Autowired
	private ProjectActivityService projectActivityService;

	@Override
	@Modifying
	public Boolean delete(UUID id) {
//        try {
//            if (id != null) {
//                // List<HrTaskStaff> staffs = hrTaskStaffRepository.getAllByTaskId(id);
//                // if(staffs!=null && staffs.size()>0){
//                // hrTaskStaffRepository.deleteAll(staffs);
//                // }
//                // List<HrSubTask> subTasks = hrSubTaskRepository.getAllByTask(id);
//                // if(subTasks!=null && subTasks.size()>0){
//                // List<HrSubTaskItem> subTaskItems = hrSubTaskItemRepository.findAllByTask(id);
//                // if(subTaskItems!=null && subTaskItems.size()>0){
//                // List<HrSubTaskItemStaff> subTaskItemStaffs =
//                // hrSubTaskItemStaffRepository.getAllByTask(id);
//                // if(subTaskItemStaffs!=null && subTaskItemStaffs.size()>0){
//                // hrSubTaskItemStaffRepository.deleteAll(subTaskItemStaffs);
//                // }
//                // hrSubTaskItemRepository.deleteAll(subTaskItems);
//                // }
//                // hrSubTaskRepository.deleteAll(subTasks);
//                // }
//
//                timeSheetDetailRepository.updateByTask(id);
//                hrTaskRepository.deleteById(id);
//
//                return true;
//            }
//        } catch (Exception e) {
//            return false;
//        }
//        return false;

		// NEW LOGIC DELETE - not delete permanent from database, update voided fields
		// in task entity
		HrTask onDeleteTask = hrTaskRepository.findById(id).orElse(null);
		if (onDeleteTask == null)
			return false;
		onDeleteTask.setVoided(true);
		HrTask savedTask = hrTaskRepository.save(onDeleteTask);
		return true;
	}

	@Override
	public HrTaskDto createOrUpdate(HrTaskDto dto, UUID id) {
		if (dto == null) {
			return null;
		}

		UserDto onCallingApiUser = userExtService.getCurrentUser();

		HrTask hrTask = null;
		if (id != null) {
			hrTask = this.getEntityById(id);
		}

		if (hrTask == null && dto.getId() != null) {
			hrTask = this.getEntityById(dto.getId());
		}

		if (hrTask == null) {
			hrTask = new HrTask();

			// handle set creator when this task is new
			if (onCallingApiUser != null && onCallingApiUser.getId() != null) {
				hrTask.setCreatedBy(onCallingApiUser.getUsername());
			}
		}

		// update field modifiedBy by username
		if (onCallingApiUser != null && onCallingApiUser.getId() != null) {
			hrTask.setModifiedBy(onCallingApiUser.getUsername());
		}

		if (dto.getEndTime() != null && dto.getStartTime() != null && dto.getEndTime().before(dto.getStartTime())) {
			System.out.println("INVALID STARTTIME AND ENDTIME");
			return null;
		}

		// handling for auto generating code of task
		if (dto.getId() == null) {
			// handling for NEW TASK, but this task must belong to specific project
			if (dto.getProject() != null && dto.getProject().getId() != null) {
				hrTask.setCode(this.getUpcomingTaskCodeInProject(dto.getProject().getId()));
			}
		} else {
			// handling for UPDATE TASK, beware of the case that user changes the project of
			// the task, so we must update the code as well
			// checking whether current project is different from old project

			// case 1: task used to belong to specific project but now is not
			if (hrTask.getProject() != null && hrTask.getProject().getId() != null
					&& (dto.getProject() == null || dto.getProject().getId() == null)) {
				// then we remove code of this project
				hrTask.setCode(null);
			}
			// case 2: task used to not belong to any project, but now it is
			else if (hrTask.getProject() == null && dto.getProject() != null && dto.getProject().getId() != null) {
				// then we create code for this task
				hrTask.setCode(this.getUpcomingTaskCodeInProject(dto.getProject().getId()));
			}
			// case 3: task always has code but the project is different
			else if (hrTask.getProject() != null && dto.getProject() != null
					&& !hrTask.getProject().getId().equals(dto.getProject().getId())) {
				// then we just create new code for this task
				hrTask.setCode(this.getUpcomingTaskCodeInProject(dto.getProject().getId()));
			}
		}

		// this var for recording task history logic
		HrTaskDto oldValuesOfTask = new HrTaskDto(hrTask);
		boolean isSentMail = false;
		hrTask.setComment(null);
		// project
		Project project = null;
		if (dto.getProject() != null && dto.getProject().getId() != null) {
			Optional<Project> optional = projectRepository.findById(dto.getProject().getId());
			if (optional.isPresent()) {
				project = optional.get();
			}
		}
		hrTask.setProject(project);
		// projectActivity
		ProjectActivity projectActivity = null;
		if (dto.getActivity() != null && dto.getActivity().getId() != null) {
			Optional<ProjectActivity> optional = projectActivityRepository.findById(dto.getActivity().getId());
			if (optional.isPresent()) {
				projectActivity = optional.get();
			}
		}
		hrTask.setActivity(projectActivity);
		// workingStatus
		WorkingStatus workingStatus = null;
		if (dto.getStatus() != null && dto.getStatus().getId() != null) {
			workingStatus = workingStatusService.getEntityById(dto.getStatus().getId());
		}
		hrTask.setWorkingStatus(workingStatus);

		hrTask.setName(dto.getName());
		hrTask.setEstimateHour(dto.getEstimateHour());
		hrTask.setDescription(dto.getDescription());
		hrTask.setStartTime(dto.getStartTime());
		hrTask.setEndTime(dto.getEndTime());
		hrTask.setPriority(dto.getPriority());
		hrTask.setOrderNumber(dto.getOrderNumber());

		hrTask = hrTaskRepository.save(hrTask);

		// new logic to save HrTaskStaff, now only one staff does specific task at
		// certain time
		// handle for case there's an assignee for project
		Staff assignee = null;
		if (dto.getAssignee() != null && dto.getAssignee().getId() != null) {
			// checking whether this assignee has been recorded or not before
			boolean isExisted = false;
			Set<HrTaskStaff> newTaskStaffs = new HashSet<>();

			if (hrTask.getStaffs() != null && hrTask.getStaffs().size() > 0) {
				for (HrTaskStaff oldTaskStaff : hrTask.getStaffs()) {
					if (oldTaskStaff.getStaff().getId().equals(dto.getAssignee().getId())) {
						isExisted = true;
						newTaskStaffs.add(oldTaskStaff);
						break;
					}
				}
			}
			assignee = staffRepository.findById(dto.getAssignee().getId()).orElse(null);
			if (!isExisted) {
				// at this code, record for taskstaff of assign is not created,
				HrTaskStaff taskStaff = new HrTaskStaff();

				taskStaff.setTask(hrTask);

				if (assignee == null)
					return null;
				taskStaff.setStaff(assignee);
				taskStaff.setDateAssign(new Date());

				// then i create it
				HrTaskStaff savedTaskStaff = hrTaskStaffRepository.save(taskStaff);
				if (savedTaskStaff == null)
					return null;

				newTaskStaffs.add(savedTaskStaff);

				// sent mail to staff
				isSentMail = true;
			}

			hrTask.getStaffs().clear();
			hrTask.getStaffs().addAll(newTaskStaffs);
		} else {
			// clear all old taskStaffs
			if (hrTask.getStaffs() != null && hrTask.getStaffs().size() > 0) {
				hrTask.getStaffs().clear();
			}
		}

		// this variable for logging task subtask or its children has been changed
		int countSubtaskUpdated = 0; // number of subTaskItem is marked COMPLETE (checked)
		int countSubTaskUnChecked = 0; // number of subTaskItem is marked from COMPLETE to UNCOMPLETED
		// SUBTASK V2 - WRITTEN BY DIAYTI
		if (dto.getSubTasks() != null) {
			// save subtask
			List<HrSubTask> toSaveSubTasks = new ArrayList<>();

			for (HrSubTaskDto subTaskDto : dto.getSubTasks()) {
				// this subtask hasn't existed yet => create new it
				HrSubTask subTask = new HrSubTask();

				if (subTaskDto != null && subTaskDto.getId() != null) {
					// retrieve from database whether to update
					// this subtask hasn't existed yet => create new it
					subTask = hrSubTaskRepository.findById(subTaskDto.getId()).orElse(null);
				}

				subTask.setTask(hrTask);
				subTask.setCode(subTaskDto.getCode());
				subTask.setName(subTaskDto.getName());
				if (subTask.getName() == null)
					subTask.setName("");
				subTask.setDescription(subTaskDto.getDescription());

				// save item of subtask
				if (subTaskDto.getItems() != null) {
					List<HrSubTaskItem> toSaveSubTaskItems = new ArrayList<>();

					for (HrSubTaskItemDto subTaskItemDto : subTaskDto.getItems()) {
						// in this case, it's new
						HrSubTaskItem subTaskItem = new HrSubTaskItem();

						if (subTaskItemDto != null && subTaskItemDto.getId() != null) {
							// retrieve from database to check whether is update
							subTaskItem = hrSubTaskItemRepository.findById(subTaskItemDto.getId()).orElse(null);
						}

						// handling for logging changes of subtasks changed from UNCHECKED TO CHECKED
						if (
						// if value of subTaskItem changed from false to true (from NOT DONE -> DONE) =>
						// log status that there's subtaskItem completed
						(subTaskItem.isValue() == false && subTaskItemDto.isValue() == true)
//                                        ||
//                                        // or if current value of this item is not changed (currently is TRUE = DONE YET) and any field of this subTask is Updated => log status that there's subtaskItem completed
//                                        (subTaskItem.isValue() == true && subTaskItemDto.isValue() == true
//                                                && subTaskItemDto.getStartTime() != null && subTaskItemDto.getEndTime() != null
//                                                && subTaskItemDto.getStaffs() != null && subTaskItemDto.getStaffs().size() > 0
//                                                && ((!subTaskItem.getName().equals(subTaskItemDto.getName())
//                                                || (subTaskItem.getStartTime() == null || subTaskItem.getStartTime().getTime() != subTaskItemDto.getStartTime().getTime())
//                                                || (subTaskItem.getEndTime() == null || subTaskItem.getEndTime().getTime() != subTaskItemDto.getEndTime().getTime())
//                                        )))
						) {
							countSubtaskUpdated++;
						}

						// handling for logging changes of subtasks changed from CHECKED TO UNCHECKED
						if (subTaskItem.isValue() == true && subTaskItemDto.isValue() == false) {
							countSubTaskUnChecked++;
						}

						subTaskItem.setCode(subTaskItemDto.getCode());
						subTaskItem.setName(subTaskItemDto.getName());
						if (subTaskItem.getName() == null)
							subTaskItem.setName("");
						subTaskItem.setDescription(subTaskItemDto.getDescription());
						subTaskItem.setValue(subTaskItemDto.isValue());
						subTaskItem.setSubTask(subTask);
						subTaskItem.setStartTime(subTaskItemDto.getStartTime());
						subTaskItem.setEndTime(subTaskItemDto.getEndTime());

						// change assignee of subTaskItem
						Set<HrSubTaskItemStaff> toSaveSTIStaffs = new HashSet<>();

						if (subTaskItemDto.getStaffs() != null && subTaskItemDto.getStaffs().size() > 0) {
							// only have 1 assignee for 1 subTaskItem
							StaffDto itemDoer = subTaskItemDto.getStaffs().get(0);

							// doer
							Staff doer = staffRepository.findById(itemDoer.getId()).orElse(null);
							if (doer == null) {
								System.out.println("INVALID USER!");
								return null;
							}

							// subTaskItemStaff
							HrSubTaskItemStaff stiStaff = null;
							// find the existing one in database first
							List<HrSubTaskItemStaff> allValidSTIS = hrSubTaskItemStaffRepository
									.findByStaffIdAndItemId(doer.getId(), subTaskItem.getId());
							if (allValidSTIS != null && allValidSTIS.size() > 0) {
								stiStaff = allValidSTIS.get(0);
							}
							// create new item staff if there's no old record
							if (stiStaff == null) {
								stiStaff = new HrSubTaskItemStaff();
								stiStaff.setStaff(doer);
								stiStaff.setSubTaskItem(subTaskItem);
							}

							toSaveSTIStaffs.add(stiStaff);
						}

						// save to the field of subTaskItem
						if (subTaskItem.getStaffs() == null)
							subTaskItem.setStaffs(new HashSet<>());
						subTaskItem.getStaffs().clear();
						subTaskItem.getStaffs().addAll(toSaveSTIStaffs);

						toSaveSubTaskItems.add(subTaskItem);
					}

					if (subTask.getSubTaskItems() == null)
						subTask.setSubTaskItems(new HashSet<>());
					subTask.getSubTaskItems().clear();
					subTask.getSubTaskItems().addAll(toSaveSubTaskItems);
				}

				toSaveSubTasks.add(subTask);
			}

			if (hrTask.getSubTasks() == null)
				hrTask.setSubTasks(new HashSet<>());
			hrTask.getSubTasks().clear();
			hrTask.getSubTasks().addAll(toSaveSubTasks);
		}

		// save labels
		List<LabelDto> labelSet = dto.getLabels();
		if (labelSet != null && !labelSet.isEmpty()) {
			HashSet<HrTaskLabel> taskLabels = new HashSet<>();
			for (LabelDto item : labelSet) {
				HrTaskLabel taskLabel = new HrTaskLabel();
				Label label = new Label();
				if (item != null && item.getId() != null) {
					label = labelService.getEntityById(item.getId());
				}
				taskLabel.setTask(hrTask);
				if (label != null) {
					taskLabel.setLabel(label);
				}
				taskLabels.add(taskLabel);
			}
			if (hrTask.getLabels() != null) {
				hrTask.getLabels().clear();
				hrTask.getLabels().addAll(taskLabels);
			} else {
				hrTask.setLabels(taskLabels);
			}
		} else if (labelSet != null) {
			if (hrTask.getLabels() != null) {
				hrTask.getLabels().clear();
			}
		}

		hrTask = hrTaskRepository.save(hrTask);

		// handle send mail
		HrTaskDto response = new HrTaskDto(hrTask);
		if (dto.getComment() != null) {
			// sent mail to staff
			isSentMail = true;
			response.setComment(dto.getComment());
		}

		// handle record task history
		if (id == null) {
			// this is new task
			HrTaskHistoryDto newHistory = hrTaskHistoryService.generateHistoryLog(null, response, countSubtaskUpdated,
					countSubTaskUnChecked);

			// autocomplete start time and end time of task when drag and drop ticket by
			// specific status
			// CASE THIS TASK IS NEW
			autoCompleteStartTimeAndEndTimeOfTask(null, response);
		} else {
			// this is old task
			HrTaskHistoryDto newHistory = hrTaskHistoryService.generateHistoryLog(oldValuesOfTask, response,
					countSubtaskUpdated, countSubTaskUnChecked);

			// autocomplete start time and end time of task when drag and drop ticket by
			// specific status
			// CASE THIS TASK IS UPDATED
			autoCompleteStartTimeAndEndTimeOfTask(oldValuesOfTask, response);
		}

		if (isSentMail) {
			UserDto currentUser = userExtService.getCurrentUser();
			this.sendEmailtoStaff(assignee, workingStatus, dto, currentUser.getDisplayName());
		}

		return response;
	}

	private boolean isValidStartEndTime(Date startTime, Date endTime) {
		if (startTime != null && endTime != null && endTime.before(startTime)) {
			return false;
		}

		return true;
	}

	private void autoCompleteStartTimeAndEndTimeOfTask(HrTaskDto oldValues, HrTaskDto currentValues) {
		WorkingStatus oldWorkingStatus = null;
		if (oldValues != null && oldValues.getStatus() != null && oldValues.getStatus().getId() != null) {
			oldWorkingStatus = workingStatusRepository.findById(oldValues.getStatus().getId()).orElse(null);
		}

		WorkingStatus currentWorkingStatus = null;
		if (currentValues != null && currentValues.getStatus() != null && currentValues.getStatus().getId() != null) {
			currentWorkingStatus = workingStatusRepository.findById(currentValues.getStatus().getId()).orElse(null);
		}

		// autogenerate occurs only when working status of task CHANGED
		if (currentWorkingStatus != null
				&& (oldWorkingStatus == null || !currentWorkingStatus.getId().equals(oldWorkingStatus.getId()))) {
			// update working status to DOING
			if (currentWorkingStatus.getCode().equals("inprocess") && currentValues.getStartTime() == null) {
				// autofill startTime for task when update status to DOING
				HrTask task = hrTaskRepository.findById(currentValues.getId()).orElse(null);
				if (task == null)
					return;
				if (!isValidStartEndTime(new Date(), task.getEndTime()))
					return;
				task.setStartTime(new Date());

				HrTask savedEntity = hrTaskRepository.save(task);
				currentValues.setStartTime(savedEntity.getStartTime());
			}

			// update working status to RESOLVED/COMPLETED
			else if ((currentWorkingStatus.getCode().equals("resolved")
					|| currentWorkingStatus.getCode().equals("completed")) && currentValues.getEndTime() == null) {
				// autofill endTime for task
				HrTask task = hrTaskRepository.findById(currentValues.getId()).orElse(null);
				if (task == null)
					return;
				if (!isValidStartEndTime(task.getStartTime(), new Date()))
					return;
				task.setEndTime(new Date());

				HrTask savedEntity = hrTaskRepository.save(task);
				currentValues.setEndTime(savedEntity.getEndTime());

				// autogenerate timesheet detail V2 - written by DiAyTi
				// this function runs only current task has both startTime and endTime
				// or its subTaskItem has both startTime and endTime AND HAS ASSIGNEE
//                List<TimeSheetDetailDto> autogeneratedTSD =
//                        timeSheetDetailService.autogenerateTimesheetDetailV2(currentValues.getId());
			}
		}
	}

	private boolean generateTimesheetDetail(List<UUID> staffIds, WorkingStatusDto workingStatusDto, HrTask hrTask,
			boolean checkCompletedTask) {
		HrTaskDto hrTaskDto = new HrTaskDto(hrTask);
		List<Date> startTimeTasks = new ArrayList<>();
		Date startTime = new Date();
		Date endTime = new Date();
		List<Date> listMaxEndDateTimeSheetDetail = new ArrayList<>();

		if (hrTaskDto.getSubTasks() != null && hrTaskDto.getSubTasks().size() > 0) {
			List<TimeSheetDetailDto> oldTimesheetDetails = timeSheetDetailService
					.getListTimeSheetDetailBySubtaskItem(hrTaskDto.getId());
			timeSheetDetailService.deleteTimeSheetDetails(oldTimesheetDetails);
			List<HrSubTaskDto> listSubtask = hrTaskDto.getSubTasks();
			if (listSubtask != null && listSubtask.size() > 0) {
				for (HrSubTaskDto subTask : listSubtask) {
					List<HrSubTaskItemDto> listSubTaskItem = subTask.getItems();
					if (listSubTaskItem != null && listSubTaskItem.size() > 0) {
						for (HrSubTaskItemDto item : listSubTaskItem) {
							List<StaffDto> staffs = item.getStaffs();
							if (staffs != null && staffs.size() > 0) {
								if (item.getStartTime() != null && item.getEndTime() != null
										&& item.getStartTime().before(item.getEndTime())) {
									startTime = item.getStartTime();
									endTime = item.getEndTime();
								} else {
									logger.info(item.getName() + " chua nhap du start time, end time");
									continue;
								}
								if (item != null && !item.isValue()) {
									continue;
								}
								for (StaffDto staff : staffs) {
									if (staff != null && staff.getId() != null) {
										staffIds.remove(staff.getId()); // xoa nhan vien da duoc luu item
										List<TimeSheetDto> listTimesheet = timeSheetService
												.getTimeSheetByTime(staff.getId(), startTime, endTime);
										List<TimeSheetShiftWorkPeriodDto> allshift = new ArrayList<>();
										if (listTimesheet != null && listTimesheet.size() > 0) {
											for (TimeSheetDto ts : listTimesheet) {
												if (ts.getTimeSheetShiftWorkPeriod() != null
														&& ts.getTimeSheetShiftWorkPeriod().size() > 0) {
													for (TimeSheetShiftWorkPeriodDto sw : ts
															.getTimeSheetShiftWorkPeriod()) {
														if (sw.getWorkingFormat() != null
																&& sw.getWorkingFormat() != -1) {
															allshift.add(sw);
														} else {
															continue;
														}
													}
												}

											}
										}

										if (allshift != null && allshift.size() > 0) {
											for (TimeSheetShiftWorkPeriodDto shift : allshift) {
												Date shiftStartTime = DateTimeUtil.setTimeToDate(
														shift.getShiftWorkTimePeriod().getStartTime(),
														shift.getTimeSheet().getWorkingDate());
												Date shiftEndTime = DateTimeUtil.setTimeToDate(
														shift.getShiftWorkTimePeriod().getEndTime(),
														shift.getTimeSheet().getWorkingDate());

												if (shiftStartTime.after(endTime) || shiftEndTime.before(startTime)) {
													continue;
												}

												TimeSheetDetailDto timeSheetDetail = new TimeSheetDetailDto();

												timeSheetDetail.setTimeSheet(shift.getTimeSheet());
												if (startTime.before(shiftStartTime)) {
													timeSheetDetail.setStartTime(shiftStartTime);
												} else {
													timeSheetDetail.setStartTime(startTime);
												}
												if (endTime.after(shiftEndTime)) {
													timeSheetDetail.setEndTime(shiftEndTime);
												} else {
													timeSheetDetail.setEndTime(endTime);
												}

												// get old timsheetdetail of shift
												boolean isDuplicate = false;
												// List<TimeSheetDetailDto> oldTimesheetDetails = timeSheetDetailService
												// .getListTimeSheetDetailBySubtaskItem(staff.getId(), shift.getId(),
												// item.getId());
												// timeSheetDetailService.deleteTimeSheetDetails(oldTimesheetDetails);
												if (oldTimesheetDetails != null && oldTimesheetDetails.size() > 0) {
													for (TimeSheetDetailDto oldTs : oldTimesheetDetails) {
														if (oldTs.getHrTask() != null
																&& oldTs.getHrTask().getId().equals(hrTask.getId())) {
															//
															logger.info("Duplicate timesheetdetail: " + oldTs.getId()
																	+ "with Start Time: " + oldTs.getStartTime()
																	+ "endtime: " + oldTs.getEndTime());
															isDuplicate = true;
														}
													}
												}

												// if (isDuplicate)
												// break;

												timeSheetDetail.setProjectActivity(hrTaskDto.getActivity());
												timeSheetDetail.setPriority(hrTaskDto.getPriority());
												timeSheetDetail.setProject(hrTaskDto.getProject());
												timeSheetDetail.setWorkingStatus(workingStatusDto);
												timeSheetDetail.setEmployee(new StaffDto());
												timeSheetDetail.getEmployee().setId(staff.getId());
												timeSheetDetail.setTimeSheetShiftWorkPeriodDto(shift);
												timeSheetDetail.setHrTask(hrTaskDto);
												timeSheetDetail.setDescription(hrTaskDto.getName());
												logger.info("Generate TimesheetDetail of Staff:" + staff.getId()
														+ " - From date: " + startTime + " - To date: " + endTime);
												timeSheetDetailService.saveTimeSheetDetail(timeSheetDetail, null);

											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		if (checkCompletedTask) {
			if (hrTaskDto.getStartTime() != null) {
				startTime = hrTaskDto.getStartTime();
			}
			if (hrTaskDto.getEndTime() != null) {
				endTime = hrTaskDto.getEndTime();
			}

			if (staffIds != null && staffIds.size() > 0)
				for (UUID staffId : staffIds) {
					List<TimeSheetDto> listTimesheet = timeSheetService.getTimeSheetByTime(staffId, startTime, endTime);
					List<TimeSheetShiftWorkPeriodDto> allshift = new ArrayList<>();
					if (listTimesheet != null && listTimesheet.size() > 0) {
						for (TimeSheetDto ts : listTimesheet) {
							if (ts.getTimeSheetShiftWorkPeriod() != null
									&& ts.getTimeSheetShiftWorkPeriod().size() > 0) {
								for (TimeSheetShiftWorkPeriodDto sw : ts.getTimeSheetShiftWorkPeriod()) {
									if (sw.getWorkingFormat() != null && sw.getWorkingFormat() != -1) {
										allshift.add(sw);
									} else {
										continue;
									}
								}
							}

						}
					}

					if (allshift != null && allshift.size() > 0) {
						for (TimeSheetShiftWorkPeriodDto shift : allshift) {
							Date shiftStartTime = DateTimeUtil.setTimeToDate(
									shift.getShiftWorkTimePeriod().getStartTime(),
									shift.getTimeSheet().getWorkingDate());
							Date shiftEndTime = DateTimeUtil.setTimeToDate(shift.getShiftWorkTimePeriod().getEndTime(),
									shift.getTimeSheet().getWorkingDate());

							if (shiftStartTime.after(endTime) || shiftEndTime.before(startTime)) {
								continue;
							}

							TimeSheetDetailDto timeSheetDetail = new TimeSheetDetailDto();

							timeSheetDetail.setTimeSheet(shift.getTimeSheet());
							if (startTime.before(shiftStartTime)) {
								timeSheetDetail.setStartTime(shiftStartTime);
							} else {
								timeSheetDetail.setStartTime(startTime);
							}

							if (endTime.after(shiftEndTime)) {
								timeSheetDetail.setEndTime(shiftEndTime);
							} else {
								timeSheetDetail.setEndTime(endTime);
							}

							// get old timsheetdetail of shift
							boolean isDuplicate = false;
							// List<TimeSheetDetailDto> oldTimesheetDetails = timeSheetDetailService
							// .getListTimesheetByShift(shift.getId(), staffId);
							List<TimeSheetDetailDto> oldTimesheetDetails = timeSheetDetailService
									.getListTimeSheetDetailByTask(staffId, shift.getId(), hrTaskDto.getId());
							timeSheetDetailService.deleteTimeSheetDetails(oldTimesheetDetails);
							if (oldTimesheetDetails != null && oldTimesheetDetails.size() > 0) {
								for (TimeSheetDetailDto oldTs : oldTimesheetDetails) {
									if (oldTs.getHrTask() != null && oldTs.getHrTask().getId().equals(hrTask.getId())) {
										logger.info("Duplicate timesheetdetail: " + oldTs.getId() + "with Start Time: "
												+ oldTs.getStartTime() + "endtime: " + oldTs.getEndTime());
										isDuplicate = true;
									}
								}
							}

							// if (isDuplicate)
							// break;

							timeSheetDetail.setProjectActivity(hrTaskDto.getActivity());
							timeSheetDetail.setPriority(hrTaskDto.getPriority());
							timeSheetDetail.setProject(hrTaskDto.getProject());
							timeSheetDetail.setWorkingStatus(workingStatusDto);
							timeSheetDetail.setEmployee(new StaffDto());
							timeSheetDetail.getEmployee().setId(staffId);
							timeSheetDetail.setTimeSheetShiftWorkPeriodDto(shift);
							timeSheetDetail.setHrTask(hrTaskDto);
							timeSheetDetail.setDescription(hrTaskDto.getName());
							logger.info("Generate TimesheetDetail of Staff:" + staffId + " - From date: " + startTime
									+ " - To date: " + endTime);
							timeSheetDetailService.saveTimeSheetDetail(timeSheetDetail, null);
						}
					}
				}
		}

		return true;
	}

	private void sendEmailtoStaff(Staff assignee, WorkingStatus workingStatus, HrTaskDto dto, String currentUser) {
		// create thread to optimize response time when save task
		try {
			Thread t = new Thread(new Runnable() {
				private Context initContext(String url, String modifier, String code, String name, String status,
						Integer priority, String assignee, String comment, String projectCode) {
					String priorityStr = "";
					if (priority.equals(1)) {
						priorityStr = "Thấp";
					} else if (priority.equals(2)) {
						priorityStr = "Trung bình";
					} else if (priority.equals(3)) {
						priorityStr = "Cao";
					} else if (priority.equals(4)) {
						priorityStr = "Cấp bách";
					}

					Context context = new Context();
					context.setVariable("url", url);
					context.setVariable("modifier", modifier);
					context.setVariable("code", code);
					context.setVariable("name", name);
					context.setVariable("status", status);
					context.setVariable("priority", priorityStr);
					context.setVariable("assignee", assignee);
					context.setVariable("comment", comment);
					context.setVariable("projectCode", projectCode);

					return context;
				}

				public void run() {

					try {
						if (assignee == null) {
							return;
						}
						String assigneeEmail = assignee.getEmail();
						if (assigneeEmail == null || assigneeEmail.length() == 0)
							return;
						String[] to = { assigneeEmail };
						String subject = "Globits HR";
						MailInfoDto mailDto = new MailInfoDto(to, subject);
						mailDto.setTemplateName("email-template");
						String statusName = "";
						if (workingStatus != null) {
							statusName = workingStatus.getName();
						}

						Context context = this.initContext("", currentUser, dto.getCode(), dto.getName(), statusName,
								dto.getPriority(), assignee.getDisplayName(), dto.getComment(),
								dto.getProject().getCode());
						try {
							MailUtil.sendEmail(mailDto, context);
						} catch (jakarta.mail.MessagingException e) {
							e.printStackTrace();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					Thread.yield();
				}
			});

			t.setDaemon(true);
			t.start();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public HrTaskDto getById(UUID id) {
		HrTask entity = this.getEntityById(id);
		if (entity != null) {
			return new HrTaskDto(entity);
		}
		return null;
	}

	@Override
	public HrTask getEntityById(UUID id) {
		HrTask entity = null;
		Optional<HrTask> optional = hrTaskRepository.findById(id);
		if (optional.isPresent()) {
			entity = optional.get();
		}

		return entity;
	}

	@Override
	public List<HrTaskDto> searchByList(SearchDto dto) {
		if (dto == null) {
			return null;
		}
		boolean isRoleUser = false;
		boolean isRoleAdmin = false;
		boolean isRoleManager = false;
		UserDto user = userExtService.getCurrentUser();
		if (user != null && user.getRoles() != null && user.getRoles().size() > 0) {
			for (RoleDto item : user.getRoles()) {
				if (item.getName() != null && "ROLE_ADMIN".equals(item.getName())) {
					isRoleAdmin = true;
				}
				if (item.getName() != null && "HR_MANAGER".equals(item.getName())) {
					isRoleManager = true;
				}
				if (item.getName() != null && "HR_USER".equals(item.getName())) {
					isRoleUser = true;
				}
			}
		}
		StaffDto staffDto = userExtService.getCurrentStaff();
		if (isRoleAdmin) {
			isRoleManager = false;
			isRoleUser = false;
		} else {
			if (isRoleManager) {
				isRoleUser = false;
			}
		}
		if (dto.getFromDate() != null) {
			dto.setFromDate(DateTimeUtil.getStartOfDay(dto.getFromDate()));
		}
		if (dto.getToDate() != null) {
			dto.setToDate(DateTimeUtil.getEndOfDay(dto.getToDate()));
		}
		String whereClause = " where (1=1)";
		String orderBy = " ORDER BY entity.createDate DESC";
		String sql = "SELECT new com.globits.task.dto.HrTaskDto(entity) FROM HrTask as entity";
		if (dto.getStaffIdList() != null && dto.getStaffIdList().size() > 0) {
			whereClause += " AND entity.id in (SELECT entity.task.id FROM HrTaskStaff as entity WHERE entity.staff.id IN :listStaffId)";
		}
		if (dto.getProjectActivityIdList() != null && dto.getProjectActivityIdList().size() > 0) {
			whereClause += " AND entity.activity.id IN :listActivityId";
		}
		if (dto.getProjectIdList() != null && dto.getProjectIdList().size() > 0) {

			whereClause += " AND entity.activity.id in (SELECT entity.id FROM ProjectActivity as entity WHERE entity.project.id IN :listProjectId)";
		}
		if (dto.getFromDate() != null) {
			whereClause += " AND entity.startTime >= :fromDate ";
		}
		if (dto.getToDate() != null) {
			whereClause += "  AND entity.startTime <= :toDate";
		}
		if (dto.getProjectId() != null) {
			whereClause += "  AND entity.project.id = :projectId";
		}
		if (dto.getWorkingStatusId() != null) {
			whereClause += " AND entity.workingStatus.id = :workingStatusId";
		}
		if (dto.getYearReport() != null && dto.getMonthYear() != null) {
			whereClause += " AND YEAR(entity.startTime) = :yearReport AND MONTH(entity.startTime) = :monthReport";
		}
		if (isRoleManager || isRoleAdmin) {
			whereClause += "";
		} else if (isRoleUser) {
			if (user.getId() != null) {
				whereClause += " AND entity.project is null or entity.id IN (Select entity.task.id from HrTaskStaff entity where entity.staff.id = :staffId)";
			}
		}
		sql += whereClause + orderBy;
		Query query = manager.createQuery(sql, HrTaskDto.class);
		if (dto.getStaffIdList() != null) {
			query.setParameter("listStaffId", dto.getStaffIdList());
		}
		if (dto.getProjectActivityIdList() != null) {
			query.setParameter("listActivityId", dto.getProjectActivityIdList());
		}
		if (dto.getProjectIdList() != null) {
			query.setParameter("listProjectId", dto.getProjectIdList());
		}
		if (dto.getFromDate() != null) {
			query.setParameter("fromDate", dto.getFromDate());
		}
		if (dto.getToDate() != null) {
			query.setParameter("toDate", dto.getToDate());
		}
		if (dto.getProjectId() != null) {
			query.setParameter("projectId", dto.getProjectId());
		}
		if (dto.getWorkingStatusId() != null) {
			query.setParameter("workingStatusId", dto.getWorkingStatusId());
		}
		if (dto.getYearReport() != null && dto.getMonthYear() != null) {
			query.setParameter("yearReport", dto.getYearReport());
			query.setParameter("monthReport", dto.getMonthYear());
		}
		if (isRoleUser) {
			if (staffDto != null) {
				query.setParameter("staffId", staffDto.getId());
			}
		}
		List<HrTaskDto> entities = query.getResultList();
		return entities;
	}

	@Override
	public KanbanDto updateStatusNew(SearchDto dto) {
		HrTaskDto result = null;
		if (dto.getTaskId() == null || dto.getWorkingStatusId() == null)
			return null;
		HrTask task = this.getEntityById(dto.getTaskId());
		if (task == null)
			return null;

		result = new HrTaskDto(task);
		WorkingStatus workingStatus = workingStatusService.getEntityById(dto.getWorkingStatusId());
		if (workingStatus == null)
			return null;

		result.setStatus(new WorkingStatusDto());
		result.getStatus().setId(dto.getWorkingStatusId());
		result = this.createOrUpdate(result, dto.getTaskId());
		return new KanbanDto(result);
	}

	private String getOrderByQuery(SearchTaskDto dto) {
		String query = " order by ";

		if (dto.getIncreasingPriorityOrder() != null)
			if (dto.getIncreasingPriorityOrder())
				query += " entity.priority, ";
			else
				query += " entity.priority desc, ";

		if (dto.getIncreasingCodeOrder() != null)
			if (dto.getIncreasingCodeOrder())
				query += " CAST(entity.code as integer), ";
			else
				query += " CAST(entity.code as integer) desc, ";

		if (dto.getIncreasingLastModifyDate() != null)
			if (dto.getIncreasingLastModifyDate())
				query += " entity.modifyDate, ";
			else
				query += " entity.modifyDate desc, ";

		query += "entity.createDate DESC";

		return query;
	}

	private String constructQueryFromToDateUpdate(SearchTaskDto searchObject) {
		String dateQuery = " (";

		// case 1: fromDate and toDate are both filled
		if (searchObject.getFromDateUpdate() != null && searchObject.getToDateUpdate() != null) {
			dateQuery += " DATE(history.createDate) >= DATE(:fromDateUpdate) and DATE(history.createDate) <= DATE(:toDateUpDate) ";
		}
		// case 2: only fromDateUpdate is filled
		else if (searchObject.getFromDateUpdate() != null) {
			dateQuery += " DATE(history.createDate) >= DATE(:fromDateUpdate) ";
		}
		// case 3: only toDateUpDate is filled
		else if (searchObject.getToDateUpdate() != null) {
			dateQuery += " DATE(history.createDate) <= DATE(:toDateUpdate) ";
		}

		dateQuery += ") ";

		return dateQuery;
	}

	private String constructQueryFromToDate(SearchTaskDto searchObject) {
		String dateQuery = " (";

		// case 1: fromDate and toDate are both filled
		if (searchObject.getFromDate() != null && searchObject.getToDate() != null) {
			dateQuery += " DATE(entity.createDate) >= DATE(:fromDate) and DATE(entity.createDate) <= DATE(:toDate) ";
		}
		// case 2: only fromDate is filled
		else if (searchObject.getFromDate() != null) {
			dateQuery += " DATE(entity.createDate) >= DATE(:fromDate) ";
		}
		// case 3: only toDate is filled
		else if (searchObject.getToDate() != null) {
			dateQuery += " DATE(entity.createDate) <= DATE(:toDate) ";
		}

		dateQuery += ") ";

		return dateQuery;
	}
	
	@Override
	public Page<KanbanDto> pagingListTask(SearchTaskDto dto) {
		if (dto == null) {
			return null;
		}
		int pageIndex = dto.getPageIndex();
		int pageSize = dto.getPageSize();
		if (pageIndex > 0) {
			pageIndex--;
		} else {
			pageIndex = 0;
		}

		// check whether current user is admin or not
		boolean isAdmin = false;
		UserDto user = userExtService.getCurrentUser();
		if (user != null && user.getRoles() != null && user.getRoles().size() > 0) {
			for (RoleDto item : user.getRoles()) {
				if (item.getName() != null
						&& ("ROLE_ADMIN".equals(item.getName()) || "ROLE_SUPER_ADMIN".equals(item.getName()))) {
					isAdmin = true;
				}
			}
		}

		String whereClause = " where (1=1) and (entity.voided is null or entity.voided = false) ";
		String sql = "SELECT distinct new com.globits.task.dto.KanbanDto(entity) FROM HrTask as entity";
		String sqlCount = "SELECT COUNT(distinct entity.id) from HrTask as entity";

		String orderBy = " ORDER BY entity.createDate DESC";

		if (dto.getIncreasingCodeOrder() != null || dto.getIncreasingPriorityOrder() != null
				|| dto.getIncreasingLastModifyDate() != null) {
			orderBy = getOrderByQuery(dto);
		}

		// filter by Priority
		if (dto.getPriority() != null) {
			whereClause += (" and entity.priority = :priority ");
		}

		if (dto.getWorkingStatusId() != null) {
			whereClause += " AND entity.workingStatus.id = :workingStatus";
		}

		String projectStaffSql = "select ps.project.id from ProjectStaff ps "
				+ "where (ps.voided is null or ps.voided = false)";
		if (!isAdmin) {
			projectStaffSql += " AND ps.staff.id = :currentStaffId ";
		}
		if (dto.getTasksOfAllProjects() != null && dto.getTasksOfAllProjects().equals(true)) {
			whereClause += " and entity.project.id is not null "
					+ "and (entity.project.voided is null or entity.project.voided is false) "
					+ "AND entity.project.id in (" + projectStaffSql + ") ";
		} else if (dto.getTasksOfAllProjects() != null && dto.getTasksOfAllProjects().equals(false)) {
			whereClause += " AND entity.project.id is null ";
		} else if (dto.getProjectIdList() != null) {
			whereClause += " AND entity.project.id in :projectIdList and entity.project.id in (" + projectStaffSql
					+ ") ";
		}

		if (dto.getProjectId() != null) {
			whereClause += " AND entity.project.id = :projectId ";
		}

		if (dto.getKeyword() != null && !dto.getKeyword().isEmpty() && StringUtils.hasText(dto.getKeyword())) {
			dto.setKeyword(dto.getKeyword().trim());
			whereClause += " AND (UPPER(entity.name) LIKE UPPER(:keyword) or (entity.project is not null and UPPER(concat(entity.project.code, '#', entity.code)) LIKE UPPER(:keyword))) ";
		}
		if (dto.getPriority() != null) {
			whereClause += " AND entity.priority = :priority";
		}
		if (dto.getStaffId() != null) {
			sql += " inner join HrTaskStaff ts on entity.id = ts.task.id";
			sqlCount += " inner join HrTaskStaff ts on entity.id = ts.task.id";
			whereClause += " AND ts.staff.id = :staffId or SIZE(entity.staffs) = 0";
		}

		if (dto.getModifierId() != null) {
			sql += " inner join HrTaskHistory hth on entity.id = hth.task.id";
			sqlCount += " inner join HrTaskHistory hth on entity.id = hth.task.id";
			whereClause += " AND hth.modifier.id = :modifierId "
			+ "and hth.event like '%\"isNew\":true}%' "
			// Lọc theo task có history cũ nhất
			+ " AND hth.createDate = ( "
			+ "     SELECT MIN(h.createDate) "
			+ "     FROM HrTaskHistory h "
			+ "     WHERE h.task.id = entity.id "
			+ " )";
		}

		// handle for filter task has createDate from fromDate to toDate
		if (dto.getFromDate() != null || dto.getToDate() != null) {
			// add query fromDate/toDate to whereClause
			whereClause += (" and " + constructQueryFromToDate(dto));
		}

		// handle for filter task has history log from fromDateUpdate to toDateUpdate
		if (dto.getFromDateUpdate() != null || dto.getToDateUpdate() != null) {
			sql += " inner join HrTaskHistory history on entity.id = history.task.id ";
			sqlCount += " inner join HrTaskHistory history on entity.id = history.task.id ";

			// add query fromDateUpdate/toDateUpdate to whereClause
			whereClause += (" and " + constructQueryFromToDateUpdate(dto));
		}

		// filter by project's activity
		if (dto.getProjectActivityId() != null) {
			// only find task with selected activity
			if (dto.getIncludeChildrenActivities() == null || dto.getIncludeChildrenActivities().equals(false)) {
				whereClause += " and entity.activity.id = :projectActivityId ";
			}
			// find task including all of its children
			else {
				whereClause += " and entity.activity.id in :onFindActivityIds ";
			}
		}

		sql += whereClause + orderBy;
		sqlCount += whereClause;
		Query query = manager.createQuery(sql, KanbanDto.class);
		Query qCount = manager.createQuery(sqlCount);

		// filter by Priority
		if (dto.getPriority() != null) {
			query.setParameter("priority", dto.getPriority());
			qCount.setParameter("priority", dto.getPriority());
		}

		if (dto.getWorkingStatusId() != null) {
			query.setParameter("workingStatus", dto.getWorkingStatusId());
			qCount.setParameter("workingStatus", dto.getWorkingStatusId());
		}

		if (dto.getTasksOfAllProjects() != null && dto.getTasksOfAllProjects().equals(true)) {
			// only can see task in joined project with user has role HR_USER
			if (!isAdmin) {
				UUID currentStaffId = userExtService.getCurrentStaff().getId();
				query.setParameter("currentStaffId", currentStaffId);
				qCount.setParameter("currentStaffId", currentStaffId);
			}
		} else if (dto.getProjectIdList() != null) {
			query.setParameter("projectIdList", dto.getProjectIdList());
			qCount.setParameter("projectIdList", dto.getProjectIdList());

			// only can see task in joined project with user has role HR_USER
			if (!isAdmin) {
				UUID currentStaffId = userExtService.getCurrentStaff().getId();
				query.setParameter("currentStaffId", currentStaffId);
				qCount.setParameter("currentStaffId", currentStaffId);
			}
		}

		if (dto.getProjectId() != null) {
			query.setParameter("projectId", dto.getProjectId());
			qCount.setParameter("projectId", dto.getProjectId());
		}

		if (dto.getKeyword() != null && !dto.getKeyword().isEmpty() && StringUtils.hasText(dto.getKeyword())) {
			query.setParameter("keyword", "%" + dto.getKeyword() + "%");
			qCount.setParameter("keyword", "%" + dto.getKeyword() + "%");
		}
		if (dto.getPriority() != null) {
			query.setParameter("priority", dto.getPriority());
			qCount.setParameter("priority", dto.getPriority());
		}
		if (dto.getStaffId() != null) {
			query.setParameter("staffId", dto.getStaffId());
			qCount.setParameter("staffId", dto.getStaffId());
		}
		if (dto.getModifierId() != null) {
			query.setParameter("modifierId", dto.getModifierId());
			qCount.setParameter("modifierId", dto.getModifierId());
		}

		// filter by fromDate
		if (dto.getFromDate() != null) {
			query.setParameter("fromDate", dto.getFromDate());
			qCount.setParameter("fromDate", dto.getFromDate());
		}
		// filter by toDate
		if (dto.getToDate() != null) {
			query.setParameter("toDate", dto.getToDate());
			qCount.setParameter("toDate", dto.getToDate());
		}

		// filter by fromDateUpdate
		if (dto.getFromDateUpdate() != null) {
			query.setParameter("fromDateUpdate", dto.getFromDateUpdate());
			qCount.setParameter("fromDateUpdate", dto.getFromDateUpdate());
		}
		// filter by toDateUpDate
		if (dto.getToDateUpdate() != null) {
			query.setParameter("toDateUpDate", dto.getToDateUpdate());
			qCount.setParameter("toDateUpDate", dto.getToDateUpdate());
		}

		// filter by project's activity
		if (dto.getProjectActivityId() != null) {
			// only find task with selected activity
			if (dto.getIncludeChildrenActivities() == null || dto.getIncludeChildrenActivities().equals(false)) {
				query.setParameter("projectActivityId", dto.getProjectActivityId());
				qCount.setParameter("projectActivityId", dto.getProjectActivityId());
			}
			// find task including all of its children
			else {
				Set<UUID> onFindActivityIds = projectActivityService
						.getChildrenActivityIdsByParentActivityId(dto.getProjectActivityId());
				// must find for both selected activity and its children
				onFindActivityIds.add(dto.getProjectActivityId());

				query.setParameter("onFindActivityIds", new ArrayList<>(onFindActivityIds));
				qCount.setParameter("onFindActivityIds", new ArrayList<>(onFindActivityIds));
			}
		}

		int startPosition = pageIndex * pageSize;
		query.setFirstResult(startPosition);
		query.setMaxResults(pageSize);
		List<KanbanDto> entities = query.getResultList();

		// replace username to display name of staff
		for (KanbanDto kanban : entities) {
			// attach creator name to kanban
			HrTaskHistoryDto firstHistory = hrTaskHistoryService.getFirstCreatedHistoryOfTask(kanban.getId());
			String creatorName = "";
			if (firstHistory != null && firstHistory.getModifier() != null) {
				creatorName = firstHistory.getModifier().getDisplayName();
			}
			kanban.setCreatorName(creatorName);
		}

		long count = (long) qCount.getSingleResult();
		Pageable pageable = PageRequest.of(pageIndex, pageSize);
		return new PageImpl<>(entities, pageable, count);

	}

	@Override
	public List<KanbanDto> getListByLimit(SearchTaskDto dto) {
		if (dto == null) {
			return null;
		}
		List<WorkingStatusDto> statusList = workingStatusRepository.getListStatusIsNotNull();

		List<KanbanDto> list = new ArrayList<>();

		for (WorkingStatusDto workingStatus : statusList) {
			if (workingStatus != null && workingStatus.getId() != null) {
				dto.setWorkingStatusId(workingStatus.getId());

				Page<KanbanDto> pages = this.pagingListTask(dto);

				if (pages.getContent() != null && pages.getContent().size() > 0) {
					list.addAll(pages.getContent());
				}
			}
		}
		return list;
	}

	// this function only used for exporting excel => get all data
	@Override
	public List<KanbanDto> exportExcelTaskByFilter(SearchTaskDto dto) {
		if (dto == null) {
			return null;
		}

		// check whether current user is admin or not
		boolean isAdmin = false;
		UserDto user = userExtService.getCurrentUser();
		if (user != null && user.getRoles() != null && user.getRoles().size() > 0) {
			for (RoleDto item : user.getRoles()) {
				if (item.getName() != null
						&& ("ROLE_ADMIN".equals(item.getName()) || "ROLE_SUPER_ADMIN".equals(item.getName()))) {
					isAdmin = true;
				}
			}
		}

		String whereClause = " where (1=1) and (entity.voided is null or entity.voided = false) ";
		String sql = "SELECT distinct new com.globits.task.dto.KanbanDto(entity) FROM HrTask as entity";

		String orderBy = " ORDER BY entity.createDate DESC";

		if (dto.getIncreasingCodeOrder() != null || dto.getIncreasingPriorityOrder() != null
				|| dto.getIncreasingLastModifyDate() != null) {
			orderBy = getOrderByQuery(dto);
		}

		// filter by Priority
		if (dto.getPriority() != null) {
			whereClause += (" and entity.priority = :priority ");
		}

		if (dto.getWorkingStatusId() != null) {
			whereClause += " AND entity.workingStatus.id = :workingStatus";
		}

		String projectStaffSql = "select ps.project.id from ProjectStaff ps "
				+ "where (ps.voided is null or ps.voided = false)";
		if (!isAdmin) {
			projectStaffSql += " AND ps.staff.id = :currentStaffId ";
		}
		if (dto.getTasksOfAllProjects() != null && dto.getTasksOfAllProjects().equals(true)) {
			whereClause += " and entity.project.id is not null "
					+ "and (entity.project.voided is null or entity.project.voided is false) "
					+ "AND entity.project.id in (" + projectStaffSql + ") ";
		} else if (dto.getTasksOfAllProjects() != null && dto.getTasksOfAllProjects().equals(false)) {
			whereClause += " AND entity.project.id is null ";
		} else if (dto.getProjectIdList() != null) {
			whereClause += " AND entity.project.id in :projectIdList and entity.project.id in (" + projectStaffSql
					+ ") ";
		}

		if (dto.getProjectId() != null) {
			whereClause += " AND entity.project.id = :projectId ";
		}

		if (dto.getKeyword() != null && !dto.getKeyword().isEmpty() && StringUtils.hasText(dto.getKeyword())) {
			dto.setKeyword(dto.getKeyword().trim());
			whereClause += " AND (UPPER(entity.name) LIKE UPPER(:keyword) or (entity.project is not null and UPPER(concat(entity.project.code, '#', entity.code)) LIKE UPPER(:keyword))) ";
		}
		if (dto.getPriority() != null) {
			whereClause += " AND entity.priority = :priority";
		}
		if (dto.getStaffId() != null) {
			sql += " inner join HrTaskStaff ts on entity.id = ts.task.id";
			whereClause += " AND ts.staff.id = :staffId or SIZE(entity.staffs) = 0";
		}

		// handle for filter task has createDate from fromDate to toDate
		if (dto.getFromDate() != null || dto.getToDate() != null) {
			// add query fromDate/toDate to whereClause
			whereClause += (" and " + constructQueryFromToDate(dto));
		}

		// handle for filter task has history log from fromDateUpdate to toDateUpdate
		if (dto.getFromDateUpdate() != null || dto.getToDateUpdate() != null) {
			sql += " inner join HrTaskHistory history on entity.id = history.task.id ";

			// add query fromDateUpdate/toDateUpdate to whereClause
			whereClause += (" and " + constructQueryFromToDateUpdate(dto));
		}

		// filter by project's activity
		if (dto.getProjectActivityId() != null) {
			// only find task with selected activity
			if (dto.getIncludeChildrenActivities() == null || dto.getIncludeChildrenActivities().equals(false)) {
				whereClause += " and entity.activity.id = :projectActivityId ";
			}
			// find task including all of its children
			else {
				whereClause += " and entity.activity.id in :onFindActivityIds ";
			}
		}

		sql += whereClause + orderBy;
		Query query = manager.createQuery(sql, KanbanDto.class);

		// filter by Priority
		if (dto.getPriority() != null) {
			query.setParameter("priority", dto.getPriority());
		}

		if (dto.getWorkingStatusId() != null) {
			query.setParameter("workingStatus", dto.getWorkingStatusId());
		}

		if (dto.getTasksOfAllProjects() != null && dto.getTasksOfAllProjects().equals(true)) {
			// only can see task in joined project with user has role HR_USER
			if (!isAdmin) {
				UUID currentStaffId = userExtService.getCurrentStaff().getId();
				query.setParameter("currentStaffId", currentStaffId);
			}
		} else if (dto.getProjectIdList() != null) {
			query.setParameter("projectIdList", dto.getProjectIdList());

			// only can see task in joined project with user has role HR_USER
			if (!isAdmin) {
				UUID currentStaffId = userExtService.getCurrentStaff().getId();
				query.setParameter("currentStaffId", currentStaffId);
			}
		}

		if (dto.getProjectId() != null) {
			query.setParameter("projectId", dto.getProjectId());
		}

		if (dto.getKeyword() != null && !dto.getKeyword().isEmpty() && StringUtils.hasText(dto.getKeyword())) {
			query.setParameter("keyword", "%" + dto.getKeyword() + "%");
		}
		if (dto.getPriority() != null) {
			query.setParameter("priority", dto.getPriority());
		}
		if (dto.getStaffId() != null) {
			query.setParameter("staffId", dto.getStaffId());
		}

		// filter by fromDate
		if (dto.getFromDate() != null) {
			query.setParameter("fromDate", dto.getFromDate());
		}
		// filter by toDate
		if (dto.getToDate() != null) {
			query.setParameter("toDate", dto.getToDate());
		}

		// filter by fromDateUpdate
		if (dto.getFromDateUpdate() != null) {
			query.setParameter("fromDateUpdate", dto.getFromDateUpdate());
		}
		// filter by toDateUpDate
		if (dto.getToDateUpdate() != null) {
			query.setParameter("toDateUpDate", dto.getToDateUpdate());
		}

		// filter by project's activity
		if (dto.getProjectActivityId() != null) {
			// only find task with selected activity
			if (dto.getIncludeChildrenActivities() == null || dto.getIncludeChildrenActivities().equals(false)) {
				query.setParameter("projectActivityId", dto.getProjectActivityId());
			}
			// find task including all of its children
			else {
				Set<UUID> onFindActivityIds = projectActivityService
						.getChildrenActivityIdsByParentActivityId(dto.getProjectActivityId());
				// must find for both selected activity and its children
				onFindActivityIds.add(dto.getProjectActivityId());

				query.setParameter("onFindActivityIds", new ArrayList<>(onFindActivityIds));
			}
		}

		List<KanbanDto> results = query.getResultList();

		// replace username to display name of staff
		for (KanbanDto kanban : results) {
			// attach creator name to kanban
			HrTaskHistoryDto firstHistory = hrTaskHistoryService.getFirstCreatedHistoryOfTask(kanban.getId());
			String creatorName = "";
			if (firstHistory != null && firstHistory.getModifier() != null) {
				creatorName = firstHistory.getModifier().getDisplayName();
			}
			kanban.setCreatorName(creatorName);
		}

		return results;
	}

	// get the code for the next task in specific project
	@Override
	public String getUpcomingTaskCodeInProject(UUID projectId) {
		if (projectId == null)
			return null;

		// wrong logic to get new task's code
		// long latestCode = (Long) hrTaskRepository.countTasksInProject(projectId);
		// fix bug
		String latestCode = "0";
		List<HrTask> tasksInProject = hrTaskRepository.getTasksInProject(projectId, PageRequest.of(0, 1));
		if (tasksInProject != null && tasksInProject.size() > 0) {
			HrTask latestTask = tasksInProject.get(0);
			latestCode = latestTask.getCode();
		}

		// get string value
		return String.valueOf(Integer.parseInt(latestCode) + 1);
	}

	@Override
	public List<KanbanDto> getByProjectActivity(UUID id) {
		List<KanbanDto> result = new ArrayList<>();
		List<HrTask> tasks = hrTaskRepository.getByProjectActivity(id);
		if (tasks != null && tasks.size() > 0) {
			for (HrTask hrTask : tasks) {
				KanbanDto item = new KanbanDto(hrTask);
				result.add(item);
			}
		}
		return result;
	}

	@Override
	public List<KanbanDto> getAllTaskCreatedUpdatedInRangeFromToDate(SearchTimeSheetDto searchTimeSheetDto) {
		if (searchTimeSheetDto == null || searchTimeSheetDto.getToDate() == null
				|| searchTimeSheetDto.getFromDate() == null || searchTimeSheetDto.getStaffId() == null)
			return null;

		String STATUS_SIGN = "\"status\":{\"";
		String COMPLETED_STATUS = "\":\"Completed\"";
		String RESOLVED_STATUS = "\":\"Resolved\"";

		String hql = "SELECT distinct new com.globits.task.dto.KanbanDto(task) " + "FROM HrTask as task "
				+ "inner join HrTaskHistory history on history.task.id = task.id "
				+ "where history.modifier.id = :staffId and "
				+ "(DATE(history.createDate) >= DATE(:fromDate) and DATE(history.createDate) <= DATE(:toDate)) ";

//        +"and history.event like '%" + STATUS_SIGN + "%' and " +
//                "(history.event like '%" + RESOLVED_STATUS + "%' or history.event like '%" + COMPLETED_STATUS + "%')";

		Query query = manager.createQuery(hql, KanbanDto.class);
		query.setParameter("staffId", searchTimeSheetDto.getStaffId());
		query.setParameter("fromDate", searchTimeSheetDto.getFromDate());
		query.setParameter("toDate", searchTimeSheetDto.getToDate());

		List<KanbanDto> result = query.getResultList();
		return result;
	}
}
