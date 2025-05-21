import React, { useState, useEffect, memo, useMemo } from "react";
import { Grid, makeStyles } from "@material-ui/core";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import LocalConstants from "app/LocalConstants";
import { formatDate, formatMoney, formatVNDMoney, getFirstDateOfWeek, getLastDateOfWeek } from "app/LocalFunction";
import { IconButton, Icon, Tooltip } from "@material-ui/core";
import SalaryBoardTotalRow from "../SalaryResultDetail/SalaryBoardTotal/SalaryBoardTotalRow";
import GlobitsPagination from "app/common/GlobitsPagination";
import { useHistory } from "react-router-dom";
import ConstantList from "../../../appConfig";
import CompareArrowsIcon from "@material-ui/icons/CompareArrows";
import { useParams } from "react-router-dom/cjs/react-router-dom";
import ParyollBoardPopup from "./HistoryTimeSheetDetailPopup";
import StaffWorkScheduleV2CUForm from "../../StaffWorkScheduleV2/StaffWorkScheduleV2CUForm";
import StaffWorkScheduleStatisticPopup from "../../StaffWorkScheduleV2/StaffWorkScheduleStatisticPopup";
import HistoryIcon from '@material-ui/icons/History';
import AssignmentIcon from '@material-ui/icons/Assignment';
import { toast } from "react-toastify";

const useStyles = makeStyles(() => ({
  root: {
    background: "#E4f5fc",
    padding: "10px 15px",
    borderRadius: "5px",
  },
  groupContainer: {
    width: "100%",
    "& .MuiOutlinedInput-root": {
      borderRadius: "0!important",
    },
  },
}));

function PayrollBoard() {
  const { t } = useTranslation();
  const history = useHistory();
  const classes = useStyles();
  const { id: salaryResultId } = useParams();

  const {
    payrollStore,
    salaryStaffPayslipStore,
    staffWorkScheduleStore,
    staffSalaryTemplateStore
  } = useStore();

  const {
    onViewSalaryResult,
    handleChangePage,
    totalElements,
    totalPages,
    searchObject,
    setPageSize,
    listSalaryResultStaffs,
    openSchedulePopup,
    handleSetOpenSchedulePopup
  } = payrollStore;

  const {
    findStaffTemplateIdByStaffIdAndTemplateId,
    handleOpenView
  } = staffSalaryTemplateStore;

  const {
    openViewStatistic,
    openViewPopup,
    openCreateEditPopup,
    handleGetTotalStaffWorkSchedule
  } = staffWorkScheduleStore;

  const {
    handleOpenCreateEdit,
    handleOpenRecalculatePayslip
  } = salaryStaffPayslipStore;

  function getHardCodeColumns() {
    return [
      {
        displayName: "Mã NV",
        field: "staffCode",
      },
      {
        displayName: "Họ và tên",
        field: "staffName",
      },
      {
        displayName: "Đơn vị",
        field: "mainOrganization",
      },
      {
        displayName: "Phòng ban",
        field: "mainDepartment",
      },
      {
        displayName: "Chức danh",
        field: "mainPositionTitle",
      },
      // {
      //     displayName: "Vị trí",
      //     field: "mainPosition",
      // },
    ];
  }

  const columnHeaders = useMemo(function () {
    const columnGroups = [];
    const remainItems = [];

    const data = JSON.parse(JSON.stringify(onViewSalaryResult));

    data?.templateItems?.forEach(function (item) {
      if (!item?.hiddenOnSalaryBoard) {
        if (item.templateItemGroupId == null) {
          // this is a common column has rowspan = 2
          let columnItem = JSON.parse(JSON.stringify(item));
          columnItem.isItem = true;

          columnGroups.push(columnItem);
        } else {
          // these code below handle for column group and its item

          // 1st case: the group existed right before => merge column with old consecutive before
          if (columnGroups.length > 0 && columnGroups[columnGroups.length - 1].id == item.templateItemGroupId) {
            columnGroups[columnGroups.length - 1].colSpan++;
          }
          // 2nd case: the group is not appear right before => add new group
          else {
            let group = data?.templateItemGroups.find(function (group) {
              return group.id == item.templateItemGroupId;
            });
            group = JSON.parse(JSON.stringify(group));
            group.colSpan = 1;

            columnGroups.push(group);
          }

          remainItems.push(JSON.parse(JSON.stringify(item)));
        }
      }
    });

    return {
      columnGroups,
      remainItems
    };
  }, [
    onViewSalaryResult?.templateItemGroups,
    onViewSalaryResult?.templateItems
  ]);

  const [staffId, setStaffId] = useState(null);

  const handleChangeRouter = (staffId) => {
    setStaffId(staffId)
    handleSetOpenSchedulePopup(true)
    // let redirectUrl = ConstantList.ROOT_PATH + `history-time-sheet-detail/${staffId}`;
    // history.push (redirectUrl);
  };

  useEffect(() => {
    if (!openCreateEditPopup) {
      handleGetTotalStaffWorkSchedule()
    }
  }, [openCreateEditPopup]);


  async function handleViewStaffTemplate(staffId) {
    try {
      const payload = {
        staffId,
        salaryTemplateId: onViewSalaryResult?.salaryTemplate?.id
      };

      const response = await findStaffTemplateIdByStaffIdAndTemplateId(payload);

      handleOpenView(response);
    }
    catch (error) {
      toast.error("Không lấy được mẫu bảng lương áp dụng cho nhân viên");
      console.error(error);
    }
  }

  return (
    <>
      <Grid container spacing={2}>
        <Grid item xs={12}>
          <section className="commonTableContainer">
            <table className={`commonTable w-100`}>
              <thead>
                <tr className="tableHeader">
                  <th rowSpan={2} align="center" style={{ width: "40px" }}
                    className="stickyCell stickyHeader"
                  >
                    Thao tác
                  </th>

                  {getHardCodeColumns()?.map(function (column, index) {
                    return (
                      <React.Fragment key={index}>
                        <th
                          rowSpan={2}
                          align="center"
                          style={{ minWidth: "120px" }}
                          className="stickyHeader"
                        >
                          {column?.displayName}
                        </th>
                      </React.Fragment>
                    );
                  })}

                  {columnHeaders.columnGroups.map(function (column, index) {

                    return (
                      <React.Fragment key={index}>
                        {column?.isItem && (
                          <th
                            rowSpan={2}
                            align="center"
                            style={{ minWidth: "120px" }}
                            className="stickyHeader"
                          >
                            {column?.displayName}
                          </th>
                        )}

                        {!column?.isItem && (
                          <th
                            colSpan={column?.colSpan}
                            className="stickyHeader"
                            align="center"
                          >
                            {column?.name}
                          </th>
                        )}
                      </React.Fragment>
                    );
                  })}
                </tr>

                <tr className="tableHeader">
                  {columnHeaders.remainItems.map(function (column, index) {

                    return (
                      <th key={index} align="center" style={{ minWidth: "120px" }}>
                        {column?.displayName}
                      </th>
                    );
                  })}

                </tr>
              </thead>

              <tbody>
                {
                  listSalaryResultStaffs?.map(function (resultStaff, index) {
                    return (
                      <tr key={index} className={`row-table-body row-table-no_data`}>
                        <td className="stickyCell">
                          <div className="flex flex-middle justify-center">
                            <Tooltip
                              title="Xem phiếu lương nhân viên"
                              arrow
                              placement="top"
                            >
                              <IconButton className="bg-white" size="small"
                                onClick={() => {
                                  handleOpenCreateEdit(resultStaff?.id);
                                }}
                              >
                                <Icon fontSize="small" style={{ color: "gray" }}
                                >
                                  remove_red_eye
                                </Icon>
                              </IconButton>
                            </Tooltip>

                            {
                              !resultStaff?.isLocked && (
                                <Tooltip
                                  arrow
                                  title="Tính lại phiếu lương"
                                  placement="top"
                                >
                                  <IconButton className="bg-white ml-4" size="small"
                                    onClick={() => {
                                      handleOpenRecalculatePayslip(resultStaff?.id);
                                    }}
                                  >
                                    <Icon fontSize="small" color="primary"
                                    >
                                      monetization_on
                                    </Icon>
                                  </IconButton>
                                </Tooltip>
                              )
                            }

                            <Tooltip
                              title="Chi tiết mẫu bảng lương áp dụng"
                              placement="top"
                              arrow
                            >
                              <IconButton
                                className="bg-white ml-4"
                                size="small"
                                onClick={() => handleViewStaffTemplate(resultStaff?.staff?.id)}
                              >
                                <AssignmentIcon
                                  style={{ color: "orange", fontSize: "20px" }}
                                />
                              </IconButton>
                            </Tooltip>

                            <Tooltip
                              title="Lịch làm việc và các lần chấm công trong kỳ lương"
                              placement="top"
                              arrow
                            >
                              <IconButton
                                className="bg-white ml-4"
                                size="small"
                                onClick={() => {
                                  handleChangeRouter(resultStaff?.staff?.id);
                                }}
                              >
                                <HistoryIcon
                                  style={{ color: "green", fontSize: "20px" }}
                                />
                              </IconButton>
                            </Tooltip>


                          </div>
                        </td>

                        {getHardCodeColumns()?.map(function (column, index) {
                          return (
                            <td key={index} className={`px-6 center no-wrap-text`}>
                              {resultStaff[`${column?.field}`]}
                            </td>
                          );
                        })}

                        {
                          resultStaff?.salaryResultStaffItems?.map(function (item, jndex) {
                            if (!item?.salaryTemplateItem?.hiddenOnSalaryBoard) {
                              let align = "";
                              let displayValue = item?.value;

                              if (item?.referenceCode === LocalConstants.SalaryItemCodeSystemDefault.STT_SYSTEM.value) {
                                align = "text-center";
                              } else if (item?.valueType === LocalConstants.SalaryItemValueType.MONEY.value
                                || item?.valueType === LocalConstants.SalaryItemValueType.NUMBER.value
                                || item?.valueType === LocalConstants.SalaryItemValueType.PERCENT.value
                              ) {
                                align = "text-align-right";
                                if (displayValue) {
                                  displayValue = formatVNDMoney(displayValue);
                                }
                              }
                              // else if (item?.valueType === LocalConstants.SalaryItemValueType.NUMBER.value) {
                              //    align = "text-align-right";
                              //}

                              return (
                                <td key={index + "_" + jndex}
                                  className={`px-6 ${align ? align : ""} no-wrap-text`}>
                                  {displayValue}
                                </td>
                              );
                            }
                          })
                        }
                      </tr>
                    );
                  })
                }

                <SalaryBoardTotalRow
                  getHardCodeColumns={getHardCodeColumns}
                />

              </tbody>
            </table>
          </section>
        </Grid>

        <Grid item xs={12}>
          <GlobitsPagination
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={searchObject.pageSize}
            pageSizeOption={[15, 25, 50, 100, 200, 500]}
            totalElements={totalElements}
            page={searchObject.pageIndex}
          />
        </Grid>
      </Grid>

      {openSchedulePopup && (<ParyollBoardPopup staffId={staffId} />)}

      {openCreateEditPopup && <StaffWorkScheduleV2CUForm />}

      {openViewPopup && <StaffWorkScheduleV2CUForm readOnly={true} />}

      {openViewStatistic && (<StaffWorkScheduleStatisticPopup />)}
    </>

  );
}

export default memo(observer(PayrollBoard));