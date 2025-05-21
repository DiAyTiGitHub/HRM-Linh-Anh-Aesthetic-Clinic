import { makeAutoObservable } from "mobx";
import {
  deleteHrResourcePlan,
  deleteMultiple,
  getHrResourcePlanById,
  pagingHrResourcePlan,
  saveHrResourcePlan,
  updateStatus,
  updateStatusByGeneralDirector,
  updateStatusByViceGeneralDirector,
  autoGenCode
} from "./HrResourcePlanService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import { HttpStatus } from "../../LocalConstants";

toast.configure ({
  autoClose:2000,
  draggable:false,
  limit:3,
});

export default class HrResourcePlanStore {

  intactSearchObject = {
    pageIndex:1,
    pageSize:10,
    keyword:null,
    positionTitle:null,
    positionTitleId:null,
    department:null,
    departmentId:null,
    organization:null,
    organizationId:null,
    getAggregate:null,
    viceGeneralDirectorStatus:null, // Trạng thái phó tổng giám đốc duyệt. Chi tiết: HrConstants.HrResourcePlanApprovalStatus
    generalDirectorStatus:null, // Trạng thái tổng giám đốc duyệt. Chi tiết: HrConstants.HrResourcePlanApprovalStatus
  };

  searchObject = JSON.parse (JSON.stringify (this.intactSearchObject));
  tabCU = 0;
  initialHrResourcePlan = {
    id:null,
    department:null,
    planNumber:null,
    currentNumber:null,
    positionTitle:null,
  };

  hrResourcePlanList = [];
  // aggregateHrResourcePlanList = [];
  totalElements = 0;
  totalPages = 0;
  page = 1;
  rowsPerPage = 10;
  keyword = "";
  loadingInitial = false;
  openConfirmAddHrResourcePlanPopup = true;
  openConfirmDeletePopup = false;
  openConfirmDeleteListPopup = false;
  openCreateEditPopup = false;
  openAggregateCreateEditPopup = false;
  listOnDelete = [];
  listForSelect = [];

  selectedHrResourcePlan = null;
  selectedHrResourcePlanList = [];
  openViewPopup = false;

  handleOpenView = async (hrResourcePlanId) => {
    try {
      if (hrResourcePlanId) {
        const {data} = await getHrResourcePlanById (hrResourcePlanId);
        this.selectedHrResourcePlan = data;
      } else {
        this.selectedHrResourcePlan = {
          ... this.initialHrResourcePlan,
        };
      }
      this.openViewPopup = true;
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };
  resetSearch = async () => {
    this.searchObject = {... this.intactSearchObject};

    await this.pagingHrResourcePlan ();
  };

  constructor () {
    makeAutoObservable (this);
  }

  resetStore = () => {
    this.searchObject = JSON.parse (JSON.stringify (this.intactSearchObject));
    this.totalElements = 0;
    this.totalPages = 0;
    this.hrResourcePlanList = [];
    this.openCreateEditPopup = false;
    this.selectedHrResourcePlan = null;
    this.openConfirmDeletePopup = false;
    this.openConfirmDeleteListPopup = false;
    this.openAggregateCreateEditPopup = false;
    this.listOnDelete = [];
    this.listForSelect = [];
    this.openViewPopup = false;
    this.openViceDirectorConfirmPopup = false;
    this.openDirectorConfirmPopup = false;
  };

  resetStoreNonClose = () => {
    this.totalElements = 0;
    this.totalPages = 0;
    this.listOnDelete = [];
  };

  handleCloseConfirmAddHrResourcePlanPopup = () => {
    this.openConfirmAddHrResourcePlanPopup = false;
  };

  handleOpenConfirmAddHrResourcePlanPopup = () => {
    this.openConfirmAddHrResourcePlanPopup = true;
  };

  pagingHrResourcePlan = async () => {
    try {
      const payload = {
        ... this.searchObject,
        // getAggregate: false,
      };
      const data = await pagingHrResourcePlan (payload);
      this.hrResourcePlanList = data.data.content;
      this.totalElements = data.data.totalElements;
      this.totalPages = data.data.totalPages;
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  // pagingAggregateHrResourcePlan = async () => {
  //     try {
  //         const payload = {
  //             ...this.searchObject,
  //             getAggregate: true,
  //         };
  //         const data = await pagingHrResourcePlan(payload);
  //         this.aggregateHrResourcePlanList = data.data.content;
  //         this.totalElements = data.data.totalElements;
  //         this.totalPages = data.data.totalPages;
  //     } catch (error) {
  //         console.error(error);
  //         toast.error(i18n.t("toast.error"));
  //     }
  // };

  setListForSelect = (data) => {
    this.listForSelect = data?.content;
    this.totalElements = data.totalElements;
    this.totalPages = data.totalPages;
  };

  getListHrResourcePlan = (searchObj) => {
    const payload = {
      pageIndex:1,
      pageSize:10,
      ... searchObj,
      getAggregate:false,
    };
    if (searchObj?.organization) {
      payload.organizationId = searchObj?.organization?.id;
    } else {
      payload.organizationId = null;
    }

    if (searchObj?.department) {
      payload.departmentId = searchObj?.department?.id;
    } else {
      payload.departmentId = null;
    }

    return pagingHrResourcePlan (payload) // Thêm `return` để trả về Promise
        .then (({data}) => {
          return data;
        })
        .catch ((err) => {
          console.log (err);
        });
  };

  setTabCU = (tab) => {
    this.tabCU = tab;
  };

  setPageIndex = async (page) => {
    this.searchObject.pageIndex = page;
    await this.pagingHrResourcePlan ();
  };

  setPageSize = async (event) => {
    this.searchObject.pageSize = event.target.value;
    this.searchObject.pageIndex = 1;
    await this.pagingHrResourcePlan ();
  };

  handleChangePage = async (event, newPage) => {
    await this.setPageIndex (newPage);
  };

  handleOpenCreateEdit = async (hrResourcePlanId, type) => {
    try {
      if (hrResourcePlanId) {
        const {data} = await getHrResourcePlanById (hrResourcePlanId);
        this.selectedHrResourcePlan = data;
      } else {
        this.selectedHrResourcePlan = {
          ... this.initialHrResourcePlan,
        };
      }
      if (type) {
        this.openAggregateCreateEditPopup = true;
      } else {
        this.openCreateEditPopup = true;
      }
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  handleClose = () => {
    this.openConfirmDeletePopup = false;
    this.openCreateEditPopup = false;
    this.openConfirmDeleteListPopup = false;
    this.openAggregateCreateEditPopup = false;
    this.openViewPopup = false;

    this.openViceDirectorConfirmPopup = false;
    this.openDirectorConfirmPopup = false;
    this.openConfirmStatusPopup = false;
  };

  handleDelete = (hrResourcePlan) => {
    this.selectedHrResourcePlan = {... hrResourcePlan};
    this.openConfirmDeletePopup = true;
  };

  handleDeleteList = () => {
    this.openConfirmDeleteListPopup = true;
  };

  handleConfirmDelete = async () => {
    try {
      const {data} = await deleteHrResourcePlan (this.selectedHrResourcePlan.id);
      toast.success (i18n.t ("toast.delete_success"));
      if (this.tabCU === 0) {
        await this.pagingHrResourcePlan ();
      } else if (this.tabCU === 1) {
        await this.pagingAggregateHrResourcePlan ();
      }
      this.handleClose ();
    } catch (error) {
      console.log (error);
      toast.warning (i18n.t ("toast.error"));
    }
  };

  handleConfirmDeleteList = async () => {
    try {
      const deleteData = [];

      for (let i = 0; i < this?.listOnDelete?.length; i++) {
        deleteData.push (this?.listOnDelete[i]?.id);
      }
      await deleteMultiple (deleteData);
      toast.success (i18n.t ("toast.delete_success"));

      if (this.tabCU === 0) {
        await this.pagingHrResourcePlan ();
      } else if (this.tabCU === 1) {
        await this.pagingAggregateHrResourcePlan ();
      }
      this.listOnDelete = [];

      this.handleClose ();
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  };

  handleSelectListDelete = (hrResourcePlans) => {
    this.listOnDelete = hrResourcePlans;
  };

  saveHrResourcePlan = async (hrResourcePlan) => {
    try {
      const {data} = await saveHrResourcePlan (hrResourcePlan);
      toast.success ("Thông tin yêu cầu định biên đã được lưu");
      this.handleClose ();
    } catch (error) {
      console.error (error);
      if (error.response.status == 409) {
        toast.error ("Mã yêu cầu định biên đã được sử dụng, vui lòng sử dụng mã yêu cầu định biên khác", {
          autoClose:5000,
          draggable:false,
          limit:5,
        });
      } else {
        toast.error (i18n.t ("toast.error"));
      }
    }
  };

  getHrResourcePlan = async (id) => {
    if (id != null) {
      try {
        const {data} = await getHrResourcePlanById (id);
        this.selectedHrResourcePlan = data;
        this.openCreateEditPopup = true;
      } catch (error) {
        console.log (error);
        toast.warning (i18n.t ("toast.error"));
      }
    } else {
      this.handleSelectHrResourcePlan (null);
    }
  };

  handleSelectHrResourcePlan = (dto) => {
    this.selectedHrResourcePlan = dto;
  };

  handleSetSearchObject = (searchObject) => {
    if (searchObject.department == null) {
      searchObject.departmentId = null;
    } else {
      searchObject.departmentId = searchObject.department.id;
    }
    if (searchObject.organization == null) {
      searchObject.organizationId = null;
    } else {
      searchObject.organizationId = searchObject.organization.id;
    }

    this.searchObject = {... searchObject};
  };

  // handleSelectListDelete = (deleteHrResourcePlan) => {
  //   this.listOnDelete = deleteHrResourcePlan;
  // };

  getSelectedIds = () => {
    const ids = [];
    this?.listOnDelete?.forEach (function (item) {
      ids.push (item?.id);
    });

    return ids;
  }

  onUpdateStatus = null;


  handleConfirmUpdateStatusByViceDirector = async () => {
    try {
      if (this?.listOnDelete?.length <= 0) {
        toast.error ("Không có yêu cầu nào được chọn");
        return;
      }

      if (this.onUpdateStatus == null) {
        throw new Error ("On update status in invalid");
      }
      const payload = {
        chosenRecordIds:this.getSelectedIds (),
        viceGeneralDirectorStatus:this.onUpdateStatus,
      };

      const {data} = await updateStatusByViceGeneralDirector (payload);
      if (!data) throw new Error ("");

      toast.success ("Cập nhật trạng thái thành công!");

      this.onUpdateStatus = null;

      this.handleClose ();
      await this.pagingHrResourcePlan ();
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  }


  handleConfirmUpdateStatusByDirector = async () => {
    try {
      if (this?.listOnDelete?.length <= 0) {
        toast.error ("Không có yêu cầu nào được chọn");
        return;
      }

      if (this.onUpdateStatus == null) {
        throw new Error ("On update status in invalid");
      }
      const payload = {
        chosenRecordIds:this.getSelectedIds (),
        generalDirectorStatus:this.onUpdateStatus,
      };

      const {data} = await updateStatusByGeneralDirector (payload);
      if (!data) throw new Error ("");

      toast.success ("Cập nhật trạng thái thành công!");

      this.onUpdateStatus = null;

      this.handleClose ();
      await this.pagingHrResourcePlan ();
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  }

  handleConfirmUpdateStatus = async () => {
    try {
      if (this?.listOnDelete?.length <= 0) {
        toast.error ("Không có yêu cầu nào được chọn");
        return;
      }

      if (this.onUpdateStatus == null) {
        throw new Error ("On update status in invalid");
      }
      const payload = {
        chosenRecordIds:this.getSelectedIds (),
        planApprovalStatus:this.onUpdateStatus,
      };

      const {data} = await updateStatus (payload);
      if (!data) throw new Error ("");

      toast.success ("Cập nhật trạng thái thành công!");

      this.onUpdateStatus = null;
      this.listOnDelete = [];

      this.handleClose ();
      await this.pagingHrResourcePlan ();
    } catch (error) {
      console.error (error);
      toast.error (i18n.t ("toast.error"));
    }
  }
  openViceDirectorConfirmPopup = false;
  openDirectorConfirmPopup = false;
  openConfirmStatusPopup = false;

  handleOpenViceDirectorConfirmPopup = async (status) => {
    this.onUpdateStatus = status;
    this.openViceDirectorConfirmPopup = true;
  }

  handleOpenDirectorConfirmPopup = async (status) => {
    this.onUpdateStatus = status;
    this.openDirectorConfirmPopup = true;
  }

  handleOpenConfirmStatusPopup = async (status) => {
    this.onUpdateStatus = status;
    this.openConfirmStatusPopup = true;
  }

  autoGenCode = async (configKey) => {
    const response = await autoGenCode (configKey)
    if (response.status === HttpStatus.OK) {
      return response.data;
    }
  }
}
