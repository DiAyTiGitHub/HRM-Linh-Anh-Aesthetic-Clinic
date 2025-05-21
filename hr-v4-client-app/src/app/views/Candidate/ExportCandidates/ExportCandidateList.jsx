import React, {memo} from "react";
import GlobitsTable from "app/common/GlobitsTable";
import {useStore} from "app/stores";
import {useTranslation} from "react-i18next";
import {Icon, IconButton, Tooltip} from "@material-ui/core";
import {observer} from "mobx-react";
import {formatDate} from "app/LocalFunction";
import {useHistory} from "react-router-dom";
import ConstantList from "app/appConfig";
import LocalConstants from "app/LocalConstants";

function ExportCandidateList() {
    const {exportCandidateStore} = useStore();
    const {t} = useTranslation();

    const {
        listReportCandidates,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleSelectListChosen,
        getOnboardStatusName,
        mapTabToStatus,
        handleDelete
    } = exportCandidateStore;

    const history = useHistory();

    function handleUpdateApplicant(applicant) {
        //link to new page like a staff
        history.push(ConstantList.ROOT_PATH + `candidate/` + applicant?.id);
    }

    function handleUpdateStaff(applicant) {
        if (applicant?.staff?.id && LocalConstants.CandidateOnboardStatus.ONBOARDED.value == applicant.onboardStatus)
            history.push(ConstantList.ROOT_PATH + "staff/edit/" + applicant?.staff?.id);
    }

    const columns = [
        {
            title: t("general.action"),
            width: "10%",
            align: "center",
            minWidth: "100px",
            render: (rowData) => {
                return (
                    <div className="flex flex-middle justify-center">
                        <Tooltip title="Thông tin ứng viên" placement="top">
                            <IconButton size="small" onClick={function () {
                                handleUpdateApplicant(rowData);
                            }}>
                                <Icon fontSize="small" color="primary">
                                    remove_red_eye
                                </Icon>
                            </IconButton>
                        </Tooltip>

                        {rowData?.staff?.id && LocalConstants.CandidateOnboardStatus.ONBOARDED.value == rowData.onboardStatus && (
                            <Tooltip title="Thông tin nhân viên" placement="top">
                                <IconButton className="ml-8 text-green" size="small"
                                            onClick={() => handleUpdateStaff(rowData)}>
                                    <Icon fontSize="small" color="green">
                                        account_circle
                                    </Icon>
                                </IconButton>
                            </Tooltip>
                        )}

                        <Tooltip title="Xóa bản ghi ứng viên" placement="top">
                            <IconButton className="ml-8" size="small" onClick={() => handleDelete(rowData)}>
                                <Icon fontSize="small" color="secondary">
                                    delete
                                </Icon>
                            </IconButton>
                        </Tooltip>
                    </div>
                );
            },
        },
        {
            title: "Mã ứng viên",
            field: "candidateCode",
            minWidth: "150px",
        },
        {
            title: "Họ tên",
            field: "displayName",
            minWidth: "150px",
        },
        {
            title: "Ngày sinh",
            field: "birthDate",
            minWidth: "150px",
            render: (rowData) => (
                <span>
          {rowData?.birthDate && (formatDate("DD/MM/YYYY", rowData?.birthDate))}
        </span>
            ),
        },
        // {
        //   title: "Giới tính",
        //   field: "gender",
        //   render: rowData => (
        //     <span>
        //       {rowData?.gender === "M" ? "Nam" : rowData?.gender === "F" ? "Nữ" : ""}
        //     </span>
        //   )
        // },
        {
            title: "SĐT",
            field: "phoneNumber",
            minWidth: "150px",
            render: rowData => (
                <>
                    {rowData?.phoneNumber && (
                        <span className="pr-8">
              {rowData?.phoneNumber}
            </span>
                    )}
                </>
            )
        },
        {
            title: "Ngày nộp hồ sơ",
            field: "submissionDate",
            minWidth: "150px",
            render: (rowData) => (
                <span>
          {rowData?.submissionDate && (formatDate("DD/MM/YYYY", rowData?.submissionDate))}
        </span>
            ),
        },
        {
            title: "Kế hoạch tuyển dụng",
            field: "recruitment",
            minWidth: "150px",
            render: rowData => (
                <>
                    {rowData?.recruitmentPlan && (
                        <span className="pr-8">
              {rowData?.recruitmentPlan?.name}
            </span>
                    )}
                </>
            )
        },
        {
            title: "Ngày thi tuyển",
            field: "interviewDate",
            minWidth: "150px",
            render: (rowData) => (
                <span>
          {rowData?.interviewDate && (formatDate("DD/MM/YYYY", rowData?.interviewDate))}
        </span>
            ),
        },
        {
            title: "Trạng thái nhận việc",
            field: "status",
            align: "center",
            minWidth: "150px",
            render: function (candidate) {
                return (<span className="w-100 text-center">{LocalConstants.CandidateStatus.getNameByValue(candidate.status)}</span>);
            }
        },
        {
            title: "Đơn vị tuyển dụng",
            field: "recruitmentPlan.recruitmentRequest.organization.name",
            align: "left",
            minWidth: "150px",
        },
        {
            title: "Phòng ban tuyển dụng",
            field: "recruitmentPlan.recruitmentRequest.hrDepartment.name",
            align: "left",
            minWidth: "150px",
        },
        {
            title: "Vị trí tuyển dụng",
            field: "positionTitle.name",
            align: "left",
            minWidth: "150px",
        },
    ];

    if (mapTabToStatus(searchObject?.onboardStatus) === LocalConstants.CandidateOnboardStatus.NOT_COME.value) {
        columns.splice(columns.length - 1, 0, {
            title: "Lý do từ chối",
            field: "refusalReason",
            minWidth: "150px",
        });
    }

    return (
        <GlobitsTable
            selection
            data={listReportCandidates}
            handleSelectList={handleSelectListChosen}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={searchObject?.pageSize}
            pageSizeOption={[5, 10, 25, 50, 100]}
            totalElements={totalElements}
            page={searchObject?.pageIndex}
        />
    );
}

export default memo(observer(ExportCandidateList));
