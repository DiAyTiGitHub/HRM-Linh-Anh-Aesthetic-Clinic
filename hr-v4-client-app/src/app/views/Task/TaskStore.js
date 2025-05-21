import { makeAutoObservable } from "mobx";
import { toast } from "react-toastify";
import { dataDefaultProject } from "../Project/ProjectStore";
import { getWorkingStatusWithTotalNOTasksByProject, pagingWorkingStatus } from "../WorkingStatus/WorkingStatusService";
import { deleteTask, getAllTask, getTask, pagingTask, getListByLimitTask, saveTask, exportExcelTaskByFilter, } from "./TaskService";
import _ from 'lodash';
import { pagingProject } from "../Project/ProjectService";
import i18n from "i18n";
import { v4 as uuid } from 'uuid';
import history from "../../../history";
import localStorageService from "app/services/localStorageService";
import { saveAs } from "file-saver";

const dataDefaultTaskForm = {
  activity: null,
  code: null,
  description: "",
  endTime: null,
  estimateHour: null,
  id: null,
  name: null,
  orderNumber: null,
  project: null,
  staffs: [],
  startTime: null,
  status: null,
  priority: 2,
  subTasks: []
}

export const dataAllProjectTask = { ...dataDefaultProject, name: 'Tất cả', id: "" }
export const dataDefaultProjectTask = [
  { ...dataDefaultProject, name: 'Tất cả dự án', id: uuid + '-all-project' },
  { ...dataDefaultProject, name: 'Không thuộc dự án', id: uuid + '-none-project' },
]

export default class TaskStore {
  listAllTask = [];
  listMoreTask = [];
  projectList = [];
  selectedTask = null;
  dataTaskForm = dataDefaultTaskForm;
  optionWorkingStatus = [];
  listTask = { columns: [], columnsPriority: [], listProject: [] };
  tabIndexTask = 0;
  listTaskKanban = [];
  listTaskPaging = []
  currentProject = dataAllProjectTask;
  pageIndex = 1;
  pageSize = 10;
  totalElements = 0;
  totalPage = 0;
  openPopup = false;
  openNewPopup = false;
  openConfirmDeleteTask = false;

  constructor() {
    makeAutoObservable(this);

    //reset field for kanban filter -> set keyword and staffId of filter is null
    const kanbanFilter = this.getKanbanFilter();
    this.setKanbanFilter({
      ...kanbanFilter,
      staffId: null,
      keyword: null
    });
  }

  handleChangeListTaskKanban = listTask => this.listTaskKanban = listTask.concat();

  handleChangeTabIndexTask = tabIndex => this.tabIndexTask = tabIndex;

  handleChangeCurrentProject = project => {
    let idProjectTask = '';
    if (project.id?.includes('all-project')) {
      idProjectTask = "all-project"
    } else if (project.id?.includes('none-project')) {
      idProjectTask = 'none-project'
    } else {
      idProjectTask = project.id
    }
    localStorage.setItem('id_project_task', JSON.stringify(idProjectTask))
    this.currentProject = project
  };

  handleDeleteTask = () => this.openConfirmDeleteTask = true

  resetTaskStore = () => {
    this.listAllTask = [];
    this.listMoreTask = [];
    this.selectedTask = null;
    this.dataTaskForm = dataDefaultTaskForm;
    this.optionWorkingStatus = [];
    this.listTask = { columns: [], columnsPriority: [], listProject: [] };
    this.tabIndexTask = 0;
    this.listTaskKanban = [];
    this.listTaskPaging = [];
    this.pageIndex = 1;
    this.pageSize = 10;
    this.totalElements = 0;
    this.totalPage = 0;
    this.openConfirmDeleteTask = false;
    this.openPopup = false;
    this.openNewPopup = false;
    this.availableProjectList = [];
    this.taskTableFilter = {
      keyword: null,
      staffId: null,
      workingStatusId: null,
      priority: null,
    };
    this.needReloadOnJoinStaff = true;
    this.needReloadKanbanHeaderTotalTasks = false;
  }

  getPagingTaskProject = async () => {
    try {
      //get all options working status first
      if (!this?.optionWorkingStatus || this.optionWorkingStatus?.length == 0) {
        const { data } = await pagingWorkingStatus({ pageIndex: 1, pageSize: 100, });
        this.optionWorkingStatus = data?.content?.filter(e => e?.statusValue !== null);
      }

      await this.pagingTaskTable();
    }
    catch (error) {
      console.error(error);
      toast.error("Có lỗi xảy ra khi tải dữ liệu");
    }

  }

  getAllWorkingStatus = async () => {
    try {
      const searchObject = {
        pageIndex: 1,
        pageSize: 1000
      };

      const { data } = await pagingWorkingStatus(searchObject);
      this.optionWorkingStatus = data?.content?.filter(e => e?.statusValue !== null);
    }
    catch (error) {
      console.error(error);
      toast.error("Lỗi xảy ra khi lấy dữ liệu trạng thái công việc");
    }
  }

  pagingTask = async (item) => {
    try {
      const chosenProjects = this.getOnViewProjects();
      const projectIdList = [];
      for (let i = 0; i < chosenProjects?.length; i++) {
        const project = chosenProjects[i];
        projectIdList.push(project?.id);
      }

      const searchObj = {
        ...item,
        // projectId: this?.currentProject?.id,
        ...this.taskTableFilter,
        pageIndex: this?.pageIndex,
        pageSize: this?.pageSize,
        projectIdList: projectIdList
      }

      for (let i = 0; i < projectIdList?.length; i++) {
        const projectId = projectIdList[i];

        //handle for paging all tasks of all projects or only task not in any project
        if (projectId?.includes('all-project') || projectId?.includes('none-project')) {
          searchObj.projectId = undefined;

          if (projectId?.includes('all-project')) {
            searchObj.tasksOfAllProjects = true;
          }
          else {
            searchObj.tasksOfAllProjects = false;
          }

          searchObj.projectIdList = undefined;
        }
      }

      const { data } = await pagingTask(searchObj);

      this.listTaskPaging = data?.content ? data.content : [];
      this.totalElements = data?.totalElements;
      this.totalPage = data?.totalPages;
    }
    catch (err) {
      console.error(err);
      toast.error("Có lỗi xảy ra khi lấy dữ liệu, vui lòng thử lại sau");
    }

  }

  search_data = (item) => {
    this.pageIndex = 1;
    if (item) {
      this.pagingTask(item);
    } else {
      this.pagingTask();
    }
  };

  getProject = async () => {
    var searchObject = {
      pageIndex: 1,
      pageSize: 1000,
    };

    try {
      let data = await pagingProject(searchObject);
      this.projectList = data?.data?.content;
    } catch (error) {
      toast.warning(i18n.t("toast.error"));
    }
  };

  getListByLimitTask = async () => {
    try {
      const newListData = [];
      const onViewProjectIdList = this.getOnViewProjectIdList();

      const kanbanFilter = this.getKanbanFilter();
      let searchObj = {
        ...kanbanFilter,
        pageIndex: 1,
        pageSize: 10,
        projectIdList: onViewProjectIdList
      }

      //handle for paging all tasks of all projects or only task not in any project
      for (let i = 0; i < onViewProjectIdList?.length; i++) {
        const projectId = onViewProjectIdList[i];

        if (projectId?.includes('all-project') || projectId?.includes('none-project')) {
          searchObj.projectIdList = undefined;

          if (projectId?.includes('all-project')) {
            searchObj.tasksOfAllProjects = true;
          }
          else if (projectId?.includes('none-project')) {
            searchObj.projectId = undefined;
            searchObj.tasksOfAllProjects = false;
          }
        }
      }

      //get all options working status first
      if (!this?.optionWorkingStatus || this.optionWorkingStatus.length == 0) {
        await this.getAllWorkingStatus();
      }

      const { data } = await getListByLimitTask(searchObj);

      const { data: listWorkingStatus } = await getWorkingStatusWithTotalNOTasksByProject(searchObj);

      this.optionWorkingStatus.map((item) => {
        const listCard = Array.isArray(data) ? data.filter(e => e?.statusId === item?.id) : [];

        //handle get total of task in this status
        let totalOfTasksInStatus = 0;
        listWorkingStatus?.forEach(function (status) {
          if (status?.id === item?.id) totalOfTasksInStatus = status?.totalOfTasksInStatus;
        });

        const kanbanColumn = {
          ...item,
          pageIndex: 1,
          last: listCard?.length < searchObj?.pageSize,
          cards: _.uniqBy(listCard, e => e?.id),
          totalOfTasksInStatus
        };

        newListData.push(kanbanColumn);
      });

      this.listTaskKanban = Array.isArray(newListData) ? newListData.sort((item1, item2) => item1?.statusValue - item2?.statusValue) : [];
    }
    catch (error) {
      console.error(error);
      toast.error("Có lỗi xảy ra khi lấy dữ liệu");
    }
  }

  //load 1 more kanban card if the number of task in changed column < 10 and can load more
  autoLoadMoreKanban = async (sourceColumnId) => {
    try {
      const newListTaskKanban = this?.listTaskKanban;
      const indexItem = newListTaskKanban?.findIndex(e => e?.id === sourceColumnId);
      // console.log("need load more column: ", newListTaskKanban[indexItem]);

      if (!(!newListTaskKanban[indexItem]?.last && newListTaskKanban[indexItem]?.cards?.length < 10)) {
        // is not acquired full condition to load more task
        // when there's no more task (last = true) or number of card is >=10
        // console.log("catched in return condition")
        return;
      }

      const onViewProjectIdList = this.getOnViewProjectIdList();

      const kanbanFilter = this.getKanbanFilter();
      const searchObj = {
        ...kanbanFilter,
        workingStatusId: sourceColumnId,
        pageIndex: 10, // load the 10th card 
        pageSize: 1, //load only that card
        projectIdList: onViewProjectIdList
      }

      //handle for paging all tasks of all projects or only task not in any project
      for (let i = 0; i < onViewProjectIdList?.length; i++) {
        const projectId = onViewProjectIdList[i];

        if (projectId?.includes('all-project') || projectId?.includes('none-project')) {
          searchObj.projectId = undefined;
          searchObj.projectIdList = undefined;

          if (projectId?.includes('all-project')) {
            searchObj.tasksOfAllProjects = true;
          }
        }
      }

      //1 more new task is in data
      const { data } = await pagingTask(searchObj);
      // console.log("1 more task data: ", data);
      const newCards = [...newListTaskKanban[indexItem]?.cards, ...data?.content];
      // console.log("new cards: ", newCards);

      newListTaskKanban[indexItem] = {
        ...newListTaskKanban[indexItem],
        // last: data?.last,
        cards: _.uniqBy(newCards, e => e?.id),
      }

      this.handleChangeListTaskKanban(newListTaskKanban);

    }
    catch (error) {
      console.error(error);
      toast.error("Có lỗi xảy ra khi lấy dữ liệu");
      this.needReloadKanbanHeaderTotalTasks = false;

    }
  }

  needReloadKanbanHeaderTotalTasks = false;

  setNeedReloadKanbanHeader = status => {
    this.needReloadKanbanHeaderTotalTasks = status;
  }

  reloadKanbanHeaderTotalTasks = async () => {
    try {
      const newListData = [];
      const onViewProjectIdList = this.getOnViewProjectIdList();

      const kanbanFilter = this.getKanbanFilter();
      let searchObj = {
        ...kanbanFilter,
        // pageIndex: 1,
        // pageSize: 10,
        projectIdList: onViewProjectIdList
      }

      //handle for paging all tasks of all projects or only task not in any project
      for (let i = 0; i < onViewProjectIdList?.length; i++) {
        const projectId = onViewProjectIdList[i];

        if (projectId?.includes('all-project') || projectId?.includes('none-project')) {
          searchObj.projectId = undefined;
          searchObj.projectIdList = undefined;

          if (projectId?.includes('all-project')) {
            searchObj.tasksOfAllProjects = true;
          }
        }
      }

      //get all options working status first
      if (!this?.optionWorkingStatus || this.optionWorkingStatus.length == 0) {
        this.getAllWorkingStatus();
      }

      const { data: listWorkingStatus } = await getWorkingStatusWithTotalNOTasksByProject(searchObj);

      this.optionWorkingStatus.map((item) => {
        //map old list card items
        let listCard = [];
        let oldKanbanColumn = null;
        for (let i = 0; i < this?.listTaskKanban?.length; i++) {
          const kanbanColumn = this.listTaskKanban[i];

          if (kanbanColumn?.id == item?.id) {
            listCard = kanbanColumn?.cards;
            oldKanbanColumn = kanbanColumn;
            break;
          }
        }

        //handle get total of task in this status
        let totalOfTasksInStatus = 0;
        listWorkingStatus?.forEach(function (status) {
          if (status?.id === item?.id) totalOfTasksInStatus = status?.totalOfTasksInStatus;
        });

        const kanbanColumn = {
          ...oldKanbanColumn,
          ...item,
          // last: listCard?.length < searchObj?.pageSize,
          cards: _.uniqBy(listCard, e => e?.id),
          totalOfTasksInStatus
        };

        newListData.push(kanbanColumn);
      });

      this.listTaskKanban = Array.isArray(newListData) ? newListData.sort((item1, item2) => item1?.statusValue - item2?.statusValue) : [];
      this.needReloadKanbanHeaderTotalTasks = false;

    }
    catch (error) {
      console.error(error);
      toast.error("Có lỗi xảy ra khi lấy dữ liệu");
      this.needReloadKanbanHeaderTotalTasks = false;

    }
  }

  getAllTask = async (id) => {
    try {
      const response = await getAllTask({ projectId: id, pageIndex: 1, pageSize: 10 });

      this.listAllTask = response?.data;

      const responseStatus = await pagingWorkingStatus({ pageIndex: 1, pageSize: 100, });
      this.optionWorkingStatus = responseStatus.data.content;
    } catch (error) {
      toast.error("Đã có lỗi xảy !", "Thất bại!");
    }
  };

  getMoreTaskByStatus = async (id) => {
    try {
      const newListTaskKanban = this?.listTaskKanban;
      const indexItem = newListTaskKanban?.findIndex(e => e?.id === id);
      const onViewProjectIdList = this.getOnViewProjectIdList();

      const kanbanFilter = this.getKanbanFilter();
      const searchObj = {
        ...kanbanFilter,
        workingStatusId: id,
        pageIndex: newListTaskKanban[indexItem]?.pageIndex + 1,
        pageSize: 10,
        projectIdList: onViewProjectIdList
      }

      //handle for paging all tasks of all projects or only task not in any project
      for (let i = 0; i < onViewProjectIdList?.length; i++) {
        const projectId = onViewProjectIdList[i];

        if (projectId?.includes('all-project') || projectId?.includes('none-project')) {
          searchObj.projectId = undefined;
          searchObj.projectIdList = undefined;

          if (projectId?.includes('all-project')) {
            searchObj.tasksOfAllProjects = true;
          }
        }
      }

      const { data } = await pagingTask(searchObj);
      const newCards = [...newListTaskKanban[indexItem]?.cards, ...data?.content];

      newListTaskKanban[indexItem] = {
        ...newListTaskKanban[indexItem],
        pageIndex: searchObj?.pageIndex,
        last: data?.last,
        cards: _.uniqBy(newCards, e => e?.id),
      }

      this.handleChangeListTaskKanban(newListTaskKanban);
    }
    catch (error) {
      console.error(error);
      toast.error("Có lỗi xảy ra khi tải dữ liệu, vui lòng thử lại sau");
    }

  };

  handleConfirmDeleteTask = () => {
    deleteTask(this.dataTaskForm?.id).then((response) => {
      if (this.tabIndexTask === 0) {
        if (this.listTaskPaging?.length > 1) {
          this.getPagingTaskProject();
        } else {
          this.handleChangePageIndex(null, this?.totalPage)
        }
      } else {
        const newListTaskKanban = this?.listTaskKanban;
        const indexCards = newListTaskKanban?.findIndex(e => e?.id === this.dataTaskForm?.status?.id);
        newListTaskKanban[indexCards].cards = newListTaskKanban[indexCards]?.cards.filter(e => e?.id !== this.dataTaskForm?.id);
        this.handleChangeListTaskKanban(newListTaskKanban);

        this.needReloadKanbanHeaderTotalTasks = true;
      }
      this.handleClose();
    }).catch(() => {
      toast.error("Xóa không thành công!", "Thất bại");
    });
  }

  handleOpenTaskPopup = async (taskId, statusFromColumn) => {
    try {
      //is old task
      if (taskId) {
        const { data } = await getTask(taskId);

        // const subTasks = !Array.isArray(data?.subTasks) ? [] : data?.subTasks?.map((item) => {
        //   let numberCompleted = 0;
        //   if (Array.isArray(item?.items)) item?.items?.map((e) => e?.value ? numberCompleted += 1 : null);

        //   return { ...item, numberCompleted, };
        // });

        this.dataTaskForm = {
          ...data,
          // subTasks,
          description: data?.description || '',
        };

      }
      //is new task
      else {
        let toDoStatus = null;
        if (statusFromColumn) toDoStatus = statusFromColumn;
        else {
          if (this?.optionWorkingStatus?.length == 0) await this.getAllWorkingStatus();

          this?.optionWorkingStatus?.forEach(function (status) {
            if (status?.name == "Todo") toDoStatus = status;
          });
        }

        const dataForm = {
          ...dataDefaultTaskForm,
          project: this.currentProject.id ? this.currentProject : null,
          status: toDoStatus
        };

        //handle for filling project when only 1 project is selected
        const chosenProjects = this.getOnViewProjects();
        if (chosenProjects && chosenProjects?.length == 1) {
          dataForm.project = chosenProjects[0];
        }

        //except for case select all project or none project option :v
        if (dataForm?.project?.id?.includes('all-project') || dataForm?.project?.id?.includes('none-project')) {
          dataForm.project = undefined;
        }

        this.dataTaskForm = dataForm;
      }

      this.openNewPopup = true;

    } catch (error) {
      toast.error("Lấy dữ liệu công việc có lỗi");
      console.error(error);
    }
  }

  handleSubmitFormTask = async (values) => {
    try {
      const newValues = values?.staffs && values?.staffs?.length > 0 ? {
        ...values,
        staffs: values?.staffs?.map((item) => ({ id: item?.id }))
      } : values;

      const { data } = await saveTask(newValues);

      // // Nếu ko có project và project hiện tại ko phải là all-project và none-project thì tự động nhảy sang all-project
      // if (!data?.project && !this.currentProject?.id?.includes('all-project') && !this.currentProject?.id.includes('none-project')) {
      //   history.push('/task/kanban/all-project');
      //   return;
      // }

      // if (data?.project && data.project?.id
      //   //  !== this.currentProject.id
      // ) {
      //   // Nếu project hiện tại và project được tạo task khác nhau thì nhảy sang project vừa được tạo task
      //   history.push('/task/kanban/' + data?.project?.id);
      //   return;
      // }

      // Refresh trang khi tạo mới và edit task khi đang ở task table
      if (this.tabIndexTask === 0) {
        await this.getPagingTaskProject();
      }

      // Refresh trang khi tạo mới và edit task khi đang ở tab kanban
      if (this.tabIndexTask === 1) {
        //this is list kanban column
        const newListTaskKanban = this.listTaskKanban;

        //find column to update by its index
        const columnIndex = newListTaskKanban?.findIndex(e => e?.id === values?.status?.id);
        const kanbanCard = this.convertToKanbanCard(data);

        //is old task => update old ticket in kanban table
        if (values?.id) {
          if (values?.status?.id === this?.dataTaskForm?.status?.id) {
            const indexItemCard = newListTaskKanban[columnIndex]?.cards?.findIndex(e => e?.id === data?.id);
            newListTaskKanban[columnIndex].cards[indexItemCard] = kanbanCard;
          } else {
            const indexCardsOld = newListTaskKanban.findIndex(e => e?.id === this?.dataTaskForm?.status?.id);
            newListTaskKanban[indexCardsOld].cards = newListTaskKanban[indexCardsOld]?.cards?.filter(e => e?.id !== data?.id);
            newListTaskKanban[columnIndex].cards = [kanbanCard, ...newListTaskKanban[columnIndex]?.cards];
          }
        }
        // create new task => create new kanban ticket in kanban table
        else {
          newListTaskKanban[columnIndex].cards = [kanbanCard, ...newListTaskKanban[columnIndex]?.cards]
        }

        this.handleChangeListTaskKanban(newListTaskKanban);
        this.needReloadKanbanHeaderTotalTasks = true;
      }

      toast.success("Cập nhật công việc thành công", "Thành công!");
      this.handleClose();
    }
    catch (error) {
      toast.error("Có lỗi xảy ra, vui lòng thử lại sau!", "Thất bại!");
      console.error(error);
    }
  }

  convertToKanbanCard = (task) => {
    const kanbanCard =
    {
      id: task?.id,
      assignee: task?.assignee?.displayName,
      activity: task?.activity?.name,
      projectName: task?.project?.name,
      projectCode: task?.project?.code,
      name: task?.name,
      code: task?.code,
      startTime: task?.startTime,
      endTime: task?.endTime,
      statusId: task?.status?.id,
      statusName: task?.status?.name,
      priority: task?.priority,
      lastModifyDate: task?.lastModifyDate
    }

    return kanbanCard;
  }

  handleChangePageIndex = (_, pageIndex) => {
    this.pageIndex = pageIndex;
    this.getPagingTaskProject()
  }

  handleChangPageSize = (event) => {
    this.pageIndex = 1;
    this.pageSize = event.target.value;
    this.getPagingTaskProject();
  }

  handleClose = () => {
    this.openPopup = false;
    this.openNewPopup = false;
  };

  handleClosePopupConfirmDelete = () => this.openConfirmDeleteTask = false;

  setRowsPerPage = (event) => {
    this.pageSize = event.target.value;
    this.pageIndex = 1;
    this.getPagingTaskProject();
  };

  handleChangePage = (event, newPage) => {
    this.pageIndex = newPage;
    this.getPagingTaskProject();
  };

  getPriorityTitleAndColor = priority => {
    if (!priority) {
      return {
        color: "white",
        title: "Chưa đặt"
      };
    }

    if (priority == 1) {
      return {
        color: "rgb(10 255 15)",
        title: "Thấp"
      };
    }

    if (priority == 2) {
      return {
        color: "#5cafe5",
        title: "Trung bình"
      };
    }

    if (priority == 3) {
      return {
        color: "#e19e2e",
        title: "Cao"
      };
    }

    if (priority == 4) {
      return {
        color: "rgba(218, 78, 53, 0.925)",
        title: "Cấp bách"
      };
    }

    return {
      color: "white",
      title: "Chưa đặt"
    };
  }






  // handle for kanban filter
  initialKanbanFilter = {
    keyword: null,
    staffId: null,
    increasingCodeOrder: false,
    increasingPriorityOrder: false,
    increasingLastModifyDate: false,
    projectActivityId: null,
    project: null,
    projectActivity: null,
    projectId: null,
    fromDate: null,
    toDate: null,
    fromDateUpdate: null,
    toDateUpdate: null,
    staff: null,
    includeChildrenActivities: false,
    priority: null,
  }

  getKanbanFilter = () => {
    const currentLoginUserId = localStorageService
      .getLoginUser()?.id;

    let kanbanFilter = JSON.parse(localStorage.getItem("kanbanFilter_user_" + currentLoginUserId));
    if (!kanbanFilter) {
      kanbanFilter = {
        ...this.initialKanbanFilter
      }
    }

    return kanbanFilter;
  }

  setKanbanFilter = (kanbanFilter) => {
    const currentLoginUserId = localStorageService
      .getLoginUser()?.id;

    if (kanbanFilter != null) {
      //set staffId
      if (kanbanFilter?.staff && kanbanFilter?.staff?.id) {
        kanbanFilter.staffId = kanbanFilter?.staff?.id;
      }
      else {
        kanbanFilter.staff = null;
        kanbanFilter.staffId = null;
      }
      //set projectId
      if (kanbanFilter?.project && kanbanFilter?.project?.id) {
        kanbanFilter.projectId = kanbanFilter?.project?.id;
      }
      else {
        kanbanFilter.project = null;
        kanbanFilter.projectId = null;
      }
      //set activityId
      if (kanbanFilter?.projectActivity && kanbanFilter?.projectActivity?.id) {
        kanbanFilter.projectActivityId = kanbanFilter?.projectActivity?.id;
      }
      else {
        kanbanFilter.projectActivity = null;
        kanbanFilter.projectActivityId = null;
      }

      // console.log("onsave kanban filter: ", kanbanFilter)
      localStorage.setItem("kanbanFilter_user_" + currentLoginUserId, JSON.stringify(kanbanFilter));
    }
  }





  //HANDLE FOR CHOOSING MULTIPLE PROJECT TO DISPLAY
  availableProjectList = [];

  handleSaveViewingProjects = async (values) => {
    this.setOnViewProjects(values);

    if (this.tabIndexTask == 0) {
      //if this is tab TASK TABLE
      this.handleChangeTablePageIndex(null, 1);
      await this.pagingTaskTable();
    }
    else if (this.tabIndexTask == 1) {
      //if this is tab KANBAN
      //load kanban data when switching projects
      const onViewProjects = this.getOnViewProjects();

      if (onViewProjects) {
        await this.getListByLimitTask();
      }
      else {
        console.error("NO PROJECT IS CHOOSEN TO DISPLAY");
      }
    }

    //reload staff joining chosen projects
    this.needReloadOnJoinStaff = true;
  }

  getAllProjectsForChooseMultiplePopover = async (searchWrapper) => {
    try {
      const searchObject = {
        ...searchWrapper,
        pageIndex: 1,
        pageSize: 1000,
      };

      const { data } = await pagingProject(searchObject);

      if (!searchWrapper || searchWrapper?.keyword?.length === 0) {
        //insert 2 option all project and none project only in case there's not search object
        this.availableProjectList = [...dataDefaultProjectTask, ...(Array.isArray(data?.content) ? data.content : [])];
      } else {
        this.availableProjectList = Array.isArray(data?.content) ? data.content : [];
      }


      // if (!searchWrapper || searchWrapper?.keyword?.length == 0 || !searchWrapper?.keyword) {
      //   //insert 2 option all project and none project only in case there's not search object
      //   this.availableProjectList = [...dataDefaultProjectTask, ...data?.content];
      // } else {
      //   this.availableProjectList = data?.content;
      // }
    }
    catch (error) {
      console.error(error);
      toast.error("Có lỗi xảy ra khi lấy dữ liệu các dự án!");
    }
  }


  getOnViewProjects = () => {
    const currentLoginUserId = localStorageService
      .getLoginUser()?.id;

    let onViewProjects = JSON.parse(localStorage.getItem("onViewProjects_user_" + currentLoginUserId));
    if (!onViewProjects) {
      onViewProjects = [
        { ...dataDefaultProject, name: 'Tất cả dự án', id: uuid + '-all-project' }
      ];
    }

    return onViewProjects;
  }

  getOnViewProjectIdList = () => {
    const projectList = this.getOnViewProjects();
    const idList = [];

    projectList.forEach(function (project) {
      idList.push(project?.id);
    });

    return idList;
  }

  setOnViewProjects = (onViewProjects) => {
    if (onViewProjects != null) {
      //OLD LOGIC: reset project activity when choose other project
      // const kanbanFilter = this.getKanbanFilter();
      // kanbanFilter.projectActivity = null;
      // kanbanFilter.projectActivityId = null;

      //LOGIC 14/8/2024: reset all filter when changing on view projects
      const kanbanFilter = JSON.parse(JSON.stringify(this.initialKanbanFilter));

      this.setKanbanFilter(kanbanFilter);

      const currentLoginUserId = localStorageService.getLoginUser()?.id;

      localStorage.setItem("onViewProjects_user_" + currentLoginUserId, JSON.stringify(onViewProjects));
    }
  }

  handleRenderChosenProjectNames = () => {
    //this function is for rendering names of choosen projects to view
    let allNames = "";
    const chosenProjects = this.getOnViewProjects();
    for (let i = 0; i < chosenProjects?.length; i++) {
      const project = chosenProjects[i];

      if (allNames.length > 0) {
        allNames += ", ";
      }

      allNames += project?.name;
    }

    if (allNames.length == 0) allNames = "Chưa chọn dự án";
    return allNames;
  }


  //HANDLING FOR TASKTABLE
  taskTableFilter = {
    keyword: null,
    staffId: null,
    workingStatusId: null,
    priority: null,
  }
  needReloadOnJoinStaff = true;

  setTaskTableFilter = (filter) => {
    this.taskTableFilter = filter;

    this.pagingTask();
  }

  handleChangeTaskTableFilter = (filter) => {
    filter.staffId = filter?.staff?.id;

    this.pageIndex = 1;
    this.setTaskTableFilter(filter);
  }

  setNeedReloadOnJoinStaff = state => {
    this.needReloadOnJoinStaff = state;
  }


  // HANDLE EXPORT EXCEL TASK TABLE
  // AUTHORITY TO EXPORT EXCEL
  canExportExcel = () => {
    const roles =
      localStorageService
        .getLoginUser()
        ?.user?.roles?.map((item) => item.authority) || [];

    let hasAuthToCreateDelete = ["HR_MANAGER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN"];

    if (roles.some((role) => hasAuthToCreateDelete.indexOf(role) !== -1))
      return true;

    return false;
  }

  handleExportExcel = async () => {
    try {
      toast.info("Vui lòng đợi, yêu cầu đang được xử lý");

      const chosenProjects = this.getOnViewProjects();
      const projectIdList = [];
      for (let i = 0; i < chosenProjects?.length; i++) {
        const project = chosenProjects[i];
        projectIdList.push(project?.id);
      }

      const tableFilter = this.getKanbanFilter();

      const searchObj = {
        ...tableFilter,
        projectIdList: projectIdList
      }

      for (let i = 0; i < projectIdList?.length; i++) {
        const projectId = projectIdList[i];

        //handle for paging all tasks of all projects or only task not in any project
        if (projectId?.includes('all-project') || projectId?.includes('none-project')) {
          searchObj.projectId = undefined;

          if (projectId?.includes('all-project')) {
            searchObj.tasksOfAllProjects = true;
          }
          else {
            searchObj.tasksOfAllProjects = false;
          }

          searchObj.projectIdList = undefined;
        }
      }

      const { data } = await exportExcelTaskByFilter(searchObj);
      let blob = new Blob([data], {
        type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      });

      saveAs(blob, "DanhSachCongViecTheoBoLoc.xlsx");
      toast.success(i18n.t("general.successExport"));
    }
    catch (err) {
      console.error(err);
      toast.error("Có lỗi xảy ra khi thực hiện thao tác xuất Excel");
    }
  }



  //HANDLE FOR TASK TABLE FILTER
  pagingTaskTable = async () => {
    try {
      const chosenProjects = this.getOnViewProjects();
      const projectIdList = [];
      for (let i = 0; i < chosenProjects?.length; i++) {
        const project = chosenProjects[i];
        projectIdList.push(project?.id);
      }

      const tableFilter = this.getKanbanFilter();

      const searchObj = {
        ...tableFilter,
        pageIndex: this?.pageIndex,
        pageSize: this?.pageSize,
        projectIdList: projectIdList
      }

      for (let i = 0; i < projectIdList?.length; i++) {
        const projectId = projectIdList[i];

        //handle for paging all tasks of all projects or only task not in any project
        if (projectId?.includes('all-project') || projectId?.includes('none-project')) {

          if (projectId?.includes('all-project')) {
            searchObj.tasksOfAllProjects = true;
          }
          else {
            searchObj.projectId = undefined;
            searchObj.tasksOfAllProjects = false;
          }

          searchObj.projectIdList = undefined;
        }
      }

      const { data } = await pagingTask(searchObj);

      this.listTaskPaging = data?.content ? data.content : [];
      this.totalElements = data?.totalElements;
      this.totalPage = data?.totalPages;
    }
    catch (err) {
      console.error(err);
      toast.error("Có lỗi xảy ra khi lấy dữ liệu, vui lòng thử lại sau");
    }

  }

  handleChangeTablePageIndex = async (_, pageIndex) => {
    this.pageIndex = pageIndex;
    await this.pagingTaskTable();
  }

  handleChangeTablePageSize = async (event) => {
    this.pageIndex = 1;
    this.pageSize = event.target.value;
    await this.pagingTaskTable();
  }




  //4/7/2024 - NOW 2 SCREEN USING COMMON FILTER
  //these function to serve for this purpose

  loadTaskData = async () => {
    try {
      if (this.tabIndexTask == 0) {
        //user is using tab TABLE
        await this.pagingTaskTable();
      }
      else if (this.tabIndexTask == 1) {
        //user is using tab KANBAN
        await this.getListByLimitTask();
      }
    }
    catch (e) {
      console.error(e);
    }
  }

  isFilterChanged = () => {
    const currentValues = this.getKanbanFilter();
    // console.log("cathced isFilterChanged")
    if (currentValues?.staff != this.initialKanbanFilter?.staff) return true;
    if (currentValues?.staffId != this.initialKanbanFilter?.staffId) return true;
    if (currentValues?.projectActivityId != this.initialKanbanFilter?.projectActivityId) return true;
    if (currentValues?.project != this.initialKanbanFilter?.project) return true;
    if (currentValues?.projectActivity != this.initialKanbanFilter?.projectActivity) return true;
    if (currentValues?.fromDate != this.initialKanbanFilter?.fromDate) return true;
    if (currentValues?.toDate != this.initialKanbanFilter?.toDate) return true;
    if (currentValues?.fromDateUpdate != this.initialKanbanFilter?.fromDateUpdate) return true;
    if (currentValues?.toDateUpdate != this.initialKanbanFilter?.toDateUpdate) return true;
    if (currentValues?.includeChildrenActivities != this.initialKanbanFilter?.includeChildrenActivities) return true;
    if (currentValues?.priority != this.initialKanbanFilter?.priority) return true;

    return false;
  }
}
