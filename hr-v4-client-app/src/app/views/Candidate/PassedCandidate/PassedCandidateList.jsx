import React, { memo, useState } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { Icon, IconButton, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import { formatDate } from "app/LocalFunction";
import { useHistory } from "react-router-dom";
import ConstantList from "app/appConfig";
import MoreHorizIcon from "@material-ui/icons/MoreHoriz";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import LocalConstants from "app/LocalConstants";

function PassedCandidateList() {
    const { passedCandidateStore } = useStore();
    const { t } = useTranslation();

    const {
        listPassedCandidates,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleSelectListChosen,
        getReceptionStatusName,
        handleOpenRejectPopup,
        handleOpenReceptPopup,
        handleOpenResetPopup
    } = passedCandidateStore;

    const history = useHistory();

    function handleUpdateApplicant(applicant) {
        //link to new page like a staff
        history.push(ConstantList.ROOT_PATH + `candidate/` + applicant?.id + "?isFromPassedCandidate=true");
    }

    const [anchorEl, setAnchorEl] = useState();
    const [selectedRow, setSelectedRow] = useState(null);

    const columns = [
        {
            title: t("general.action"),
            width: "10%",
            minWidth: "100px",
            align: "center",
            render: (rowData) => {
                return (
                    <div className="flex flex-middle justify-center">
                        <Tooltip title="Chi tiết" placement="top">
                            <IconButton size="small" onClick={function () {
                                handleUpdateApplicant(rowData);
                            }}>
                                <Icon fontSize="small" color="primary">
                                    remove_red_eye
                                </Icon>
                            </IconButton>
                        </Tooltip>

                        <Tooltip title="Thao tác khác" placement="top">
                            <IconButton
                                className="ml-8"
                                size="small"
                                onClick={(event) => {
                                    setSelectedRow(rowData);
                                    setAnchorEl(event?.currentTarget);
                                }}
                            >
                                <MoreHorizIcon />
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
        {
            title: "Giới tính",
            field: "gender",
            minWidth: "150px",
            render: rowData => (
                <span>
                    {rowData?.gender === "M" ? "Nam" : rowData?.gender === "F" ? "Nữ" : ""}
                </span>
            )
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
            title: "Đợt tuyển dụng",
            field: "recruitment",
            minWidth: "150px",
            render: rowData => (
                <>
                    {rowData?.recruitment && (
                        <span className="pr-8">
                            {rowData?.recruitment?.name}
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
            title: "Trạng thái phân việc",
            field: "receptionStatus",
            align: "center",
            minWidth: "150px",
            render: function (candidate) {
                return (
                    <span className="w-100 text-center">{getReceptionStatusName(candidate?.receptionStatus)}</span>);
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

    function handleClosePopover() {
        setAnchorEl(null);
        setSelectedRow(null);
    }

    if (searchObject?.receptionStatus == LocalConstants.CandidateReceptionStatus.REJECTED.value) {
        columns.splice(columns.length - 1, 0, {
            title: "Lý do từ chối",
            field: "refusalReason",
            minWidth: "150px",
        });
    } else if (searchObject?.receptionStatus == LocalConstants.CandidateReceptionStatus.RECEPTED.value) {
        columns.splice(columns.length - 1, 0, {
            title: "Ngày nhận việc",
            field: "onboardDate",
            minWidth: "150px",
            render: (rowData) => (
                <span>
                    {rowData?.onboardDate && (formatDate("DD/MM/YYYY", rowData?.onboardDate))}
                </span>
            ),
        });
    }

    return (
        <>
            <GlobitsTable
                selection
                data={listPassedCandidates}
                handleSelectList={handleSelectListChosen}
                columns={columns}
                totalPages={totalPages}
                handleChangePage={handleChangePage}
                setRowsPerPage={setPageSize}
                pageSize={searchObject?.pageSize}
                pageSizeOption={[10, 25, 50]}
                totalElements={totalElements}
                page={searchObject?.pageIndex}
            />

            {Boolean(anchorEl) && (
                <Menu
                    id={"simple-menu-options"}
                    anchorEl={anchorEl}
                    keepMounted
                    open={Boolean(anchorEl)}
                    onClose={handleClosePopover}
                    className="py-0"
                >
                    {/* ĐÁNH DẤU ỨNG VIÊN LÀ ĐÃ ĐƯỢC PHÂN NGÀY NHẬN VIỆC */}
                    {selectedRow?.receptionStatus != LocalConstants.CandidateReceptionStatus.RECEPTED.value && (
                        <MenuItem className="flex items-center justify-center" onClick={function () {
                            handleSelectListChosen([JSON.parse(JSON.stringify(selectedRow))]);
                            handleOpenReceptPopup();
                            handleClosePopover();
                        }}>
                            <Icon className="pr-6" fontSize="small">
                                add_alarm
                            </Icon>

                            <span className="ml-4">
                                Chờ nhận việc
                            </span>
                        </MenuItem>
                    )}


                    {/* Đánh dấu ứng viên đã bị từ chối */}
                    {selectedRow?.receptionStatus != LocalConstants.CandidateReceptionStatus.REJECTED.value && (
                        <MenuItem className="flex items-center justify-center" onClick={function () {
                            handleSelectListChosen([JSON.parse(JSON.stringify(selectedRow))]);
                            handleOpenRejectPopup();
                            handleClosePopover();
                        }}>
                            <Icon className="pr-6" fontSize="small" style={{ color: "red" }}>
                                thumb_down
                            </Icon>

                            <span className="ml-4">
                                Từ chối
                            </span>
                        </MenuItem>
                    )}

                    {/* Cài lại trạng thái tiếp nhận */}
                    {selectedRow?.receptionStatus != LocalConstants.CandidateReceptionStatus.NOT_RECEPTED_YET.value && (
                        <MenuItem className="flex items-center justify-center" onClick={function () {
                            handleSelectListChosen([JSON.parse(JSON.stringify(selectedRow))]);
                            handleOpenResetPopup();
                            handleClosePopover();
                        }}>
                            <Icon className="pr-6" fontSize="small" style={{ color: "blue" }}>
                                loop
                            </Icon>

                            <span className="ml-4">
                                Cài lại
                            </span>
                        </MenuItem>
                    )}
                </Menu>
            )}
        </>
    );
}

export default memo(observer(PassedCandidateList));
